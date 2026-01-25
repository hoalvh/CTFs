const loginForm = document.getElementById('loginForm');
const registerForm = document.getElementById('registerForm');
const loginError = document.getElementById('loginError');
const registerError = document.getElementById('registerError');
const tabs = document.querySelectorAll('.tab');
const authCard = document.getElementById('authCard');
const appCard = document.getElementById('appCard');
const uploadForm = document.getElementById('uploadForm');
const uploadStatus = document.getElementById('uploadStatus');
const filesContainer = document.getElementById('filesContainer');
const userChip = document.getElementById('userChip');
const fileViewer = document.getElementById('fileViewer');
const viewerContent = document.getElementById('viewerContent');
const viewerTitle = document.getElementById('viewerTitle');
const closeViewer = document.getElementById('closeViewer');
const loginUsernameInput = loginForm.querySelector('input[name="username"]');
const adminKeyRow = document.getElementById('adminKeyRow');
const adminKeyInput = document.getElementById('adminKeyInput');

switchTabs();
attachForms();
bootstrap();

function switchTabs() {
  tabs.forEach((tab) => {
    tab.addEventListener('click', () => {
      tabs.forEach((t) => t.classList.remove('active'));
      tab.classList.add('active');
      const target = tab.dataset.target;
      document.querySelectorAll('.form').forEach((form) => {
        form.classList.toggle('active', form.id === `${target}Form`);
      });
    });
  });

  filesContainer.addEventListener('click', async (e) => {
    const btn = e.target.closest('.view-btn');
    if (!btn) return;
    const extractionId = btn.dataset.extraction;
    const file = btn.dataset.file;
    await openFile(extractionId, file, btn);
  });

  closeViewer.addEventListener('click', () => {
    fileViewer.classList.add('hidden');
    viewerContent.textContent = '';
    viewerTitle.textContent = 'Select a file to view';
  });
}

function attachForms() {
  loginUsernameInput.addEventListener('input', toggleAdminKey);
  toggleAdminKey();

  loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    loginError.textContent = '';
    const formData = new FormData(loginForm);
    const payload = Object.fromEntries(formData.entries());
    if ((payload.username || '').trim() !== 'admin') {
      delete payload.adminKeyInp;
      adminKeyInput.value = '';
    }
    const res = await postJSON('/api/login', payload, loginError);
    if (res) {
      await onAuthenticated(res.username);
    }
  });

  registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    registerError.textContent = '';
    const formData = new FormData(registerForm);
    const payload = Object.fromEntries(formData.entries());
    const res = await postJSON('/api/register', payload, registerError);
    if (res) {
      await onAuthenticated(res.username);
    }
  });

  uploadForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    uploadStatus.textContent = 'Uploading and extracting...';
    const body = new FormData(uploadForm);
    try {
      const res = await fetch('/api/upload', { method: 'POST', body });
      const data = await res.json();
      if (!res.ok) {
        throw new Error(data.error || 'Upload failed');
      }
      uploadStatus.textContent = 'Success!';
      uploadForm.reset();
      await loadFiles();
    } catch (err) {
      uploadStatus.textContent = err.message;
    }
  });
}

async function bootstrap() {
  try {
    const res = await fetch('/api/me');
    const data = await res.json();
    if (data.user) {
      await onAuthenticated(data.user.username);
    }
  } catch (err) {
    console.error(err);
  }
}

async function onAuthenticated(username) {
  userChip.innerHTML = `<span>${username}</span> <button id="logoutBtn">Logout</button>`;
  document.getElementById('logoutBtn').addEventListener('click', async () => {
    await fetch('/api/logout', { method: 'POST' });
    authCard.classList.remove('hidden');
    appCard.classList.add('hidden');
    userChip.textContent = '';
  });
  authCard.classList.add('hidden');
  appCard.classList.remove('hidden');
  await loadFiles();
}

async function loadFiles() {
  filesContainer.innerHTML = 'Loading list...';
  try {
    const res = await fetch('/api/files');
    const data = await res.json();
    if (!res.ok) {
      throw new Error(data.error || 'Failed to load list');
    }
    renderFiles(data.uploads || []);
  } catch (err) {
    filesContainer.textContent = err.message;
  }
}

function renderFiles(uploads) {
  if (!uploads.length) {
    filesContainer.textContent = 'No files yet.';
    return;
  }
  filesContainer.innerHTML = '';
  uploads
    .sort((a, b) => (a.extractionId < b.extractionId ? 1 : -1))
    .forEach((upload) => {
      const block = document.createElement('div');
      block.className = 'file-block';
      const ul = document.createElement('ul');
      ul.className = 'file-list';
      upload.files.forEach((f) => {
        const li = document.createElement('li');
        const nameSpan = document.createElement('span');
        nameSpan.textContent = f;
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.textContent = 'View';
        btn.className = 'view-btn';
        btn.dataset.extraction = upload.extractionId;
        btn.dataset.file = f;
        li.appendChild(nameSpan);
        li.appendChild(btn);
        ul.appendChild(li);
      });
      const title = document.createElement('h3');
      title.textContent = upload.extractionId;
      block.appendChild(title);
      block.appendChild(ul);
      filesContainer.appendChild(block);
    });
}

async function openFile(extractionId, file, btn) {
  btn.disabled = true;
  btn.textContent = 'Opening...';
  try {
    const params = new URLSearchParams({ extractionId, file });
    const res = await fetch(`/api/file?${params.toString()}`);
    const text = await res.text();
    if (!res.ok) {
      throw new Error(text || 'Failed to read file');
    }
    viewerTitle.textContent = `${extractionId} / ${file}`;
    viewerContent.textContent = text;
    fileViewer.classList.remove('hidden');
  } catch (err) {
    alert(err.message);
  } finally {
    btn.disabled = false;
    btn.textContent = 'View';
  }
}

async function postJSON(url, payload, errorEl) {
  try {
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    const data = await res.json();
    if (!res.ok) {
      throw new Error(data.error || 'An error occurred');
    }
    return data;
  } catch (err) {
    if (errorEl) errorEl.textContent = err.message;
    return null;
  }
}

function toggleAdminKey() {
  const isAdmin = (loginUsernameInput.value || '').trim() === 'admin';
  adminKeyRow.classList.toggle('hidden', !isAdmin);
  if (!isAdmin) {
    adminKeyInput.value = '';
  }
}
