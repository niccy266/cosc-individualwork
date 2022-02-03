import sys

EMPTY = 0
GREEN = 1
RED = -1

cache = [0, GREEN] + [EMPTY] * 9999999

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

            print(a, b, end=" ")

            for n in range(a, b + 1):
                print("R" if colour(n) == RED else "G", end=" ")
            print()
        except:
            print("Bad input: " + line, end="")
            continue


def colour(n):
    #print(n)
    if cache[n] == RED: 
        return RED
    if cache[n] == GREEN: 
        return GREEN

    greenMinRed = 0
    closeFactors = [1] # every number has 1 as a near factor
    for divisor in range(2, n//2 + 1):
        closeFactors.append(n//divisor)
    closeFactors = list(set(closeFactors)) #remove duplicates

    #print("\n", closeFactors, end=" ")
    for f in closeFactors:
        greenMinRed += colour(f)

    if greenMinRed > 0: # had more green than red
        cache[n] = RED
        #print(n, "was red")
        return RED
    else: 
        cache[n] = GREEN
        #print(n, "was green")
        return GREEN


if __name__ == "__main__":
    main()