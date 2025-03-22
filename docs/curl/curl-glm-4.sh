curl -X POST \
        -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsInNpZ25fdHlwZSI6IlNJR04ifQ.eyJhcGlfa2V5IjoiYzllZWQyZjE0ZmI3NDZhZDhjNzY4Y2E1Yzk0Njg2N2UiLCJleHAiOjE3NDI2Mjk5MDIwNjcsInRpbWVzdGFtcCI6MTc0MjYyODEwMjA3N30.wrtsnNa820b9d_Wz05Clvxl8jyY1FgeDDU_KvK0Rtp4" \
        -H "Content-Type: application/json" \
        -H "User-Agent: Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)" \
        -d '{
          "model":"glm-4",
          "stream": "true",
          "messages": [
              {
                  "role": "user",
                  "content": "1+1"
              }
          ]
        }' \
  https://open.bigmodel.cn/api/paas/v4/chat/completions