# idempotent-api-gateway

## Setup env
```sh
mise --version
# 2024.7.3 macos-arm64 (5d74b6b 2024-07-14)
mise install
```

## Run
```sh
./gradlew bootRun
```

```sh
curl localhost:8080/action1 -H "X-Idempotency-Key: 123"
```

## Misc
```sh
./gradlew spotlessApply
```

