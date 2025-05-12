#!/bin/bash

### 先安装webp      brew install webp

# 获取传入的文件夹路径
input_dir="$1"

if [ -z "$input_dir" ]; then
  echo "Usage: $0 <input_directory>"
  exit 1
fi

if [ ! -d "$input_dir" ]; then
  echo "Directory not found: $input_dir"
  exit 1
fi

find "$input_dir" -type f \( -name "*.png" -o -name "*.jpg" \) ! \( -name "*.9.*" \) | while IFS= read -r file; do
  filename=$(basename "$file")
  dirname=$(dirname "$file")
  basename="${filename%.*}"
  
  output_file="${dirname}/${basename}.webp"
  cwebp -q 75 "$file" -o "$output_file"
  rm -f "$file"

  echo "Converted $file to $output_file"
done

echo "Conversion complete."
