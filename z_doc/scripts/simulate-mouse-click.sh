#!/bin/bash

count=1

while true
do
  #pos="1600,350"
  pos="1087,327"
  echo "[$count] click $pos $(date '+%Y-%m-%d %H:%M:%S')"
  cliclick c:$pos
  sleep 0.5

  echo "[$count] click back $(date '+%Y-%m-%d %H:%M:%S')"
  #cliclick c:1506,183
  cliclick kd:cmd kd:shift t:b ku:shift ku:cmd
  sleep 0.5

  ((count++))

   # 每轮循环之后检查是否按了 q
  read -t 1 -n 1 key
  if [[ $key == "q" ]]; then
    echo -e "\n检测到 q，退出循环。"
    break
  fi
done