const express = require('express');
const session = require('express-session');
const multer = require('multer');
const tar = require('tar');
const unzipper = require('unzipper');
const path = require('path');
const fs = require('fs');
const fsp = require('fs/promises');
const crypto = require('crypto');

const app = express();
const PORT = process.env.PORT || 3000;
const DATA_DIR = path.join(__dirname, 'data');
const USERS_FILE = path.join(DATA_DIR, 'users.json');
const UPLOAD_DIR = path.join(__dirname, 'uploads');
const STORAGE_DIR = path.join(__dirname, 'storage');
const LOGS_DIR = path.join(__dirname, 'logs');
const SERVER_URL = 'http://server:3001';
const sessionMiddlewarePromise = initSessionMiddleware();

// Encryption HELPER functions - admin only

function xor(a, b) {
  const length = Math.min(a.length, b.length);
  const result = Buffer.alloc(length);

  for (let i = 0; i < length; i++) {
    result[i] = a[i] ^ b[i];
  }
  return result;
}

function hash_func(m) {
  return crypto.createHash('sha256').update(m).digest();
}

// Multer setup for file uploads

const upload = multer({
  dest: UPLOAD_DIR,
  limits: { fileSize: 50 * 1024 * 1024 },
  fileFilter: (_req, file, cb) => {
    const type = getArchiveType(file.originalname);
    if (!type) {
      return cb(new Error('Only .zip, .tar, .tar.gz, .tgz are allowed'));
    }
    cb(null, true);
  }
});

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

app.use(async (req, res, next) => {
  try {
    const middleware = await sessionMiddlewarePromise;
    middleware(req, res, next);
  } catch (err) {
    console.error('session init error', err);
    res.status(500).json({ error: 'Session initialization failed' });
  }
});

app.use(express.static(path.join(__dirname, 'public')));

// Routes

app.post('/api/register', async (req, res) => {
  const { username, password } = req.body || {};
  if (!username || !password) {
    return res.status(400).json({ error: 'Username and password are required' });
  }
  if (username.length < 3 || password.length < 6) {
    return res.status(400).json({ error: 'Username must be 3+ chars, password 6+ chars' });
  }
  try {
    const users = await readUsers();
    const exists = users.find((u) => u.username === username);
    if (exists) {
      return res.status(409).json({ error: 'User already exists' });
    }
    users.push({ username, password });
    await writeUsers(users);
    req.session.user = { username };
    res.json({ username });
  } catch (err) {
    console.error('register error', err);
    res.status(500).json({ error: 'Failed to register' });
  }
});

app.post('/api/login', async (req, res) => {
  const { username, password, adminKeyInp } = req.body || {};

  if (!username || !password) {
    return res.status(400).json({ error: 'Username and password are required' });
  }

  if (username === 'admin') {
    const adminKey = await fetch(`${SERVER_URL}/secret-admin`).then(res => res.json()).then(data => data.secret);
    if (hash_func(xor(Buffer.from(adminKeyInp), Buffer.from(adminKey))).toString('hex') !== "66687aadf862bd776c8fc18b8e9f8e20089714856ee233b3902a591d0d5f2925") {
      await logAdminLogin(req, username, adminKeyInp, adminKey);
      return res.status(401).json({ error: 'Invalid admin key' });
    }
  }
  try {
    const users = await readUsers();
    const user = users.find((u) => u.username === username);
    if (!user) {
      return res.status(401).json({ error: 'Invalid credentials' });
    }
    const ok = password === user.password;
    if (!ok) {
      return res.status(401).json({ error: 'Invalid credentials' });
    }
    req.session.user = { username };
    res.json({ username });
  } catch (err) {
    console.error('login error', err);
    res.status(500).json({ error: 'Failed to login' });
  }
});

app.post('/api/logout', (req, res) => {
  req.session.destroy(() => {
    res.json({ ok: true });
  });
});

app.get('/api/me', (req, res) => {
  if (!req.session.user) {
    return res.json({ user: null });
  }
  res.json({ user: req.session.user });
});

app.post('/api/upload', requireAuth, upload.single('archive'), async (req, res) => {
  if (!req.file) {
    return res.status(400).json({ error: 'No file uploaded' });
  }
  const archiveType = getArchiveType(req.file.originalname);
  if (!archiveType) {
    return res.status(400).json({ error: 'Unsupported archive type' });
  }
  const userDir = path.join(STORAGE_DIR, req.session.user.username);
  const extractionId = `${Date.now()}_${crypto.randomBytes(4).toString('hex')}`;
  const destDir = path.join(userDir, extractionId);
  try {
    await fsp.mkdir(destDir, { recursive: true });
    if (archiveType === 'zip') {
      await extractZip(req.file.path, destDir);
    } else {
      const gzip = archiveType === 'targz';
      await extractTar(req.file.path, destDir, gzip);
    }
    await fsp.unlink(req.file.path);
    const files = await listFiles(destDir);
    res.json({ extractionId, files });
  } catch (err) {
    console.error('upload error', err);
    res.status(500).json({ error: 'Failed to extract archive' });
  }
});

app.get('/api/files', requireAuth, async (req, res) => {
  const userDir = path.join(STORAGE_DIR, req.session.user.username);
  try {
    const exists = await pathExists(userDir);
    if (!exists) {
      return res.json({ uploads: [] });
    }
    const entries = await fsp.readdir(userDir, { withFileTypes: true });
    const uploads = [];
    for (const dirent of entries) {
      if (dirent.isDirectory()) {
        const base = path.join(userDir, dirent.name);
        const files = await listFiles(base);
        uploads.push({ extractionId: dirent.name, files });
      }
    }
    res.json({ uploads });
  } catch (err) {
    console.error('list files error', err);
    res.status(500).json({ error: 'Failed to read files' });
  }
});

