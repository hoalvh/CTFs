const express = require('express');
const randomBytes = require('crypto').randomBytes;
const app = express();
const PORT = process.env.PORT || 3001;
const FLAG = process.env.FLAG || 'VSL{fake_flag}';
const SESSION_SECRET = process.env.SESSION_SECRET || 'change-me';
const ADMIN_SECRET = randomBytes(16).toString('hex');

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

app.get('/flag', (req, res) => {
    res.send({ flag: FLAG });
});

app.get('/session-key', (req, res) => {
    res.json({ sessionKey: SESSION_SECRET });
});

app.get('/secret-admin', (req, res) => {
    res.json({ secret: ADMIN_SECRET });
});

app.listen(PORT, async () => {
    await ensureSetup();
    console.log("Admin secret:", ADMIN_SECRET);
    console.log(`Server running on http://localhost:${PORT}`);
});

async function ensureSetup() {
    return Promise.resolve();
}