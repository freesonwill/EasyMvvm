#!/bin/bash

# 检查是否提供了目录参数
if [ $# -ne 1 ]; then
    echo "用法: $0 <目录路径>"
    exit 1
fi

# 获取输入的目录路径
directory="$1"

# 检查目录是否存在
if [ ! -d "$directory" ]; then
    echo "错误: 目录 $directory 不存在"
    exit 1
fi

# 递归遍历目录
rename_files() {
    local dir="$1"
    local suffix="$2"

    # 遍历目录中的所有文件和子目录
    for entry in "$dir"/*; do
        # 如果是文件
        if [ -f "$entry" ]; then
            # 获取文件名（包含后缀）
            filename=$(basename "$entry")
            # 获取目录名
            dirname=$(basename "$dir")
            # 获取文件后缀（包括 .）
            extension="${filename##*.}"
            # 如果没有后缀，extension 会等于 filename，需特殊处理
            if [ "$extension" = "$filename" ]; then
                extension=""
                basename="$filename"
            else
                # 获取去掉后缀的文件名部分
                basename="${filename%.*}"
            fi

            # 检查去掉后缀的文件名是否以后缀结尾
            if [[ ! "$basename" =~ "$suffix"$ ]]; then
                # 如果不以后缀结尾，构造新文件名
                if [ -z "$extension" ]; then
                    new_filename="${basename}${suffix}"
                else
                    new_filename="${basename}${suffix}.${extension}"
                fi
                new_path="$dir/$new_filename"

                # 重命名文件
                echo "重命名: $entry -> $new_path"
                mv "$entry" "$new_path"
            fi
        # 如果是子目录，递归调用
        elif [ -d "$entry" ]; then
            rename_files "$entry" "$2"
        fi
    done
}

# 调用函数，开始处理
rename_files "$directory/src/main/res-black_blue" "_black_blue"
rename_files "$directory/src/main/res-black_red" "_black_red"
rename_files "$directory/src/main/res-classic" "_classic"
rename_files "$directory/src/main/res-white_blue" "_white_blue"
rename_files "$directory/src/main/res-white_green" "_white_green"

echo "处理完成！"