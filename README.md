# idempotent-api-gateway

## Setup env
```sh
mise --version
# 2024.7.3 macos-arm64 (5d74b6b 2024-07-14)
mise install
```

### Run on Local
```sh
./gradlew build
docker compose up -d --build
```

```sh
curl localhost:8080/action1 -H "X-Idempotency-Key: 123"
```

```sh
k6 run ./benchmarks/docker.js
```
