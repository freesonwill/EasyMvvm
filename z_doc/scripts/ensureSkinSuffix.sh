#!/bin/bash

# === 参数校验 ===
if [ $# -lt 2 ]; then
    echo "用法: $0 <目录路径> <主题名1,主题名2,...> [--dry-run]"
    echo "例如: $0 /path/to/project light,dark"
    exit 1
fi

directory="$1"
themes_str="$2"
dry_run=false

# 可选参数 --dry-run
if [ "$3" = "--dry-run" ]; then
    dry_run=true
fi

# 转换成数组
IFS=',' read -r -a themes <<< "$themes_str"

# 校验目录
if [ ! -d "$directory" ]; then
    echo "错误: 目录 $directory 不存在"
    exit 1
fi

# === 文件重命名 ===
rename_files() {
    local dir="$1"
    local suffix="$2"

    for entry in "$dir"/*; do
        if [ -f "$entry" ]; then
            filename=$(basename "$entry")
            if [[ ! "$filename" =~ "colors.xml" ]]; then
                extension="${filename##*.}"
                base="${filename%.*}"
                [[ "$extension" == "$filename" ]] && extension="" && base="$filename"

                if [[ ! "$base" =~ "$suffix"$ ]]; then
                    new_filename="${base}${suffix}"
                    [[ -n "$extension" ]] && new_filename="${new_filename}.${extension}"
                    new_path="$dir/$new_filename"
                    echo "重命名: $entry -> $new_path"
                    $dry_run || mv "$entry" "$new_path"
                fi
            fi
        elif [ -d "$entry" ]; then
            rename_files "$entry" "$suffix"
        fi
    done
}

# === 修改 colors.xml ===
# 读取文件逐行处理时，缓冲所有行到数组或变量，最后写出时控制换行
process_colors_xml() {
    local dir="$1"
    local suffix="$2"

    for entry in "$dir"/*; do
        if [ -f "$entry" ] && [ "$(basename "$entry")" = "colors.xml" ]; then
            echo "处理文件: $entry"
            temp_file="${entry}.tmp"

            lines=()
            while IFS= read -r line || [ -n "$line" ]; do
                # 改名逻辑
                if [[ "$line" =~ \<color\ name=\"([^\"]+)\" ]]; then
                    name="${BASH_REMATCH[1]}"
                    if [[ ! "$name" =~ ${suffix}$ ]]; then
                        line=$(echo "$line" | sed "s/name=\"${name}\"/name=\"${name}${suffix}\"/")
                    fi
                fi
                lines+=("$line")
            done < "$entry"

            # 确保有xml头
            if [[ ! "${lines[0]}" =~ ^\<\?xml ]]; then
                lines=( '<?xml version="1.0" encoding="utf-8"?>' "${lines[@]}" )
            fi

            # 找最后一个非空行索引
            last_non_empty_index=-1
            for ((i=${#lines[@]}-1; i>=0; i--)); do
                if [[ -n "${lines[i]// /}" ]]; then
                    last_non_empty_index=$i
                    break
                fi
            done

            # 输出文件
            {
                for ((i=0; i<last_non_empty_index; i++)); do
                    echo "${lines[i]}"
                done
                # 最后一行（最后一个非空行），用printf避免多余换行
                printf '%s' "${lines[last_non_empty_index]}"
            } > "$temp_file"

            mv "$temp_file" "$entry"
            echo "已覆盖文件: $entry"
        elif [ -d "$entry" ]; then
            process_colors_xml "$entry" "$suffix"
        fi
    done
}





# === 批量处理 ===
count=0
for module_dir in "$directory"/*; do
    if [ -d "$module_dir" ]; then
        for theme in "${themes[@]}"; do
            path="$module_dir/src/main/res-${theme}"
            suffix="_${theme}"
            if [ -d "$path" ]; then
                echo "处理模块: $module_dir 主题: $theme"
                rename_files "$path" "$suffix"
                process_colors_xml "$path" "$suffix"
                ((count++))
            fi
        done
    fi
done

echo "✅ 处理完成！共处理了 $count 个主题目录。"
