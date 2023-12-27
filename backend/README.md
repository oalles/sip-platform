# Java Backend for SIP Domain Management Using Spring OM Redis

## Description

Backend service designed for SIP domain management, utilizing the powerful Spring OM Redis. 
It provides the necessary functionalities to create SIP domains and credentials, ensuring integration with our SIP server.

## SipPlatform Java Backend
We have two central entities:

#### SipDomain
Borrowing SIP Twilio description `A SIP Domain resource describes a custom DNS hostname that can accept SIP traffic for your account`.

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("sipdomain")
@TypeAlias("sipdomain")
public class SipDomain {
    @Id
    private String id;
    @Searchable
    private String name;
    @Indexed
    private Instant dateCreated;
    @Indexed
    private Instant dateUpdated;
    @Indexed
    private Integer externalId;
}
```

#### Credential
Contains the credentials required for a user to connect via SIP. In Twilio's Sip api words
`The Credential resource stores usernames and password information. These credentials are used to authenticate SIP endpoints.`

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("credential")
@TypeAlias("credential")
public class Credential {
    @Id
    private String id;
    @Searchable
    private String username;
    @Searchable
    private String domain;
    private String ha1;
    private String ha1b;
    
    @Indexed
    private Instant dateCreated;
    @Indexed
    private Instant dateUpdated;
    @Indexed
    private Integer externalId;
}
```

// TODO: Explicar @Document annotation, relacionar con @EnableDocumentRepositories, explicar los índices que se crean, y las posibilidades de FullText Search, VSS, fuzzy search, etc.

## Prerequisites

- Java JDK 11 or later
- Maven or Gradle
- Redis Stack and Redis Insight

## Setup and Installation

1. **Clone the repository:**

```bash
   $ git clone https://github.com/oalles/sip-platform.git
   $ cd sip-platform/backend
   $ mvn clean install; mvn spring-boot:run
```
   