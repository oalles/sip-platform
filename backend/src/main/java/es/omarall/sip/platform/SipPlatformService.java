package es.omarall.sip.platform;

import es.omarall.sip.platform.credentials.Credential;
import es.omarall.sip.platform.credentials.CredentialRepository;
import es.omarall.sip.platform.credentials.CredentialResource;
import es.omarall.sip.platform.exceptions.RequestValidationException;
import es.omarall.sip.platform.sipdomains.SipDomain;
import es.omarall.sip.platform.sipdomains.SipDomainRepository;
import es.omarall.sip.platform.sipdomains.SipDomainResource;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SipPlatformService {

    private final SipDomainRepository sipDomainRepository;
    private final CredentialRepository credentialRepository;

    // region Creators
    public SipDomainResource createSipDomain(SipDomainResource sipDomainResource) {

        if (StringUtils.isEmpty(sipDomainResource.getName())) {
            throw new RequestValidationException("InvalidField", "Name field is missing", "name");
        }

        if (sipDomainRepository.findByName(sipDomainResource.getName())!= null) {
            throw new RequestValidationException("DomainException", "Domain already exists");
        }

        SipDomain sipDomain = SipDomain.builder()
                .id("sd-" + UUID.randomUUID())
                .name(sipDomainResource.getName())
                .build();

        sipDomainRepository.save(sipDomain);
        return sipDomainRepository.findById(sipDomain.getId())
                .map(this::buildFromSipDomain)
                .orElseThrow(NoSuchElementException::new);

    }

    public CredentialResource createCredential(CredentialResource credentialResource) {

        if (StringUtils.isEmpty(credentialResource.getDomain())) {
            throw new RequestValidationException("InvalidField", "domain field is missing", "domain");
        }

        if (StringUtils.isEmpty(credentialResource.getUsername())) {
            throw new RequestValidationException("InvalidField", "username field is missing", "username");
        }

        if (StringUtils.isEmpty(credentialResource.getPassword())) {
            throw new RequestValidationException("InvalidField", "password field is missing", "password");
        }

        if (credentialRepository.findByUsernameAndDomain(credentialResource.getUsername(), credentialResource.getDomain()) != null) {
            throw new RequestValidationException("CredentialException", "Credential already exists");
        }

        Credential credential = Credential.builder()
                .id("cr-" + UUID.randomUUID())
                .username(credentialResource.getUsername())
                .domain(credentialResource.getDomain())
                .build();
        credential.setPassword(credentialResource.getPassword());

        credentialRepository.save(credential);
        return credentialRepository.findById(credential.getId())
                .map(this::buildFromCredential)
                .orElseThrow(NoSuchElementException::new);
    }
    // endregion

    // region Getters
    public List<SipDomainResource> getAllDomains() {
        return this.sipDomainRepository.findAll().stream().map(this::buildFromSipDomain).collect(Collectors.toList());
    }

    public List<SipDomainResource> searchDomainsByName(String name) {
        if(name.length() < 2) {
            throw new RequestValidationException("InvalidField", "Min domain length is 2", "name");
        }
        return(this.sipDomainRepository.searchByName(name + "*").stream()
                .map(this::buildFromSipDomain).collect(Collectors.toList()));
    }

    public List<CredentialResource> searchCredentialsByDomain(String domain) {
        if(domain.length() < 2) {
            throw new RequestValidationException("InvalidField", "Min domain name length is 2", "domain");
        }
        return this.credentialRepository.searchByDomain(domain + "*").stream()
                .map(this::buildFromCredential).toList();
    }

    public List<CredentialResource> getCredentialsByDomain(String domain) {
        return this.credentialRepository.findByDomain(domain).stream()
                .map(this::buildFromCredential).toList();
    }

    public List<CredentialResource> getAllCredentials() {
        return this.credentialRepository.findAll().stream().map(this::buildFromCredential).toList();
    }
    // endregion


    // region Converters
    private SipDomainResource buildFromSipDomain(SipDomain sipDomain) {
        return SipDomainResource.builder()
                .id(sipDomain.getId())
                .name(sipDomain.getName())
                .dateCreated(sipDomain.getDateCreated())
                .dateUpdated(sipDomain.getDateUpdated())
                .build();
    }

    private CredentialResource buildFromCredential(Credential credential) {
        return CredentialResource.builder()
                .id(credential.getId())
                .username(credential.getUsername())
                .domain(credential.getDomain())
                .dateCreated(credential.getDateCreated())
                .dateUpdated(credential.getDateUpdated())
                .build();
    }
    // endregion

}
