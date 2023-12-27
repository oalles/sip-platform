package es.omarall.sip.platform.credentials;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CredentialResource {
    private String id;
    private String username;
    private String password;
    private String domain;
    private Instant dateCreated;
    private Instant dateUpdated;
}
