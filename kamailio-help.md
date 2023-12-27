## Figuring Out What Kamailio might need

Based on kamailio [domain module](https://kamailio.org/docs/db-tables/kamailio-db-5.2.x.html#idm2077) and [auth module](https://kamailio.org/docs/db-tables/kamailio-db-5.2.x.html#idm528)

#### Sample Domain
```text
DOMAIN fictsip.com

key: domain:entry::fictsip.com
id 1
domain fictsip.com
did fictsip.com
last_modified '2023-08-01 00:00:01'

hmset domain:entry::fictsip.com id 1 domain fictsip.com did fictsip.com last_modified '2023-08-01 00:00:01'
```

After a domain update, a [domain-reload](https://www.kamailio.org/docs/modules/devel/modules/domain.html#domain.rpc.reload) request is needed.


#### Sample Subscriber
```text
SUBSCRIBER omar

key: subscriber:entry::omar:fictsip.com
id 1
username omar
domain fictsip.com
password omar
ha1  2a56d2ab702bcbc9d3653bdd19c0c218   # echo -n "omar:fictsip.com:omar" | md5sum
ha1b a055baad119864775ed837aa87aad56e   # echo -n "omar@fictsip.com:fictsip.com:omar" | md5sum

hmset subscriber:entry::omar:fictsip.com id 1 username omar domain fictsip.com password omar ha1 2a56d2ab702bcbc9d3653bdd19c0c218 ha1b a055baad119864775ed837aa87aad56e
```


#### ZOIPER

```text
Domain fictsip.com
Username omar
Password omar

Use Outbound proxy: <your-ip>:5060
```

#### Links
https://github.com/miconda/md5ha1

#### Enable Key Space Notifications
You can use RedisInsights as GUI. in order to set: CONFIG SET notify-keyspace-events Kxs
See the configuration description here. https://redis.io/docs/manual/keyspace-notifications/#configuration
```text 
CONFIG SET notify-keyspace-events KEA
```