#!js name=lib api_version=1.0

import {redis} from '@redis/gears-api';
import moment from 'moment';
// import * as http from "http";

const SIP_DOMAIN_PREFIX = "sipdomain:";
const CREDENTIAL_PREFIX = "credential:";
const DOMAIN = 'domain';
const SUBSCRIBER = 'subscriber';
const SUBSCRIBER_ENTRY_PREFIX = `${SUBSCRIBER}:entry:`;
const DOMAIN_ENTRY_PREFIX = `${DOMAIN}:entry:`;
const DOMAIN_SEQUENCER_KEY = `id:sequence:${DOMAIN}`
const SUBSCRIBER_SEQUENCER_KEY = `id:sequence:${SUBSCRIBER}`;

//region Extending my Sip Domain Model Entities
const addAuditingMetadata = (client, key) => {
    // const now = moment().toISOString();
    const now = new Date().getTime();
    const auditingMetadata = {
        "dateCreated": now,
        "dateUpdated": now
    };
    client.call('json.merge', key, '$', JSON.stringify(auditingMetadata));
};

/**
 * External ID is the ID used by Kamailio to identify the entity.
 * @param client
 * @param key
 * @param externalId
 */
const addExternalId = (client, key, externalId) => {
    client.call('json.set', key, '$.externalId', externalId);
};
//endregion

// region SipPlatform Domain Event Handlers
const sipDomainKeySpaceEventsHandler = (client, data) => {

    // log(`Auditing metadata added to SIPDomain with key ${data.key} - Event: ${data.event}`);

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

// endregion

//region Key Space Event Handlers
redis.registerKeySpaceTrigger("sipDomainEventsHandler", SIP_DOMAIN_PREFIX, sipDomainKeySpaceEventsHandler, {description: "Adds auditing and Updates kamailio domain model."});
redis.registerKeySpaceTrigger("credentialEventsHandler", CREDENTIAL_PREFIX, credentialKeySpaceEventsHandler, {description: "Adds auditing and  Updates kamailio subscriber model."});
//endregion

// region Utils
const log = (message) => {
    redis.log(message);
};

const getNextId = (client, prefix) => '' + client.call('INCR', prefix);

const validateKeySpaceEvent = (client, key, event) => {
    // TODO: Validate key and event
    if (event !== 'json.set') {
        throw new Error(`Not json.set event`);
    }
};
// endRegion

//region Kamailio Domain Reload
const kamailioDomainReloadRequester = (client, data) => {

    // https://www.kamailio.org/docs/modules/devel/modules/domain.html#domain.rpc.reload
    // https://www.kamailio.org/docs/docbooks/3.2.x/rpc_api/rpc_api.html

    const requestId = client.call('INCR', 'requests:requestId');
    log(`Received reload request. RequestId: ${requestId}`);

    const url = 'http://kamailio:5060/RPC';
    const payload = {
        'jsonrpc': '2.0',
        'method': 'domain.reload',
        'id': parseInt(requestId)
    };

    log('Sending Domain reload request...');
    // http.post(url, payload, (err, res) => {
    //         if (err) {
    //             log(`Error sending reload request: ${err}`);
    //         } else {
    //             log('Reload request sent successfully.');
    //         }
    //     }
    // );
};
redis.registerStreamTrigger('kamailioDomainReloadRequester', 'reload:domain', kamailioDomainReloadRequester,
    {
        isStreamTrimmed: true,
        window: 1
    });
// endregion