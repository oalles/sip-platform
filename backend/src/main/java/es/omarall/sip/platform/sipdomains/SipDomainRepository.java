package es.omarall.sip.platform.sipdomains;

import com.redis.om.spring.repository.RedisDocumentRepository;
import com.redis.om.spring.repository.RedisEnhancedRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SipDomainRepository extends RedisDocumentRepository<SipDomain, String> {
    SipDomain findByName(String name);
    List<SipDomain> searchByName(String name);
}
