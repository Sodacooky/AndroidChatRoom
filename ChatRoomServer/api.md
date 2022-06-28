# Create Room

```json5
{
  "roomPassword": "114514",
  //optional
  "roomName": "new name"
}
```

```json5
{
  "success": true,
  "message": "成功创建房间",
  "data": {
    "user": {
      "avatarIndex": 31,
      "userId": "f7f121337453443eaa405d913d2cc00e",
      "userName": "用户f7f12",
      "cachingMessage": [
        {
          "content": "用户f7f12加入了房间",
          "type": "system"
        }
      ],
      "lastHeartbeatTime": "2022-06-22T08:10:43.412+00:00"
    },
    "room": {
      "roomId": "95c0f223421e453088fb11fb322d618d",
      "roomPassword": "114514",
      "roomName": "聊天室95c0f",
      "userContainer": {
        "userList": [
          {
            "avatarIndex": 31,
            "userId": "f7f121337453443eaa405d913d2cc00e",
            "userName": "用户f7f12",
            "cachingMessage": [],
            "lastHeartbeatTime": "2022-06-22T08:10:43.412+00:00"
          }
        ]
      }
    }
  }
}

```

# Join Room

```json5
{
  "roomId": "95c0f223421e453088fb11fb322d618d",
  "roomPassword": "114514"
}
```

```json5
{
  "success": true,
  "message": "成功加入房间",
  "data": {
    "user": {
      "avatarIndex": 30,
      "userId": "e07dac5372ad4f3488bbc41a37d94918",
      "userName": "用户e07da",
      "cachingMessage": [
        {
          "content": "用户e07da加入了房间",
          "type": "system"
        }
      ],
      "lastHeartbeatTime": "2022-06-22T08:10:52.211+00:00"
    },
    "room": {
      "roomId": "95c0f223421e453088fb11fb322d618d",
      "roomPassword": "114514",
      "roomName": "聊天室95c0f",
      "userContainer": {
        "userList": [
          {
            "avatarIndex": 31,
            "userId": "f7f121337453443eaa405d913d2cc00e",
            "userName": "用户f7f12",
            "cachingMessage": [
              {
                "content": "用户e07da加入了房间",
                "type": "system"
              }
            ],
            "lastHeartbeatTime": "2022-06-22T08:10:43.412+00:00"
          },
          {
            "avatarIndex": 30,
            "userId": "e07dac5372ad4f3488bbc41a37d94918",
            "userName": "用户e07da",
            "cachingMessage": [],
            "lastHeartbeatTime": "2022-06-22T08:10:52.211+00:00"
          }
        ]
      }
    }
  }
}
```

# Send Message

```json5
{
  "roomId": "95c0f223421e453088fb11fb322d618d",
  "userId": "e07dac5372ad4f3488bbc41a37d94918",
  "message": {
    "type": "message",
    "userId": "e07dac5372ad4f3488bbc41a37d94918",
    "contentType": "text",
    "content": "hello"
  }
}
```

```json5
{
  "success": true,
  "message": "发送成功",
  "data": null
}
```

# Fetch Message

```json5
{
  "roomId": "95c0f223421e453088fb11fb322d618d",
  "userId": "f7f121337453443eaa405d913d2cc00e"
}
```

```json5
{
  "success": true,
  "message": "你成功了！",
  "data": []
}
```
