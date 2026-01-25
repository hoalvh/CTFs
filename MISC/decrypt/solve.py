import torch
import torch.nn as nn

class MyNet(nn.Module):
    def __init__(self):
        super().__init__()
        self.linear1 = nn.Linear(1, 512)
        self.linear2 = nn.Linear(512, 2048)
        self.linear3 = nn.Linear(2048, 1024)
        self.linear4 = nn.Linear(1024, 95)
        self.active = nn.ReLU()
        self.reg = nn.LogSoftmax(dim=1)
    def forward(self, x):
        x = self.active(self.linear1(x))
        x = self.active(self.linear2(x))
        x = self.active(self.linear3(x))
        x = self.reg(self.linear4(x))
        return x

def solve():
    print("[*] Loading model...")
    try:
        model = torch.load("D:\challenge (13)\model", map_location='cpu', weights_only=False)
        model.eval()
    except FileNotFoundError:
        print("Couldn't find file 'model'")
        return

    print("[*] Đang xây dựng bảng tra ngược (Reverse Mapping)...")
    
    reverse_map = {}
    
    with torch.no_grad():
        for ascii_code in range(32, 127): 
        
            input_tensor = torch.Tensor([[float(ascii_code)]])
            
            prediction = model(input_tensor)
            
            pred_index = prediction.argmax(dim=1).item()
            output_char_code = pred_index + 32
            output_char = chr(output_char_code)
            
            reverse_map[output_char] = chr(ascii_code)

    print("[*] Solving...")
    try:
        with open('output.txt', 'r', encoding='utf-8') as f:

            cipher_text = f.read().strip()
            
            if "[source" in cipher_text:
                cipher_text = cipher_text.split("] ")[-1]
                
    except FileNotFoundError:
        
        cipher_text = "G'vL(N~NH\"kV$D\"SV6p9BVe8" 

    print(f"Ciphertext: {cipher_text}")
    
    flag = ""
    for char in cipher_text:
        if char in reverse_map:
            flag += reverse_map[char]
        else:
            
            flag += "?"
            
    print("-" * 30)
    print(f"FLAG: {flag}")
    print("-" * 30)

if __name__ == "__main__":
    solve()