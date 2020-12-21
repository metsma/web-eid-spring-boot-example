/*
 * Copyright (c) 2020 The Web eID Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.webeid.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.webeid.security.nonce.NonceGenerator;
import org.webeid.security.nonce.NonceGeneratorBuilder;
import org.webeid.security.validator.AuthTokenValidator;
import org.webeid.security.validator.AuthTokenValidatorBuilder;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.MutableConfiguration;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Configuration
public class ValidationConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(ValidationConfiguration.class);
    private static final String CACHE_NAME = "nonceCache";

    @Bean
    public Cache<String, LocalDateTime> nonceCache() {
        CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
        Cache<String, LocalDateTime> cache = cacheManager.getCache(CACHE_NAME);

        if (cache == null) {
            CompleteConfiguration<String, LocalDateTime> cacheConfig = new MutableConfiguration<String, LocalDateTime>().setTypes(String.class, LocalDateTime.class);
            cache = cacheManager.createCache(CACHE_NAME, cacheConfig);
        }
        return cache;
    }

    @Bean
    public NonceGenerator generator() {
        return new NonceGeneratorBuilder()
                .withNonceCache(nonceCache())
                .build();
    }

    @Bean
    public X509Certificate[] trustedCertificateAuthorities() {
        List<X509Certificate> caCertificates = new ArrayList<>();

        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("/certs/*.cer");

            for (Resource resource : resources) {
                X509Certificate caCertificate = (X509Certificate) certFactory.generateCertificate(resource.getInputStream());
                caCertificates.add(caCertificate);
            }

        } catch (CertificateException | IOException e) {
            LOG.error("Error initializing trusted CA certificates.", e);
            throw new RuntimeException("Error initializing trusted CA certificates.", e);
        }

        return caCertificates.toArray(new X509Certificate[0]);
    }

    @Bean
    public X509Certificate[] initializeTrustedCACertificatesFromKeyStore() {
        List<X509Certificate> caCertificates = new ArrayList<>();

        try (InputStream is = ValidationConfiguration.class.getResourceAsStream("/certs/trusted_certificates.jks")) {
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(is, yamlConfig().getKeystorePassword().toCharArray());
            Enumeration<String> aliases = keystore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                X509Certificate certificate = (X509Certificate) keystore.getCertificate(alias);
                caCertificates.add(certificate);
            }
        } catch (IOException | CertificateException | KeyStoreException | NoSuchAlgorithmException e) {
            LOG.error("Error initializing trusted CA certificates from trust store.", e);
            throw new RuntimeException("Error initializing trusted CA certificates from trust store.", e);
        }

        return caCertificates.toArray(new X509Certificate[0]);
    }


    @Bean
    public AuthTokenValidator validator() {
        return new AuthTokenValidatorBuilder()
                .withSiteOrigin(URI.create(yamlConfig().getLocalOrigin()))
                // FIXME: decide what should validation library do when cert fingerprint validation is enabled but fingerprint is null (as in Chrome)
//                .withSiteCertificateSha256Fingerprint(yamlConfig().getFingerprint())
                .withNonceCache(nonceCache())
                .withTrustedCertificateAuthorities(trustedCertificateAuthorities())
                .withTrustedCertificateAuthorities(initializeTrustedCACertificatesFromKeyStore())
                // FIXME: investigate why for Idemia test card OcspUtils.ocspUri() -> null so that "User certificate revocation check has failed: The CA/certificate doesn't have an OCSP responder"
                .withoutUserCertificateRevocationCheckWithOcsp()
                .build();
    }

    @Bean
    public YAMLConfig yamlConfig() {
        return new YAMLConfig();
    }
}
