from z3 import *

# =====================
# helpers
# =====================

def rol(x, r, bits):
    return ((x << r) | LShR(x, bits - r)) & ((1 << bits) - 1)

def ror(x, r, bits):
    return (LShR(x, r) | (x << (bits - r))) & ((1 << bits) - 1)

def select(arr, idx):
    r = arr[0]
    for i in range(1, len(arr)):
        r = If(idx == i, arr[i], r)
    return r

def update(arr, idx, val):
    return [If(idx == i, val, arr[i]) for i in range(len(arr))]

# =====================
# constants
# =====================

unk_20E0 = [
    0x25,0x5F,0x2D,0x51,0x5B,0x34,0x52,0x5A,0x6F,0x87,
    0xEA,0x67,0x56,0x48,0x41,0xAB,0xA5,0x76,0x3C,0x23,
    0x1F,0x27,0xB9,0xC1,0xEB,0xF0,0x75,0xE3,0x35,0x2B,
    0x20,0x3A,0xF4,0x2D,0xB2,0x9B,0x8F,0x13,0x2B,0xDB,
    0xBD,0x77,0x3A,0xA8,0xF4,0x82,0xB3,0xA9,0xFB,0x7C,
    0x5E,0x66,0xB5,0x84,0xFA
]

unk_20C0 = [
    0x94,0xE7,0x34,0xE3,0xC5,0x65,0x57,0xC5,
    0xF7,0x0F,0x51,0xE1,0x97,0x2C,0x9C,0xAC,
    0x83,0x4E,0xE7,0x3F,0x1F,0x63,0xBF,0x7C,
    0x3A,0x24,0x37,0xE9
]

xmm_2120 = [
    0xD3,0x92,0x51,0x10,0xD7,0x9D,0x53,0x11,
    0xDB,0x98,0x55,0x12,0xDF,0x9B,0x57,0x13
]

xmm_2130 = [
    0x30,0x90,0x50,0x80,0xC0,0x20,0xF0,0xE0,
    0x40,0x70,0x00,0xB0,0x10,0xA0,0xD0,0x06
]

# =====================
# solver
# =====================

s = Solver()

flag = [BitVec(f'f{i}', 8) for i in range(55)]

for c in flag:
    s.add(c >= 0x20, c <= 0x7E)

# =====================
# stage 1
# =====================

s1 = [BitVecVal(0, 8) for _ in range(55)]

v8  = 0
v10 = BitVecVal(0, 32)
v11 = BitVecVal(73244475, 32)
v13 = BitVecVal(0xC0F4A1E1, 32)

for i in range(55):
    ch  = ZeroExt(24, flag[i])
    v17 = v11 + ch
    v11 = v11 + 73244475
    v13 = rol(v17 ^ v13, (i % 13) + 1, 32) - 1640531535
    v19 = v10 ^ (ch * (i + 3)) ^ LShR(v13, 3) ^ LShR(v13, 17)
    v10 = v10 + 11
    s1[v8 % 55] = Extract(7, 0, v19 ^ 0xA5)
    v8 += 7

for i in range(55):
    s.add(s1[i] == unk_20E0[i])

# =====================
# stage 2
# =====================

s2 = [BitVecVal(xmm_2120[i], 32) for i in range(16)]
v23 = BitVecVal(0, 32)

for i in range(55):
    idx = (ZeroExt(24, flag[i]) + 3 * i) % 7
    cur = select(s2, idx)

    v25 = v23 + 69 * ZeroExt(24, flag[i])
    v23 = v23 + 31

    tmp = cur ^ (i - 1640531527) ^ rol(v25, idx + 1, 32)

    rot = (i - (i // 5 + (((0xCCCCCCCCCCCCCCCD * i) >> 64) & 0xFC)) + 1) & 31
    newv = rol(tmp, rot, 32) + 2135587861

    s2 = update(s2, idx, newv)

for i in range(28):
    word = s2[i // 4]
    byte = Extract(7, 0, LShR(word, 8 * (i % 4)))
    s.add(byte == unk_20C0[i])

# =====================
# stage 3
# =====================

s3 = [BitVecVal(b, 8) for b in xmm_2130]

v29 = BitVecVal(0, 64)
v30 = BitVecVal(0x9E3779B97F4A7C15, 64)
v31 = BitVecVal(0x2173A47B8C0D1EEF, 64)

for i in range(55):
    v32 = v30 + v29
    v29 = v29 + 4129

    idx = (i + ZeroExt(56, flag[i])) & 0xF
    sel = ZeroExt(56, select(s3, idx))
    val = ZeroExt(56, flag[i]) ^ sel

    v31 = v32 + rol((val << (8 * (i % 7))) ^ v31, (i % 13) + 3, 64)
    v30 = i + (v30 ^ ror(v31 ^ val, (i % 11) + 1, 64)) + 11259375

s.add(v30 ^ v31 == 5958041946172822919)

# =====================
# solve
# =====================

print("[*] Solving...")
assert s.check() == sat
m = s.model()

mid = ''.join(chr(m[f].as_long()) for f in flag)
print("FLAG =", "VSL{" + mid + "}")
