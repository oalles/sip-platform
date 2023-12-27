## Redis Stack Server and Kamailio SIP Server 

We are using Redis Stack Server, a Redis server with additional database capabilities powered by Redis modules.
Redis Stack bundles the following features into Redis: JSON, Search and Query, RedisGears V2, Time Series, and Probabilistic.

```yaml 
version: '3.9'
services:
  kamailio:
    image: kamailio/kamailio-ci:latest
    restart: always
    ports:
      - "5060:5060/udp"
      - "5060:5060/tcp"
      - "7000:7000/udp"
      - "7000:7000/tcp"
    environment:
      - DBENGINE=REDIS
      - REDIS_HOST=redis
      - REDIS_PORT=6379
      - DBNAME=0
    volumes:
      - ./config_files:/etc/kamailio/
    depends_on:
      - redis
  redis:
    image: 'redis/redis-stack-server:edge'
    ports:
      - '6379:6379'
    volumes:
      - ~/.redisData:/data
    environment:
      - REDIS_ARGS=--appendonly yes --appendfilename appendonly.aof --appendfsync always --loglevel verbose --notify-keyspace-events AKE
    deploy:
      replicas: 1
      restart_policy:
        condition: on-failure
```

## kamailio.cfg
Let s remark some configuration parameters in the kamailio.cfg file we have added or updated.

```txt

#!substdef "!MY_EXTERNAL_IP!sipplatform.com!g"

.... 

alias="sip.sipplatform.com"
alias="sipplatform.com"

#!ifdef WITH_REDIS
loadmodule "db_redis.so"
#!endif

#!ifdef WITH_JSONRPC
loadmodule "xhttp.so"
#!endif

# -------- redis params -----
modparam("db_redis", "schema_path", "/usr/share/kamailio/db_redis/kamailio")
modparam("db_redis", "keys", "version=entry:table_name")
modparam("db_redis", "keys", "location=entry:ruid&usrdom:username,domain&timer:partition,keepalive")
modparam("db_redis", "keys", "acc=entry:callid,time_hires&cid:callid")
modparam("db_redis", "keys", "subscriber=entry:username,domain")
modparam("db_redis", "keys", "domain=entry:domain")
modparam("db_redis", "keys", "domain_attrs=entry:did,name")

# ----- jsonrpcs params - enable http  -----
modparam("jsonrpcs", "pretty_format", 1)
modparam("jsonrpcs", "transport", 1)

# ----- auth_db params -----
#!ifdef WITH_AUTH
modparam("auth_db", "db_url", DBURL)
modparam("auth_db", "calculate_ha1", 0)
modparam("auth_db", "password_column", "ha1")
modparam("auth_db", "password_column_2", "ha1b")
modparam("auth_db", "use_domain", MULTIDOMAIN)

# ----- permissions params -----
#!ifdef WITH_IPAUTH
modparam("permissions", "db_url", DBURL)
modparam("permissions", "db_mode", 1)
#!endif
#!endif
```

## JSON RPC Domain Reload Request

Http JsonRPCs must be enabled for http command requests, so we can issue a [domain reload request](https://www.kamailio.org/docs/modules/devel/modules/domain.html#domain.rpc.reload) from our backend.

```text
POST http://localhost:5060/RPC
{
    "jsonrpc": "2.0",
    "method": "domain.reload",
    "id": 1 # increment this id for each request
}
```


