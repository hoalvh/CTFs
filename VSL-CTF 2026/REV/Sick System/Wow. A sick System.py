encrypted = bytes.fromhex("2F451B4119314527451B051F1B0167435D593337010D445A042B3F7C0166")

keys_to_try = [
    b'R0n4lD0',    # 0x52,0x42,0x6E,0x34,0x6C,0x44,0x30
]

for k in keys_to_try:
    decoded = bytes([encrypted[i] ^ k[i % 7] for i in range(30)])
    flag = decoded[::-1]
    try:
        decoded_str = flag.decode()
        
        print(f"Key :  {k}")
        print(f"Flag:  {decoded_str}")
    except:
        continue