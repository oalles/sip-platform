package es.omarall.sip.platform.sipdomains;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.Searchable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;

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

    @Indexed(sortable = true)
    private Instant dateCreated;
    @Indexed(sortable = true)
    private Instant dateUpdated;

    @Indexed
    private Integer externalId;
}
