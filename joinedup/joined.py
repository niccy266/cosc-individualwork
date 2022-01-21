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
        self.word = word
        self.neighbours = []
        self.visited = False
        self.distance
        self.next_node


def main:
    global dictionary

    str_start = sys.argv[0]
    str_end = sys.argv[1]
    print(start, end)

    dictionary = []
    nodes = []

    #read in dictionary
    for line in sys.stdin:
        dictionary.append(line.rstrip())
    
    #remove duplicates
    dictionary = list(dict.fromkeys(dictionary))
    for i, word in enumerate(dictionary):
        nodes.append(Node(i, word))

    start = nodes[dictionary.index(str_start)]
    end = nodes[dictionary.index(str_end)]

    single_len = dfs(start, end, True)
    double_len = dfs(start, end, False)

    print(single_len[0], (dictionary[i] for i in single_len[1:-1] if not single_len == 0)
    print(double_len[0], (dictionary[i] for i in double_len[1:-1] if not double_len == 0)


def dfs(Node start, Node end, bool single):
    global dictionary, stack

    stack = deque([i_start])
    while(stack): #checks if stack still has items
        node = stack.popleft()

        if(node == end):
            out = [node.distance]
            for i in range(node.distance):
                out.append(node.word)
                node = node.next_node
            return out
        queue_neighbours(single)





def queue_neighbours(bool single):
    index = stack.popleft()
    word = 
    

if __name__ == "__main__":
    main()