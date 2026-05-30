const express = require('express');
const session = require('express-session');
const crypto = require('crypto');
const path = require('path');
const { visit } = require('./bot');

const app = express();
const PORT = 3000;
const FLAG = process.env.GZCTF_FLAG || 'BKISC{fake_flag_for_testing}';
const ADMIN_PASS = process.env.ADMIN_PASS || 'REDACTED';

app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));
app.use(express.static(path.join(__dirname, 'public')));
app.use(express.urlencoded({ extended: false }));
app.use(express.json());

app.use(session({
    secret: crypto.randomBytes(32).toString('hex'),
    resave: false,
    saveUninitialized: false,
    cookie: { httpOnly: true, sameSite: 'lax', path: '/' }
}));

app.use((req, res, next) => {
    res.locals.flash = req.session.flash || null;
    delete req.session.flash;
    res.locals.user = req.session.userId || null;
    next();
});

const notes = new Map();

function escapeHtml(s) {
    return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;')
                    .replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

function renderPage(req, res, view, data = {}) {
    const app = req.app;
    data.user = req.session.userId || null;
    data.flash = res.locals.flash;
    app.render(view, data, (err, inner) => {
        if (err) return res.status(500).send('View error: ' + err.message);
        data.content = inner;
        res.render('layout', data);
    });
}

app.get('/', (req, res) => {
    const userNotes = [];
    if (req.session.userId) {
        for (const [id, note] of notes) {
            if (note.owner === req.session.userId) {
                userNotes.push({ id, title: note.title, shared: note.shared });
            }
        }
    }
    renderPage(req, res, 'home', { active: 'home', pageTitle: 'Home', notes: userNotes });
});

app.get('/new', (req, res) => {
    if (!req.session.userId) {
        req.session.flash = { type: 'error', message: 'Sign in to create notes' };
        return res.redirect('/login');
    }
    renderPage(req, res, 'new', { active: 'new', pageTitle: 'New Note' });
});

app.get('/note/:id', (req, res) => {
    const note = notes.get(req.params.id);
    if (!note) return res.status(404).send('Not found');

    const isConditional = !!req.headers['if-none-match'];

    if (!isConditional) {
        note.lastFreshView = Date.now();
    }

    const shareAfterLastView = note.shareTime && note.lastFreshView &&
                                note.shareTime > note.lastFreshView;

    if (note.shared && isConditional && shareAfterLastView) {
        res.setHeader('Content-Security-Policy',
            "default-src * 'unsafe-inline'; script-src 'unsafe-inline' *; connect-src *; img-src *");
        res.setHeader('Cross-Origin-Resource-Policy', 'cross-origin');
    } else {
        const nonce = crypto.randomBytes(16).toString('base64');
        res.setHeader('Content-Security-Policy',
            `default-src 'self'; script-src 'nonce-${nonce}'`);
        res.setHeader('Cross-Origin-Resource-Policy', 'same-origin');
    }

    res.setHeader('Content-Type', 'text/html; charset=utf-8');
    res.setHeader('Cache-Control', 'no-cache');

    const isOwner = req.session.userId === note.owner;

    res.send(`<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${escapeHtml(note.title)} | SecureNotes</title>
    <link rel="stylesheet" href="/style.css">
</head>
<body>
    <div class="container">
        <h1>Secure<span>Notes</span></h1>
        <p class="subtitle">End-to-end protected note sharing</p>
        <nav>
            <a href="/">My Notes</a>
            <a href="/new">New Note</a>
            <a href="/report">Report</a>
        </nav>
        <div class="note-view">
            <h1>${escapeHtml(note.title)}</h1>
            <div class="note-content">${note.content}</div>
        </div>
        ${isOwner ? `
        <div class="actions" style="margin-top:1rem">
            <form method="POST" action="/api/note/${req.params.id}/share" style="display:inline">
                <button type="submit" class="btn btn-sm ${note.shared ? 'btn-danger' : 'btn-outline'}">
                    ${note.shared ? 'Unshare' : 'Share'}
                </button>
            </form>
            <span style="color:#555; font-size:0.8rem; line-height:2.2">
                ${note.shared ? 'Shared Mode' : 'Private Mode'}
            </span>
        </div>` : ''}
    </div>
</body>
</html>`);
});

app.post('/api/note', (req, res) => {
    if (!req.session.userId) return res.status(401).send('Auth required');
    const id = crypto.randomBytes(8).toString('hex');
    const title = String(req.body.title || 'Untitled').slice(0, 200);
    let content = String(req.body.content || '');
    if (content.length > 10000) {
        req.session.flash = { type: 'error', message: 'Content too large (max 10KB)' };
        return res.redirect('/new');
    }
    content = content.replace(/<meta[\s>]/gi, '&lt;meta ');
    notes.set(id, { owner: req.session.userId, title, content, shared: false });
    if (req.headers['content-type']?.includes('application/json')) {
        res.json({ id });
    } else {
        req.session.flash = { type: 'success', message: 'Note created' };
        res.redirect('/note/' + id);
    }
});

app.post('/api/note/:id/share', (req, res) => {
    if (!req.session.userId) return res.status(401).send('Auth required');
    const note = notes.get(req.params.id);
    if (!note) return res.status(404).send('Not found');
    if (note.owner !== req.session.userId) return res.status(403).send('Not owner');
    note.shared = !note.shared;
    if (note.shared) {
        note.shareTime = Date.now();
    } else {
        delete note.shareTime;
    }
    if (req.headers['content-type']?.includes('application/json')) {
        res.json({ shared: note.shared });
    } else {
        req.session.flash = { type: 'success', message: note.shared ? 'Note shared' : 'Note unshared' };
        res.redirect('/note/' + req.params.id);
    }
});

app.get('/api/admin/data', (req, res) => {
    if (!req.session.isAdmin) return res.status(403).json({ error: 'Admin only' });
    res.json({ flag: FLAG });
});

app.get('/login', (req, res) => {
    renderPage(req, res, 'login', { active: 'login', pageTitle: 'Login' });
});

app.post('/login', (req, res) => {
    const { username, password } = req.body;
    if (username === 'admin' && password === ADMIN_PASS) {
        req.session.userId = 'admin';
        req.session.isAdmin = true;
    } else if (username) {
        req.session.userId = username;
        req.session.isAdmin = false;
    }
    req.session.flash = { type: 'success', message: `Signed in as ${req.session.userId}` };
    res.redirect('/');
});

app.get('/register', (req, res) => {
    const username = crypto.randomBytes(4).toString('hex');
    req.session.userId = username;
    req.session.isAdmin = false;
    req.session.flash = { type: 'success', message: `Account created: ${username}` };
    res.redirect('/');
});

app.post('/register', (req, res) => {
    const username = crypto.randomBytes(4).toString('hex');
    req.session.userId = username;
    req.session.isAdmin = false;
    res.json({ username });
});

app.get('/logout', (req, res) => {
    req.session.destroy();
    res.redirect('/');
});

app.get('/report', (req, res) => {
    renderPage(req, res, 'report', { active: 'report', pageTitle: 'Report' });
});

const reportCooldown = new Map();
app.post('/report', (req, res) => {
    const url = req.body.url || '';
    if (!url.startsWith('http://') && !url.startsWith('https://')) {
        req.session.flash = { type: 'error', message: 'URL must start with http:// or https://' };
        return res.redirect('/report');
    }
    const ip = req.ip;
    const now = Date.now();
    if (reportCooldown.has(ip) && now - reportCooldown.get(ip) < 10000) {
        req.session.flash = { type: 'error', message: 'Rate limited. Wait 10 seconds.' };
        return res.redirect('/report');
    }
    reportCooldown.set(ip, now);
    console.log(`[report] ${url}`);
    visit(url).catch(e => console.error('[report]', e.message));
    req.session.flash = { type: 'success', message: 'URL submitted. Admin will review shortly.' };
    res.redirect('/report');
});

app.listen(PORT, () => console.log(`SecureNotes on :${PORT}`));
