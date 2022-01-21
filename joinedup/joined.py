from functools import wraps
from collections import deque
import sys


def memoize(function):
    memo = {}
    @wraps(function)
    def wrapper(*args):
        try:
            return memo[args]
        except KeyError:
            rv = function(*args)
            memo[args] = rv
            return rv
    return wrapper


@memoize
def fibonacci(n):
    if n < 2: return n
    return fibonacci(n - 1) + fibonacci(n - 2)

print(fibonacci(25))


class Node:
    def __init__(self, i, w):
        self.index = i
        self.word = w
        self.visited = False
        self.distance
        self.next_node


def main():
    global dictionary

    str_start = sys.argv[0]
    str_end = sys.argv[1]
    print(start, end)

    dictionary = []
    nodes = []

    #read in dictionary
    for line in sys.stdin :
        dictionary.append(line.rstrip())
    
    #remove duplicates
    dictionary = list(dict.fromkeys(dictionary))
    for i, word in enumerate(dictionary) :
        nodes.append(Node(i, word))

    start = nodes[dictionary.index(str_start)]
    end = nodes[dictionary.index(str_end)]

    single_len = dfs(start, end, True)
    double_len = dfs(start, end, False)

    print(single_len[0], (dictionary[i] for i in single_len[1:] if not single_len == 0))
    print(double_len[0], (dictionary[i] for i in double_len[1:] if not double_len == 0))


def dfs(Node start, Node end, bool single):
    global dictionary, stack

    start.distance = 0
    start.visited = True
    stack = deque([start])
    while(stack): #while stack still has items
        out = queue_neighbours(single)
        if type(out) == list:
            return out
    return [0]


def queue_neighbours(bool single):
    left = stack.popleft()
    match_len = len(left.word)/2 + len(left.word) % 2

    for right in nodes :
        if(right.visited):
            continue

        #check how big the matching string must be
        match_len2 = len(word)/2 + len(word) % 2
        if(single):
            match_len = min(match_len, match_len2)
        else:
            match_len = max(match_len, match_len2)

        #check if suffix matches prefix of next word
        if(left.word[-match_len:] == word[:match_len]):
            
            #check if we reached the end word
            if(right == end):
                out = [left.distance + 1, right]
                for i in range(left.distance):
                    out.insert(1, left.word)
                    left = left.next_node
                return out

            #else visit node and add it to queue
            else:
                right.next_node = left
                right.distance = left.distance + 1
                stack.append(right)
    return False

if __name__ == "__main__":
    main()