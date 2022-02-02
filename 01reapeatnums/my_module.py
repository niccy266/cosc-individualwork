# the largest number in any base with no repeating 
# digits is b-1, b-2, b-3... b-b (0)
# e,g: 9876543210
# any number larger than this will have to use one of
# the digits from 0 to b-1 (including 0)
# adding to this number will increase at least one of
# the digits by 1 and that will cause a repeated number

from email.mime import base
import sys
from syslog import LOG_LOCAL1

def main():
    for line in sys.stdin :
        tokens = line.split()
        try:
            a = int(tokens[1])
            b = int(tokens[2])
        except:
            print("Bad line:  " + line)
            continue

        mode = tokens[0]
        if mode == "A":
            a_func(a, b)
        elif mode == "B":
            b_func(a, b)
        else:
            print("Bad line:  " + line)


def a_func(b, n):
    if b == 1: 
        return 1 # in base 1 all numbers past 1 have repeats

    upper = [b - n for n in range(1, b + 1)]
    upper_decimal = baseToDecimal(b, upper)
    if n > upper_decimal * (1 + 1/b): 
        # if input number was bigger than largest num without repeats
        # by a large margin, then that number is the start of
        # an infinite set of numbers with repeats
        return upper_decimal

    
    if n < 1000000 : # brute force for small searches
        return a_search(n, b, n / b)

    digits = numberToBase(b, n)

    if digits[0] > 1 or digits[1] >= 1 and digits[2] >=5:
        start = [1, 0, b - n for n in range(1, len(digits) - 1)] # search from like 109876543
        start = baseToDecimal(start)
        end = [1, 2, b - n for n in range(1, len(digits) - 1)] # to like 129876543
        print(digits, end)
        end = baseToDecimal(end)
        if end > n : 
            end = n
        
        long, long_l = a_search(b, start, end)

    elif(digits[0:2] == [1, 0, 0]): # max is barely over an order of magnitude, eg 100010 
        start = [1, 0, b - n for n in range(1, len(digits) - 2)]     # start around 10900 
        run,  a_search(b, n, baseToDecimal(start))

    else:                   # max is up to 109000
        start = [b - n for n in range(1, len(digits))] #  98765
        return a_search(b, n, baseToDecimal(start))


    longest = a_search(n, b, start)
    print(longest)


"""
def a_func(b, n):
    if b == 1: 
        return 1 # in base 1 all numbers past 1 have repeats
    digits = numberToBase(b, n)
    if len(digits) > b : 
        # if input number was bigger than largest num without repeats,
        # set it to that number
        digits = [b - n for n in range(1, b + 1)]
    if n < 1000000 : # brute force for small searches
        start = [1]
    elif digits[0] > 1 or digits[1] >= 1 and digits[2] >=5:
        start = [1, 0, b - n for n in range(1, len(digits) - 1)]
    else:
        start = [b - n for n in range(1, len(digits))]

    longest = a_search(digits, b, start)
    print(longest)
"""


def a_search(b, s, e):
    curr = s # record where run started
    long = curr
    long_l = 0
    while s < e:
        num = numberToBase(b, s)
        encountered = []
        for d in num:
            if d in encountered:
                curr_l = s - curr
                if curr_l > long_l:
                    long = curr
                curr = s + 1
                break
            else:
                encountered.append(d)
    return long, long_l



def b_func(b, c):
    pass


# found this code at https://stackoverflow.com/questions/2267362/how-to-convert-an-integer-to-a-string-in-any-base
def numberToBase(b, n):
    if n == 0:
        return [0]
    digits = []
    while n:
        digits.append(int(n % b))
        n //= b
    return digits[::-1]


# found this code at https://stackoverflow.com/questions/2267362/how-to-convert-an-integer-to-a-string-in-any-base
def baseToDecimal(b, n):
    num = 0
    for i, d in enumerate(n):
        num += d * b ** (len(n)-i) 
    return num


def incInBase(b, n):
    n = [0] + n     # add overflow digit
    for i in range(1, len(n) + 1):
        n[-i] += 1
        if n[-i] == b:
            n[-i] = 0
        else: 
            break
    if n[0] == 0:           
        n.popleft() # if overflow digit wasn't used, remove it
    return n



def addInBase(b, n, a):
    n = [0] + n             # add overflow digit
    a = [0]*(len(n)-len(a)) + a     # pad a to same size as n
    for i in range(1, len(n) + 1):
        d = n[-i] + a[-i]   # add right digits first
        n[-i] = d % b       # make current digit the remainder of the sum and the base
        n[-i-1] += d // b   # add overflow into next digit
    if n[0] == 0:           
        n.popleft()         # if overflow digit wasn't used, remove it
    return n


def subInBase(b, n, s):
    s = [0]*(len(n)-len(s)) + s     # pad s to same size as n
    for i in range(1, len(n) + 1):
        while s[-i] > n[-i]:    # borrowing digits
            n[-i] += b
            n[-i-1] -= 1
        d = n[-i] - s[-i]   # subtract right digits first
        n[-i] = d % b       # save remainder of difference and base as digit
        n[-i-1] += d // b   # add overflow into next digit
    while n[0] == 0:           
        n.popleft()         # clear trailing 0s on the left
    return n


if __name__ == "__main__":
    main()