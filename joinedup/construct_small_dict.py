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

single_len = bfs(start, end, True)
double_len = bfs(start, end, False)

print(single_len)

print(*single_len[0])
print(*double_len[0])
exit(0)

f = open("small_dict.txt", "w")
f.write("Now the file has more content!")
f.close()