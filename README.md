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
curl -v -u admin:password --digest -H 'Accept: application/json' -H 'Content-Type: application/json' -X POST http://localhost:11222/rest/v2/caches/profile -d "@./profile.json"
```

#### Get your cache

```bash
curl -u admin:password --digest -X GET http://localhost:11222/rest/v2/caches/profile
```

#### Cache configuration

```json
{
  "distributed-cache": {
    "mode": "SYNC",
    "owners": "2",
    "segments": "256",
    "capacity-factor": "1.0",
    "l1-lifespan": "5000",
    "statistics": "true",
    "encoding": {
      "media-type": "application/x-protostream"
    },
    "locking": {
      "isolation": "REPEATABLE_READ"
    },
    "transaction": {
      "mode": "FULL_XA",
      "locking": "OPTIMISTIC"
    },
    "expiration" : {
      "lifespan" : "5000",
      "max-idle" : "1000"
    },
    "memory": {
      "max-count": "1000000",
      "when-full": "REMOVE"
    },
    "partition-handling" : {
      "when-split" : "ALLOW_READ_WRITES",
      "merge-policy" : "PREFERRED_NON_NULL"
    },
    "persistence" : {
      "passivation" : false
    }
  }
}

```

#### Cache creation example

```bash
curl -u admin:password --digest -X POST http://localhost:11222/rest/v2/caches/character -d "@./conf/character.json"
```

#### Get your cache

```bash
curl -u admin:password --digest -X GET http://localhost:11222/rest/v2/caches/character
```
