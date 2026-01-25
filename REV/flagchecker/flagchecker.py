import struct
import binascii


KEY_HEX = "0ddc01c863994541812dfc0479296063"
key_bytes = binascii.unhexlify(KEY_HEX)
KEY = list(struct.unpack('<4I', key_bytes))

# Ciphertext
#  72 bytes (144 hex chars). Added '00'
TARGET_HEX = (
    "a369c782fb8e4e527f110fb15ad8e0cb0cb848d149e6096060beca9b28c9818ad"
    "ea0f1af237492b1b4d8f9d9de35930370d34c9d9e6b50cbe5521b5a9a2ec82138"
    "abaa7bd0779560" + "00" 
)

# XTEA DECRYPT FUNCTION
def xtea_decrypt_block(v0, v1, k):
    DELTA = 0x9E3779B9
    sum = (DELTA * 32) & 0xFFFFFFFF
    
    for _ in range(32):
        v1 = (v1 - ((((v0 << 4) ^ (v0 >> 5)) + v0) ^ (sum + k[(sum >> 11) & 3]))) & 0xFFFFFFFF
        sum = (sum - DELTA) & 0xFFFFFFFF
        v0 = (v0 - ((((v1 << 4) ^ (v1 >> 5)) + v1) ^ (sum + k[sum & 3]))) & 0xFFFFFFFF
        
    return v0, v1

# CBC
def solve():
    print("[*] Solving XTEA-CBC...")
    
    data = binascii.unhexlify(TARGET_HEX)
    blocks = []
    for i in range(0, len(data), 8):
        chunk = data[i:i+8]
        if len(chunk) < 8: break
        blocks.append(struct.unpack('<2I', chunk))
    
    plaintext_bytes = b""
    
    for i in range(1, len(blocks)):
        
        curr_v0, curr_v1 = blocks[i]
        
        prev_v0, prev_v1 = blocks[i-1]
        
        dec_v0, dec_v1 = xtea_decrypt_block(curr_v0, curr_v1, KEY)
        
        plain_v0 = dec_v0 ^ prev_v0
        plain_v1 = dec_v1 ^ prev_v1
        
        plaintext_bytes += struct.pack('<2I', plain_v0, plain_v1)

    try:
        flag = plaintext_bytes.split(b'\x00')[0]
        print(f" RAW: {plaintext_bytes}")
        print(f" FLAG: {flag.decode('utf-8', errors='ignore')}")
    except:
        print(plaintext_bytes)

if __name__ == "__main__":
    solve()