def ifrepeat():
    s = 'sssssdddddsssssddddd'
    for i in range(1,len(s) / 2 + 2):
        if len(s) % i != 0:
            continue
        else:
            if s[:i] * (len(s) / i) == s:
                return True;
            else:
                continue;
    return False;
        
        
print(ifrepeat())
s= 'sssssdddddsssssddddd'
if s[:10] * 2 == s:
    print(True)
else:
    print(False)