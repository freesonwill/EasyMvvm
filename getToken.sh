#!/usr/bin/env bash
if [ -z "$1" ]; then
    echo "错误：请输入用户名"
    echo "例如： ./getToken.sh qatest1"
    exit 1
fi
echo "用户名：$1"

data='{"username":"'"$1"'","agentPwd":"L2uf.d\u0021Zrmm"}'

#echo $data

response=$(curl 'https://www.qxe68.com:7010/api/token/s1' \
             -H 'accept: application/json, text/plain, */*' \
             -H 'accept-language: zh-CN,zh;q=0.9' \
             -H 'cache-control: no-cache' \
             -H 'content-type: application/json;charset=UTF-8' \
             -H 'origin: https://www.qxe68.com:7010' \
             -H 'pragma: no-cache' \
             -H 'priority: u=1, i' \
             -H 'referer: https://www.qxe68.com:7010/login' \
             -H 'sec-ch-ua: "Chromium";v="140", "Not=A?Brand";v="24", "Google Chrome";v="140"' \
             -H 'sec-ch-ua-mobile: ?1' \
             -H 'sec-ch-ua-platform: "Android"' \
             -H 'sec-fetch-dest: empty' \
             -H 'sec-fetch-mode: cors' \
             -H 'sec-fetch-site: same-origin' \
             -H 'user-agent: Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Mobile Safari/537.36' \
             --data-raw $data
)

#echo $response

token=$(echo "$response" | grep -o '"token":"[^"]*"'  )

token=$(echo "${token//token}")

token=$(echo "${token//:}")

token=$(echo "${token//\"}")

#echo $token

secondResponse=$(curl 'https://www.qxe68.com:7010/api/game/forward.do?gameType=0' \
                   -H 'accept: application/json, text/plain, */*' \
                   -H 'accept-language: zh-CN,zh;q=0.9' \
                   -H "authorization: $token" \
                   -H 'cache-control: no-cache' \
                   -H 'pragma: no-cache' \
                   -H 'priority: u=1, i' \
                   -H 'referer: https://www.qxe68.com:7010/console/gamemanager' \
                   -H 'sec-ch-ua: "Chromium";v="140", "Not=A?Brand";v="24", "Google Chrome";v="140"' \
                   -H 'sec-ch-ua-mobile: ?1' \
                   -H 'sec-ch-ua-platform: "Android"' \
                   -H 'sec-fetch-dest: empty' \
                   -H 'sec-fetch-mode: cors' \
                   -H 'sec-fetch-site: same-origin' \
                   -H 'user-agent: Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Mobile Safari/537.36'
)

#echo $secondResponse

loginToken=$(echo "$secondResponse" |  grep -o '"data":"[^"]*"' | sed 's/.*token=\([^"&]*\).*/\1/'  )

echo $loginToken


