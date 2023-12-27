package es.omarall.sip.platform.sipdomains;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SipDomainResource {
    private String id;
    private String name;
    private Instant dateCreated;
    private Instant dateUpdated;
}