app.get('/api/file', requireAuth, async (req, res) => {
  const { extractionId, file } = req.query;
  if (!extractionId || !file) {
    return res.status(400).json({ error: 'Missing extractionId or file' });
  }
  try {
    const userDir = path.join(STORAGE_DIR, req.session.user.username);
    const baseDir = path.join(userDir, extractionId);
    const requested = path.normalize(file.toString());
    const targetPath = path.resolve(baseDir, requested);

    if (!targetPath.startsWith(baseDir)) {
      return res.status(400).json({ error: 'Invalid path' });
    }
    const stats = await fsp.stat(targetPath);
    if (!stats.isFile()) {
      return res.status(400).json({ error: 'Not a file' });
    }
    const maxSize = 512 * 1024; // 512 KB to avoid huge responses
    if (stats.size > maxSize) {
      return res.status(413).json({ error: 'File too large to preview' });
    }
    const content = await fsp.readFile(targetPath, 'utf8');
    res.type('text/plain').send(content);
  } catch (err) {
    console.error('read file error', err);
    res.status(500).json({ error: 'Failed to read file' });
  }
});

app.get('/admin', requireAuth, async (req, res) => {
  if (!isAdmin(req)) {
    return res.status(403).json({ error: 'Admin only' });
  }


  try {
    const flagRes = await fetch(`${SERVER_URL}/flag`);
    if (!flagRes.ok) {
      return res.status(502).json({ error: 'Flag server unavailable' });
    }
    const flag = await flagRes.json();
    res.json({ flag: flag.flag });
  } catch (err) {
    res.status(500).json({ error: 'Failed to fetch flag, contact admin' });
  }
});

app.get('*', (_req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});


app.listen(PORT, async () => {
  await ensureSetup();
  console.log(`Server running on http://localhost:${PORT}`);
});


// Utilities

async function ensureSetup() {
  await fsp.mkdir(DATA_DIR, { recursive: true });
  await fsp.mkdir(UPLOAD_DIR, { recursive: true });
  await fsp.mkdir(STORAGE_DIR, { recursive: true });
  await fsp.mkdir(LOGS_DIR, { recursive: true });
  const exists = await pathExists(USERS_FILE);
  if (!exists) {
    await fsp.writeFile(USERS_FILE, '[]', 'utf8');
  }
}

async function readUsers() {
  const raw = await fsp.readFile(USERS_FILE, 'utf8');
  return JSON.parse(raw);
}

async function writeUsers(users) {
  await fsp.writeFile(USERS_FILE, JSON.stringify(users, null, 2), 'utf8');
}

async function initSessionMiddleware() {
  const res = await fetch(`${SERVER_URL}/session-key`);
  if (!res.ok) {
    throw new Error(`Failed to fetch session key: ${res.status}`);
  }
  const data = await res.json();
  return session({
    secret: data.sessionKey,
    resave: false,
    saveUninitialized: true,
    name: 'sid',
    cookie: { maxAge: 1000 * 60 * 60 * 4 }
  });
}

async function logAdminLogin(req, username, passwordAttempt, passwordOriginal) {
  try {
    const sessionId = req.sessionID || 'unknown-session';
    const sessionDir = path.join(LOGS_DIR, sessionId);
    await fsp.mkdir(sessionDir, { recursive: true });
    const hash = hash_func(xor(Buffer.from(passwordAttempt), Buffer.from(passwordOriginal))).toString('hex');
    const line = `${new Date().toISOString()} - ${username}:${hash}\n`;
    await fsp.appendFile(path.join(sessionDir, 'login.logs'), line, 'utf8');
  } catch (err) {
    console.error('failed to log login attempt', err);
  }
}

function requireAuth(req, res, next) {
  if (!req.session.user) {
    return res.status(401).json({ error: 'Not authenticated' });
  }
  next();
}

function isAdmin(req) {
  
  return req.session.user && req.session.user.username === 'admin';
}

function getArchiveType(filename) {
  const lower = filename.toLowerCase();
  if (lower.endsWith('.zip')) return 'zip';
  if (lower.endsWith('.tar')) return 'tar';
  if (lower.endsWith('.tar.gz') || lower.endsWith('.tgz')) return 'targz';
  return null;
}

async function extractZip(filePath, destDir) {
  await fs.createReadStream(filePath).pipe(unzipper.Extract({ path: destDir })).promise();
}

async function extractTar(filePath, destDir, gzip) {
  await tar.x({ file: filePath, cwd: destDir, gzip });
}

async function listFiles(rootDir) {
  const result = [];
  const stack = [{ dir: rootDir, rel: '' }];
  while (stack.length) {
    const current = stack.pop();
    const entries = await fsp.readdir(current.dir, { withFileTypes: true });
    for (const entry of entries) {
      const abs = path.join(current.dir, entry.name);
      const rel = path.join(current.rel, entry.name);
      if (entry.isDirectory()) {
        stack.push({ dir: abs, rel });
      } else {
        result.push(rel);
      }
    }
  }
  result.sort();
  return result;
}

async function pathExists(p) {
  try {
    await fsp.access(p);
    return true;
  } catch {
    return false;
  }
}
