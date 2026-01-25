import requests
import hashlib
import posixpath
from typing import List

# ================= CẤU HÌNH =================
BASE_URL = "http://124.197.22.141:6664"
Q = int("73eda753299d7d483339d80809a1d80553bda402fffe5bfeffffffff00000001", 16)

# ================= HÀM HỖ TRỢ TỪ SOURCE =================
def prime_mix(component: str, depth: int) -> int:
    base = hashlib.sha256(f"{depth}:{component}".encode()).digest()
    state = int.from_bytes(base, "big") % Q
    for round_idx in range(3):
        state = pow((state + 7 * (round_idx + 1)) % Q, 5, Q)
        state = (state * 3 + 11 * depth + round_idx) % Q
    return state

def stream_xor(key_int: int, data: bytes) -> bytes:
    key_bytes = hashlib.sha256(str(key_int).encode()).digest()
    keystream = bytearray()
    counter = 0
    while len(keystream) < len(data):
        block = hashlib.sha256(key_bytes + counter.to_bytes(4, "big")).digest()
        keystream.extend(block)
        counter += 1
    return bytes([d ^ k for d, k in zip(data, keystream)])

def normalize_identity(raw_identity: str) -> str:
    normed = posixpath.normpath(raw_identity)
    return normed.replace("\\", "/")

# ================= EXPLOIT LOGIC =================

def get_nonce():
    r = requests.get(f"{BASE_URL}/api/nonce")
    return r.json()['nonce']

def get_ciphertext():
    r = requests.get(f"{BASE_URL}/api/ciphertext")
    return bytes.fromhex(r.json()['ciphertext_hex'])

def get_key_from_oracle(raw_identity):
    nonce = get_nonce()
    # Auth token phải tính dựa trên identity đã normalize
    norm_id = normalize_identity(raw_identity)
    
    enc = f"{norm_id}|{nonce}".encode()
    token = hashlib.sha256(enc).hexdigest()[:12]
    
    params = {
        'identity': raw_identity,
        'nonce': nonce
    }
    headers = {'X-Auth': token}
    
    r = requests.get(f"{BASE_URL}/api/key", params=params, headers=headers)
    if r.status_code != 200:
        raise Exception(f"Failed to get key: {r.text}")
    return int(r.json()['secret_hex'], 16)

def solve():
    print("[*] Đang lấy Ciphertext...")
    ct = get_ciphertext()
    print(f"[+] Ciphertext: {ct.hex()}")

    # Bước 1: Tìm X = (MSK + DEPTH_NOISE[0])
    # Sử dụng Path Traversal để trick server tính key cho depth 1 ("pwn")
    print("[*] Đang tính toán tham số Depth 1 (MSK + Noise[0])...")
    raw_id_1 = "guest/../pwn"
    key_depth_1 = get_key_from_oracle(raw_id_1) # Key của "pwn"
    
    # S_pwn = alpha("pwn", 1) * X + 8
    # => X = (S_pwn - 8) * inv(alpha)
    alpha_pwn = prime_mix("pwn", 1)
    inv_alpha_pwn = pow(alpha_pwn, -1, Q)
    X = ((key_depth_1 - 8) * inv_alpha_pwn) % Q
    print(f"[+] Tìm thấy cụm (MSK + Noise[0]): {hex(X)[:20]}...")

    # Bước 2: Tìm NB_1 = DEPTH_NOISE[1]
    # Sử dụng identity hợp lệ "guest/test" (Depth 2)
    print("[*] Đang tính toán tham số Depth 2 (Noise[1])...")
    raw_id_2 = "guest/test"
    key_depth_2 = get_key_from_oracle(raw_id_2) # Key của "guest/test"
    
    # Tính lại trạng thái của "guest" dựa trên X vừa tìm được
    # sk_guest = alpha("guest", 1) * X + 8
    alpha_guest = prime_mix("guest", 1)
    sk_guest = (alpha_guest * X + 8) % Q
    
    # Key_final = alpha("test", 2) * (sk_guest + NB_1) + 9
    # => NB_1 = (Key_final - 9) * inv(alpha_test) - sk_guest
    alpha_test = prime_mix("test", 2)
    inv_alpha_test = pow(alpha_test, -1, Q)
    
    term = ((key_depth_2 - 9) * inv_alpha_test) % Q
    NB_1 = (term - sk_guest) % Q
    print(f"[+] Tìm thấy Noise[1]: {hex(NB_1)[:20]}...")

    # Bước 3: Giả lập server để tạo key cho "admin/root"
    print("[*] Đang giả lập key cho 'admin/root'...")
    
    # Tính sk_admin (Depth 1)
    # sk_admin = alpha("admin", 1) * X + 8
    alpha_admin = prime_mix("admin", 1)
    sk_admin = (alpha_admin * X + 8) % Q
    
    # Tính sk_root (Depth 2 - Final Key)
    # sk_root = alpha("root", 2) * (sk_admin + NB_1) + 9
    alpha_root = prime_mix("root", 2)
    sk_target = (alpha_root * (sk_admin + NB_1) + 9) % Q
    
    print(f"[+] Target Key: {hex(sk_target)[:20]}...")

    # Bước 4: Decrypt
    flag = stream_xor(sk_target, ct)
    print("\n" + "="*50)
    try:
        print(f"FLAG: {flag.decode()}")
    except:
        print(f"FLAG (hex): {flag.hex()}")
    print("="*50)

if __name__ == "__main__":
    solve()