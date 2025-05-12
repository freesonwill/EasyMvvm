#!/bin/bash

# 获取传入的文件夹路径
TARGET_DIR="$1"

if [ -z "$TARGET_DIR" ]; then
  TARGET_DIR="./"
  echo "no TARGET_DIR set, use $TARGET_DIR instead"
fi

# 忽略 build 目录，查找图片文件
find "$TARGET_DIR" \( -path "*/build" -o -path "*/z_doc" \) -prune -o -type f \( \
! -iname "*.9.png" \
  -iname "*.png" \
  -o -iname "*.jpg" \
  -o -iname "*.jpeg" \
  -o -iname "*.bmp" \
  -o -iname "*.gif" \
  -o -iname "*.tiff" \
  -o -iname "*.ico" \
  -o -iname "*.svg" \
  -o -iname "*.heif" \
\) -print
