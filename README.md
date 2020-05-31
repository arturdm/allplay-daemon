# allplay-daemon

Connects to available AllPlay devices and controls them via REST API.

Uses [Tchaikovsky] and [Ktor].

[Tchaikovsky]:https://github.com/dominicdesu/tchaikovsky
[Ktor]:https://ktor.io/

## Requirements

### Supported platforms

- Mac OS X (x86_64)
- Linux (x86, x86_64)

## Running released distribution

```bash
unzip allplay-daemon.zip
cd bin
./allplay-daemon
```

## Running locally

```bash
./gradlew run
```

## Build distribution

```bash
./gradlew distZip
```

## Communicating daemon

The daemon starts an embedded HTTP server on port 8765 (customisable with
`--port` parameter).

### Showing daemon status
```
GET /status
{
  "isConnected": true
}
```

### Listing speakers
```
GET /speakers
{
  "speakers": [
    {
      "id": "12345678-1234-1234-1234-123456789abc",
      "name": "D-STREAM",
      "input": {
        "activeInput": "",
        "availableInputs": [
          "Line-in"
        ]
      },
      "playlist": {
        "items": []
      },
      "volume": {
        "isMute": false,
        "volume": 34
      },
      "isConnected": true
    }
  ]
}
```

### Changing input
```
{
    "input": "Line-in"
}
POST /speakers/{id}/input

```
