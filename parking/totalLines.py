import os

def count_lines(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        lines = file.readlines()
        return len(lines)

def traverse_directory(directory):
    total_lines = 0

    for root, dirs, files in os.walk(directory):
        for file_name in files:
            if file_name.endswith(('.java', '.xml')):
                file_path = os.path.join(root, file_name)
                lines = count_lines(file_path)
                total_lines += lines

                print(f"{file_path}: {lines} lines")

    print(f"Total lines: {total_lines}")
    x= input()
    x = input(s)

# 输入要遍历的文件夹路径
folder_path = "./"
traverse_directory(folder_path)
