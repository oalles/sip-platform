package es.omarall.sip.platform.credentials;

import com.redis.om.spring.repository.RedisDocumentRepository;
import com.redis.om.spring.repository.RedisEnhancedRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CredentialRepository extends RedisDocumentRepository<Credential, String> {

    List<Credential> findByDomain(String domain);
    List<Credential> searchByDomain(String domain);
    Credential findByUsernameAndDomain(String username, String domain);
}
