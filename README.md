# battle-chat
Gamificated Chat

## HOW TO

### Run infinispan (cache) server

```shell
podman run -d -p 11222:11222 -e USER="admin" -e PASS="password" --net=host quay.io/infinispan/server:13.0
```

### Run web service

```shell
# dev mode
./mvnw compile quarkus:dev
```

### Creating Caches


```shell
POST /rest/v2/caches/{cacheName}
```


The following "standard" formats are interchangeable:

application/x-java-object

application/octet-stream

application/x-www-form-urlencoded

text/plain

#### Cache creation example

```bash
curl -v -u admin:password --digest -H 'Accept: application/json' -H 'Content-Type: application/json' -X POST http://localhost:11222/rest/v2/caches/character -d "@./conf/character.json"
```

#### Get your cache

```bash
curl -u admin:password --digest -X GET http://localhost:11222/rest/v2/caches/character
```

#### Cache configuration

```json
{
  "distributed-cache": {
    "mode": "SYNC",
    "encoding": {
      "media-type": "application/x-protostream"
    },
    "statistics": true
  }
}

```