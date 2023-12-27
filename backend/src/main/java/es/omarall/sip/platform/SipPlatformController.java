package es.omarall.sip.platform;


import es.omarall.sip.platform.credentials.CredentialResource;
import es.omarall.sip.platform.sipdomains.SipDomainResource;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/api/sip-platform")
@RequiredArgsConstructor
public class SipPlatformController {
    private final SipPlatformService sipPlatformService;

    @GetMapping("/domains")
    public List<SipDomainResource> getAllDomainsByName(@RequestParam(required = false) String name) {
        if (StringUtils.hasText(name)) {
            return sipPlatformService.searchDomainsByName(name);
        }
        return sipPlatformService.getAllDomains();
    }

    @PostMapping("/domains")
    public SipDomainResource createDomain(@RequestBody SipDomainResource sipDomainResource) {
        return sipPlatformService.createSipDomain(sipDomainResource);
    }

    @GetMapping("/domains/{domain}/credentials")
    public List<CredentialResource> getAllCredentialsByDomain(@PathVariable String domain) {
        return sipPlatformService.getCredentialsByDomain(domain);
    }

    @GetMapping("/credentials")
    public List<CredentialResource> searchCredentials(@RequestParam(required = false) String domain) {
        if (StringUtils.hasText(domain)) {
            return sipPlatformService.searchCredentialsByDomain(domain);
        }
        return sipPlatformService.getAllCredentials();
    }

    @PostMapping("/domains/{domain}/credentials")
    public CredentialResource createCredentialByDomain(@RequestBody CredentialResource credentialResource, @PathVariable String domain) {
        credentialResource.setDomain(domain);
        return sipPlatformService.createCredential(credentialResource);
    }

    @PostMapping("/credentials")
    public CredentialResource createCredential(@RequestBody CredentialResource credentialResource) {
        return sipPlatformService.createCredential(credentialResource);
    }
}
