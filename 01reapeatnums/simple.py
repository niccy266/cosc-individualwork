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

    return a_search(n, b, n / b)


def a_search(b, s, e):
    curr = s # record where run started
    long = curr
    long_l = 0
    while s < e:
        num = numberToBase(b, s)
        if not hasRepeats(num):
            curr_l = s - curr
            if curr_l > long_l:
                long_l = curr_l
                long = curr
            curr = s + 1
    return long



def hasRepeats(num):
    encountered = []
    for d in num:
        if d in encountered:
            return True
        else:
            encountered.append(d)
    return False



def b_func(b, c):
    n = 1
    while True:
        if(hasRepeats(numberToBase(b, n))):
            if(hasRepeats(numberToBase(c, n))):
                return n
        n += 1



# found this code at https://stackoverflow.com/questions/2267362/how-to-convert-an-integer-to-a-string-in-any-base
def numberToBase(b, n):
    if n == 0:
        return [0]
    digits = []
    while n:
        digits.append(int(n % b))
        n //= b
    return digits[::-1]



def baseToDecimal(b, n):
    num = 0
    for i, d in enumerate(n):
        num += d * b ** (len(n)-i) 
    return num


if __name__ == "__main__":
    main()