<?php
session_start();
$STEPS = 40;
$SECRET_KEY = trim(file_get_contents('/var/www/secret_key.txt'));

# LOGIC RESET GAME
function reset_game(){
    global $STEPS;
    $_SESSION['path'] = []; 
    $_SESSION['current_step'] = -1;
    for($i = 0; $i < $STEPS; $i++){
        $_SESSION['path'][$i] = random_int(0, 1); 
    }
    for($i = 0; $i < $STEPS; $i++){
        $_SESSION['path'][$i] = random_int(0, 1); 
    }
}

if (isset($_GET['act'])){
    if ($_GET['act'] === 'respawn'){
        reset_game();
        echo "Reset Ok Rồi Fen Ơi! Xử lí game tiếp đi nà!";
        exit;
    }
    if ($_GET['act'] === 'move'){
        $step = intval($_GET['step']);
        $side = intval($_GET['side']);
        $user_hash = (string)$_GET['h']; 
        $expected_hash = md5($SECRET_KEY . "|" . $step . "|" . $side);
        if ($user_hash !== $expected_hash) {
            die('err_signature_mismatch');
        }
        if (!isset($_SESSION['current_step'])) $_SESSION['current_step'] = -1;
        if ($step !== $_SESSION['current_step'] + 1) die('error_invalid_step');
        
        if ($_SESSION['path'][$step] === $side) {
            $_SESSION['current_step'] = $step;
            if ($step === $STEPS - 1){
                $flag = shell_exec('cat /flag-*');
                echo "You Win|".$flag; 
            } else {
                echo "ok";
            }
        } else {
            reset_game();
            die('Error: You Died!');
        }
        exit;
    }
}
?>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <title>Piano Cipher</title>
  <link rel="stylesheet" href="./style.css">

  <script>
    window.GAME_STEPS = <?php echo $STEPS; ?>;
  </script>
</head>

<body>
  <div id="overlay">
    <div class="panel">
      <div class="title">PIANO CIPHER</div>
      <div class="subtitle">2 keys • 40 beats • one correct route</div>

      <div class="howto">
        <div class="keycap">F</div>
        <div>Lane trái</div>
        <div class="sep"></div>
        <div class="keycap">J</div>
        <div>Lane phải</div>
      </div>

      <p class="desc">
        Mỗi nốt rơi tương ứng 1 bước. Đánh đúng lane để qua gate tiếp theo.<br/>
        Nếu backend bật Integrity Signing, move sai chữ ký sẽ bị chặn.
      </p>

      <button id="startBtn">START</button>
      <div class="tiny">Tip: dùng tai nghe cho “đã” hơn 🎧</div>
    </div>
  </div>

  <div id="death-screen" style="display:none;">
    <div class="panel danger">
      <div class="title">MISS!</div>
      <p class="desc">Bạn đánh sai hoặc quá trễ rồi.</p>
      <button onclick="window.respawn()">RESPAWN</button>
    </div>
  </div>

  <div id="win-screen" style="display:none;">
    <div class="panel win">
      <div class="title">CLEAR!</div>
      <p id="flag-display"></p>
      <button onclick="location.reload()">PLAY AGAIN</button>
    </div>
  </div>
  <div id="game">
    <canvas id="piano"></canvas>

    <div id="ui">
      <div class="hud">
        <div class="chip">STEP <span id="step">0</span> / <?php echo $STEPS; ?></div>
        <div class="chip msg"><span id="msg"></span></div>
        <div class="chip right">SCORE <span id="score">0</span></div>
      </div>
    </div>
  </div>
  <script type="module" src="./piano_game.js"></script>
</body>
</html>


