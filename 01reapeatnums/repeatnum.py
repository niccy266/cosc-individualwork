# the largest number in any base with no repeating 
# digits is b-1, b-2, b-3... b-b (0)
# e,g: 9876543210 for base 10
# any number larger than this will have to use one of
# the digits from 0 to b-1 (including 0)
# adding to this number will increase at least one of
# the digits by 1 and that will cause a repeated number

import sys

def main():
    for line in sys.stdin :
        tokens = line.split()
        try:
            a = int(tokens[1])
            b = int(tokens[2])
            if a < 1 or b < 1:
                raise

            mode = tokens[0]
            if mode == "A":
                print(a_func(a, b))
            elif mode == "B":
                print(b_func(a, b))
            else:
                raise
        except:
            print("Bad line: " + line)
            continue


def a_func(b, n):
    if b == 1: 
        return 1 # in base 1 all numbers past 1 have repeats

    upper = [b - n for n in range(1, b + 1)]
    upper_decimal = baseToDecimal(upper, b)
    if n > upper_decimal * (1 + 1/b): 
        # if input number was bigger than largest num without repeats
        # by a large margin, then that number is the start of
        # an infinite set of numbers with repeats
        return upper_decimal

    return a_search(b, n // b, n)


def a_search(b, s, e):
    #print("run started at ", s)
    curr = s # record where run started
    long = curr
    long_l = 0
    while s < e:
        if not hasRepeats(numberToBase(s, b)):
            curr_l = s - curr
            #print("finished run at", curr, "with length", curr_l)
            if curr_l > long_l:
                long_l = curr_l
                long = curr
            curr = s + 1
        s += 1
    #print(long, long_l, numberToBase(long, b))
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
        if(hasRepeats(numberToBase(n, b))):
            if(hasRepeats(numberToBase(n, c))):
                #print(n, "is", numberToBase(n, b), "in", b, "and", 
                #   numberToBase(n, c), "in", c)
                return n
        n += 1



# found this code at https://stackoverflow.com/questions/2267362/how-to-convert-an-integer-to-a-string-in-any-base
def numberToBase(n, b):
    if n == 0:
        return [0]
    digits = []

    # had to add this to fix the program getting stuck when a base is 1
    if b == 1:
        while n > 0:
            digits.append(1)
            n -= 1
    
    while n:
        digits.append(int(n % b))
        n //= b
    return digits[::-1]



def baseToDecimal(n, b):
    num = 0
    for i, d in enumerate(n):
        num += d * b ** (len(n)-i) 
    return num


if __name__ == "__main__":
    main()