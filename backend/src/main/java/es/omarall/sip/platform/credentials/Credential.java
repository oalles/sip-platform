package es.omarall.sip.platform.credentials;


import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.Searchable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

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

    @Indexed(sortable = true)
    private Instant dateCreated;
    @Indexed(sortable = true)
    private Instant dateUpdated;

    @Indexed
    private Integer externalId;

    public void setPassword(String password) {
        Assert.notNull(username, "Username cannot be null");
        Assert.notNull(domain, "Domain cannot be null");

        this.ha1 = this.buildHa1(username, domain, password);
        this.ha1b = this.buildHa1(username + "@" + domain, domain, password);
    }

    private String buildHa1(String... input) {
        try {
            String concatenated = String.join(":", input);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(concatenated.getBytes(StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }
}
