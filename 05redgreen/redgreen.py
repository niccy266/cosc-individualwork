import sys


EMPTY = 0
GREEN = 1
RED = -1

cache = [GREEN] + [EMPTY] * 9999999

def main():
    
    for line in sys.stdin :
        tokens = line.split()
        try:
            a = int(tokens[0])
            b = int(tokens[1])

            if b < a:
                raise
            # must be positive integers
            if a < 1 or b < 1:
                raise

            for n in range(a, b + 1):
                print(isRed(n))
        except:
            print("Bad input: " + line, end="")
            continue


def isRed(n):
    if cache[n] == RED: 
        return True
    if cache[n] == GREEN: 
        return False

    greenMinRed = 0
    divisor = 1
    while divisor <= n/2:
        greenMinRed += isRed(n/2)

    if greenMinRed > 0: 
        cache[n] = RED
        return RED
    else: 
        cache[n] = GREEN
        return GREEN


if __name__ == "__main__":
    main()