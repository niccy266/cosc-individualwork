from collections import deque
from math import ceil
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

    dictionary = []
    nodes = []

    dictionary.append(str_start)
    dictionary.append(str_end)

    try:
        #read in dictionary
        for line in sys.stdin :
            dictionary.append(line.rstrip())
    except:
        print("please pass a dictionary to stdin")
        exit(1)
    
    #remove duplicates
    dictionary = list(dict.fromkeys(dictionary))
    for i, word in enumerate(dictionary) :
        nodes.append(Node(i, word))

    #start = nodes[dictionary.index(str_start)]
    #end = nodes[dictionary.index(str_end)]

    start = nodes[0]
    end = nodes[1]

    print(start.word, end.word)

    single_len = bfs(nodes, dictionary, start, end, True)
    double_len = bfs(nodes, dictionary, start, end, False)

    print(single_len)

    print(*single_len)
    print(*double_len)
    exit(0)


def bfs(nodes, dictionary, single):
    start = nodes[0]
    end = nodes[1]
    #visit the start node

    start.distance = 0
    start.visited = True


    queue = deque([start])
    while(queue): #while stack still has items
        node = queue.popleft()

        neighbours = find_neighbours(node, single)

        for node in neighbours:
            #check if we reached the end word
            if(node == end):
                #create a list of words joining start and end to return
                out = [node.distance, node.word]
                while not node.next_node == None:
                    node = node.next_node
                    out.insert(1, node.word)
                return out
            else:
                queue.append(node)

    #didn't find a link
    return[0]


def find_neighbours(left, single):
    #minimum length of the matching part of the left word
    suffix_len = int(ceil(len(left.word))/2)

    neighbours = []

    for right in nodes :
        if(right.visited):
            continue

        #check how big the matching string must be
        preffix_len = int(ceil(len(right.word))/2)

        match_len = min(suffix_len, preffix_len) if single else max(suffix_len, preffix_len) 

        #check if suffix matches prefix of next word
        if(left.word[-match_len:] == right.word[0:match_len]):
            #visit node
            right.next_node = left
            right.distance = left.distance + 1
            right.visited = True
            #save the neighbour to the return list
            neighbours.append(right)
        
        return neighbours


if __name__ == "__main__":
    main()