# battle-chat
Gamificated Chat

## HOW TO

### Run infinispan (cache) server

'''shell
podman run -d -p 11222:11222 -e USER="admin" -e PASS="password" --net=host quay.io/infinispan/server:13.0
'''

### Run web service

'''shell
# dev mode
./mvnw compile quarkus:dev
'''