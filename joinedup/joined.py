from collections import deque
import sys


class Node:
    def __init__(self, i, w):
        self.index = i
        self.word = w
        self.visited = False
        self.distance = 100000
        self.next_node = None


def main():
    global dictionary, nodes, end

    try:
        str_start = sys.argv[1]

        str_end = sys.argv[2]
    except:
        print("please give two words to join in args")
        exit(1)

    print(str_start, str_end)

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

    print(single_len)

    print(single_len[0], (word for word in single_len[1:] if not single_len == 0))
    print(double_len[0], (word for word in double_len[1:] if not double_len == 0))
    exit(0)


def dfs(start, end, single):
    global dictionary, stack

    start.distance = 0
    start.visited = True
    stack = deque([start])
    while(stack): #while stack still has items
        out = queue_neighbours(single)
        if type(out) == list:
            return out

    return [0]


def queue_neighbours(single):
    left = stack.popleft()
    match_len = int(len(left.word)/2) + len(left.word) % 2
    print(left.word)
    print(match_len)

    for right in nodes :
        if(right.visited):
            continue

        #check how big the matching string must be
        match_len2 = int(len(right.word)/2) + len(right.word) % 2
        if(single):
            match = min(match_len, match_len2)
        else:
            match = max(match_len, match_len2)

        #check if suffix matches prefix of next word
        if(left.word[-match:] == right.word[0:match]):
            
            #check if we reached the end word
            if(right == end):
                out = [left.distance + 1, right.word]
                for i in range(left.distance + 1):
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