const canvas = document.getElementById('piano');
const ctx = canvas.getContext('2d');

const overlay = document.getElementById('overlay');
const deathScreen = document.getElementById('death-screen');
const winScreen = document.getElementById('win-screen');
const flagDisplay = document.getElementById('flag-display');

const stepEl = document.getElementById('step');
const msgEl = document.getElementById('msg');
const scoreEl = document.getElementById('score');
const startBtn = document.getElementById('startBtn');

const TOTAL = window.GAME_STEPS || 40;

let W = 0, H = 0;
function resize(){
  W = canvas.width = window.innerWidth;
  H = canvas.height = window.innerHeight;
}
window.addEventListener('resize', resize);
resize();

// ---- Audio (simple synth) ----
let audioCtx = null;
function beep(freq=440, ms=80, type='sine', gain=0.04){
  if (!audioCtx) return;
  const o = audioCtx.createOscillator();
  const g = audioCtx.createGain();
  o.type = type;
  o.frequency.value = freq;
  g.gain.value = gain;
  o.connect(g);
  g.connect(audioCtx.destination);
  o.start();
  o.stop(audioCtx.currentTime + ms/1000);
}
function chord(ok=true){
  if (!audioCtx) return;
  if (ok){
    beep(523.25, 90, 'triangle', 0.05); // C5
    setTimeout(()=>beep(659.25, 90, 'triangle', 0.04), 40); // E5
  } else {
    beep(196.0, 120, 'sawtooth', 0.05);
    setTimeout(()=>beep(155.6, 120, 'sawtooth', 0.05), 40);
  }
}

// ---- Game state ----
let running = false;
let isDead = false;
let isWon = false;

let currentStep = -1;       // step đã qua (server ok)
let score = 0;

const laneX = () => [W * 0.33, W * 0.67];
const laneW = () => W * 0.34;
const judgeY = () => H * 0.82;

const noteSpeed = () => Math.max(380, H * 0.55); // px/s
const noteHeight = () => Math.max(110, H * 0.14);

let notes = []; // each: {step, side, y, hit, resolved}
let spawnIndex = 0;

// spacing so notes come one by one
function spawnNext(){
  if (spawnIndex >= TOTAL) return;
  const step = spawnIndex;
  // (visual only) random side for appearance; real correctness server decides
  const side = Math.random() < 0.5 ? 0 : 1;
  const y = -noteHeight() - 20;
  notes.push({ step, side, y, hit:false, resolved:false });
  spawnIndex++;
}

// ---- Backend hooks (giữ y chang) ----
async function serverRespawn(){
  await fetch('?act=respawn');
}

async function serverMove(step, side){
  // giữ placeholder h như bạn đang làm (CTF exploit sẽ tự ký đúng)
  const res = await fetch(`?act=move&step=${step}&side=${side}&h=missing_or_invalid_signature`).then(r => r.text());
  return res;
}

// ---- UI screens ----
function showDeath(reason){
  running = false;
  isDead = true;
  msgEl.innerText = reason || 'MISS!';
  chord(false);
  setTimeout(()=>deathScreen.style.display='flex', 250);
}

function showWin(flag){
  running = false;
  isWon = true;
  chord(true);
  flagDisplay.innerText = flag;
  winScreen.style.display = 'flex';
}

// ---- Input ----
function laneFromKey(key){
  if (key === 'f') return 0;
  if (key === 'j') return 1;
  return null;
}

async function tryHit(side){
  if (!running || isDead || isWon) return;

  // find closest note in judge window for this side
  const jy = judgeY();
  const windowPx = noteHeight() * 0.55;
  let best = null;
  let bestDist = 1e9;

  for (const n of notes){
    if (n.resolved) continue;
    if (n.side !== side) continue;
    const centerY = n.y + noteHeight()/2;
    const d = Math.abs(centerY - jy);
    if (d < bestDist){
      bestDist = d;
      best = n;
    }
  }

  if (!best || bestDist > windowPx){
    // đánh hụt
    showDeath('MISS (timing)');
    return;
  }

  best.hit = true;
  // request server with step+side
  try{
    const res = await serverMove(best.step, side);

    if (res.startsWith('ok')){
      currentStep = best.step;
      stepEl.innerText = String(currentStep + 1);
      score += 100;
      scoreEl.innerText = String(score);
      msgEl.innerText = '';
      chord(true);

      best.resolved = true;

      if (res.includes('|VSL{')){
        showWin(res.split('|')[1]);
      }
    } else if (res === 'dead'){
      best.resolved = true;
      showDeath('WRONG NOTE!');
    } else if (res === 'err_signature_mismatch'){
      best.resolved = true;
      showDeath('SIGNATURE MISMATCH');
    } else {
      showDeath('UNKNOWN RESPONSE');
    }
  } catch(e){
    console.error(e);
    showDeath('NETWORK ERROR');
  }
}

window.addEventListener('keydown', (e)=>{
  if (!audioCtx) {
    audioCtx = new (window.AudioContext || window.webkitAudioContext)();
  }
  const side = laneFromKey(e.key.toLowerCase());
  if (side === null) return;
  tryHit(side);
});

