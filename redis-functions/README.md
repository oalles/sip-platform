# Redis Gears v2 libraries of triggers and functions

By leveraging the V8 Javascript Engine, [these scripts](index.js) will add real-time, efficient data handling and synchronization between our SIP Platform domain entities and Kamailio s entities.

## Prerequisites
* Node.js (v18.18.0) and npm(9.8.1) installed
* Redis Stack Server running with Key Space Notifications enabled: `CONFIG SET notify-keyspace-events KEA`

## Setting up the development environment 
For information on how to develop for triggers and functions, check this guide [here](https://github.com/RedisGears/RedisGears/blob/master/docs/docs/Development.md).

## Usage:
```bash
$ cd redis-functions
$ npm install
$ npm run deploy
> redis-functions@1.0.0 deploy
> gears-api index.js

Deployed! :)
```

## index.js Explanation

### keySpace Notifications Handlers

- `sipDomainKeySpaceEventsHandler()`: Handles events for SIPDomain keyspace changes, including auditing and synchronization with Kamailio's domain model.
- `credentialKeySpaceEventsHandler()`: Similar to the domain handler but for Credential entities, ensuring synchronization with Kamailio's subscriber model.

```javascript
// region SipPlatform Domain Event Handlers
const sipDomainKeySpaceEventsHandler = (client, data) => {

    log(`SipDomainKeySpaceEventHandler ${data.key} ${data.event}`);
    validateKeySpaceEvent(client, data.key, data.event);

    addAuditingMetadata(client, data.key);
    log(`Auditing metadata added to SIPDomain with key ${data.key}`);

    const kamailioId = getNextId(client, DOMAIN_SEQUENCER_KEY);
    addExternalId(client, data.key, kamailioId);
    log(`External ID ${kamailioId} added to SIPDomain with key ${data.key}`);

    // Extract domain name from our SipDomain entity
    const domainStr = client.call('JSON.GET', data.key, '$.name');
    const domain = JSON.parse(domainStr)[0];
    log(`SipDomain entity name: ${domain}`);

    // Build kamailio's domain entry
    const domainKey = `${DOMAIN_ENTRY_PREFIX}:${domain}`;
    const lastModified = moment().format('YYYY-MM-DD hh:mm:ss');
    log(`Kamailio Domain Values: ${domainKey} - ${kamailioId} - ${domain} - ${lastModified}`);

    // https://kamailio.org/docs/db-tables/kamailio-db-5.1.x.html#gen-db-domain
    client.call('HMSET', domainKey, 'id', kamailioId, 'domain', domain, 'did', domain, 'last_modified', lastModified);

    // Place a request to reload kamailio domain configuration via RPC
    client.call('XADD', 'reload:domain', '*', 'cmd', 'domain.reload');
};

const credentialKeySpaceEventsHandler = (client, data) => {

    // logInput(client, data);
    validateKeySpaceEvent(client, data.key, data.event);

    addAuditingMetadata(client, data.key);
    log(`Auditing metadata added to Credential with key ${data.key}`);

    const kamailioId = getNextId(client, SUBSCRIBER_SEQUENCER_KEY);
    addExternalId(client, data.key, kamailioId);
    log(`External ID ${kamailioId} added to Credential with key ${data.key}`);

    // Extract data from our Credential entity
    const credentialStr = client.call('JSON.GET', data.key);
    const credential = JSON.parse(credentialStr)[0];
    log(`Credential Entity: ${JSON.stringify(credential)}`);

    // log(`Response: ${response} - Response type: ${typeof response} - Response length: ${response.length}`);

    // Build kamailio subscriber entry
    const username = credential.username;
    const domain = credential.domain;
    const subscriberKey = `${SUBSCRIBER_ENTRY_PREFIX}:${username}:${domain}`;
    const ha1 = credential.ha1;
    const ha1b = credential.ha1b;
    log(`Username: ${username} - Domain: ${domain} - Subscriber Key: ${subscriberKey} - HA1: ${ha1} - HA1B: ${ha1b}`);
    // https://kamailio.org/docs/db-tables/kamailio-db-5.1.x.html#gen-db-subscriber
    client.call('hmset', subscriberKey, 'id', kamailioId, 'username', username, 'domain', domain, 'ha1', ha1, 'ha1b', ha1b);
};
```

### Registration of KeySpace Triggers

Register the keyspace notification handlers:

```javascript
redis.registerKeySpaceTrigger("sipDomainEventsHandler", SIP_DOMAIN_PREFIX, sipDomainKeySpaceEventsHandler, {description: "Adds auditing and Updates kamailio domain model."});
redis.registerKeySpaceTrigger("credentialEventsHandler", CREDENTIAL_PREFIX, credentialKeySpaceEventsHandler, {description: "Adds auditing and  Updates kamailio subscriber model."});
```

## Useful Links
* https://redis.com/blog/introducing-triggers-and-functions/
* https://redis.com/blog/database-trigger-features/
* https://redis.io/docs/manual/keyspace-notifications/
* https://github.com/RedisGears/RedisGears/tree/master/js_api
* https://github.com/RedisGears/RedisGears/blob/master/docs/docs/Development.md
