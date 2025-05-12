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

            if [[ ! "$filename" =~ "colors.xml" ]]; then

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


#处理colors.xml
process_colors_xml() {
    local dir="$1"
    local suffix="$2"

    for entry in "$dir"/*; do
        if [ -f "$entry" ] && [ "$(basename "$entry")" = "colors.xml" ]; then
            echo "处理文件: $entry"


            # 临时文件用于存储修改后的内容
            temp_file="${entry}.tmp"
            # 修改后的文件
            output_file="${entry%.*}_modified.xml"

            while IFS= read -r line; do
                if [[ "$line" =~ \<color\ name=\"([^\"]+)\" ]]; then
                    name="${BASH_REMATCH[1]}"
                    if [[ ! "$name" =~ "${suffix}"$ ]]; then
                        new_name="${name}${suffix}"
                        new_line=$(echo "$line" | sed "s/name=\"${name}\"/name=\"${new_name}\"/")
                        echo "$new_line" >> "$temp_file"
                    else
                        echo "$line" >> "$temp_file"
                    fi
                else
                    echo "$line" >> "$temp_file"
                fi
            done < "$entry"

            mv "$temp_file" "$output_file"
            echo "生成新文件: $output_file"
        elif [ -d "$entry" ]; then
            process_colors_xml "$entry"
        fi
    done
}

process_colors_xml "$directory/src/main/res-black_blue" "_black_blue"
process_colors_xml "$directory/src/main/res-black_red" "_black_red"
process_colors_xml "$directory/src/main/res-classic" "_classic"
process_colors_xml "$directory/src/main/res-white_blue" "_white_blue"
process_colors_xml "$directory/src/main/res-white_green" "_white_green"

echo "处理完成！"