// tap/click support
canvas.addEventListener('pointerdown', (e)=>{
  if (!audioCtx) audioCtx = new (window.AudioContext || window.webkitAudioContext)();
  const x = e.clientX;
  const [L, R] = laneX();
  const side = x < (L+R)/2 ? 0 : 1;
  tryHit(side);
});

// ---- Drawing ----
function roundRect(x,y,w,h,r){
  ctx.beginPath();
  ctx.moveTo(x+r,y);
  ctx.arcTo(x+w,y,x+w,y+h,r);
  ctx.arcTo(x+w,y+h,x,y+h,r);
  ctx.arcTo(x,y+h,x,y,r);
  ctx.arcTo(x,y,x+w,y,r);
  ctx.closePath();
}

function draw(){
  // bg
  ctx.clearRect(0,0,W,H);
  const grad = ctx.createLinearGradient(0,0,0,H);
  grad.addColorStop(0,'rgba(20,22,50,0.75)');
  grad.addColorStop(1,'rgba(5,5,12,0.95)');
  ctx.fillStyle = grad;
  ctx.fillRect(0,0,W,H);

  // lanes
  const lw = laneW();
  const x0 = W*0.16;
  const x1 = W - x0 - lw;

  ctx.fillStyle = 'rgba(255,255,255,0.04)';
  roundRect(x0, 70, lw, H-140, 18); ctx.fill();
  roundRect(x1, 70, lw, H-140, 18); ctx.fill();

  // lane borders
  ctx.strokeStyle = 'rgba(60,251,255,0.12)';
  ctx.lineWidth = 2;
  roundRect(x0, 70, lw, H-140, 18); ctx.stroke();
  roundRect(x1, 70, lw, H-140, 18); ctx.stroke();

  // judge line
  const jy = judgeY();
  ctx.strokeStyle = 'rgba(255,43,214,0.35)';
  ctx.lineWidth = 3;
  ctx.beginPath();
  ctx.moveTo(x0, jy);
  ctx.lineTo(x0+lw, jy);
  ctx.moveTo(x1, jy);
  ctx.lineTo(x1+lw, jy);
  ctx.stroke();

  // notes
  for (const n of notes){
    if (n.resolved && n.y > H + 200) continue;

    const laneXpos = n.side === 0 ? x0 : x1;
    const y = n.y;
    const h = noteHeight();

    const okGlow = n.hit ? 'rgba(0,255,136,0.25)' : 'rgba(60,251,255,0.20)';
    const fill = n.hit ? 'rgba(0,255,136,0.20)' : 'rgba(60,251,255,0.12)';
    const stroke = n.hit ? 'rgba(0,255,136,0.55)' : 'rgba(60,251,255,0.40)';

    // glow
    ctx.fillStyle = okGlow;
    roundRect(laneXpos+10, y-6, lw-20, h+12, 16);
    ctx.fill();

    // body
    ctx.fillStyle = fill;
    roundRect(laneXpos+16, y, lw-32, h, 14);
    ctx.fill();

    ctx.strokeStyle = stroke;
    ctx.lineWidth = 2;
    roundRect(laneXpos+16, y, lw-32, h, 14);
    ctx.stroke();

    // label (step)
    ctx.fillStyle = 'rgba(255,255,255,0.85)';
    ctx.font = `700 ${Math.max(14, Math.floor(h*0.18))}px ui-monospace, Menlo, Consolas`;
    ctx.fillText(`#${n.step+1}`, laneXpos+28, y + h*0.58);
  }

  // lane key hints near bottom
  ctx.fillStyle = 'rgba(255,255,255,0.55)';
  ctx.font = '900 16px ui-sans-serif, system-ui';
  ctx.fillText('F', x0 + 18, H - 26);
  ctx.fillText('J', x1 + 18, H - 26);
}

let lastT = performance.now();
function tick(t){
  const dt = Math.min(0.05, (t - lastT)/1000);
  lastT = t;

  if (running && !isDead && !isWon){
    // spawn pacing: 1 note every ~0.62s
    if (notes.length === 0 || (notes[notes.length-1].y > noteHeight()*0.35)) {
      if (spawnIndex < TOTAL) spawnNext();
    }

    // move notes down
    const spd = noteSpeed();
    for (const n of notes){
      n.y += spd * dt;
      // miss if passed judge too far without resolved
      if (!n.resolved && n.y > judgeY() + noteHeight()*0.65){
        showDeath('MISS (late)');
      }
    }
  }

  draw();
  requestAnimationFrame(tick);
}

// ---- Start / Respawn ----
async function startGame(){
  // audio init requires gesture
  if (!audioCtx) audioCtx = new (window.AudioContext || window.webkitAudioContext)();
  overlay.style.display = 'none';
  deathScreen.style.display = 'none';
  winScreen.style.display = 'none';

  isDead = false; isWon = false;
  running = true;

  currentStep = -1;
  score = 0;
  scoreEl.innerText = '0';
  stepEl.innerText = '0';
  msgEl.innerText = '';

  notes = [];
  spawnIndex = 0;

  await serverRespawn();
  beep(440, 60, 'triangle', 0.05);
}

window.respawn = startGame;
startBtn?.addEventListener('click', startGame);

// start render loop
requestAnimationFrame(tick);
