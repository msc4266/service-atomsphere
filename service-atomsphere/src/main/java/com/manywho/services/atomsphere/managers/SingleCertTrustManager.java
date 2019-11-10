package com.manywho.services.atomsphere.managers;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.UUID;

/**
 * @author this class is from the driver documentation 6.3.3.jre8-preview source https://github.com/Microsoft/mssql-jdbc/wiki/Custom-TrustManager
 *
 * We are currently using the driver 6.3.3.jre8-preview and this class is  not included, at least not documented
 * like with the others drivers
 *
 *  ToDo: If this class is included in future versions of the driver we should use the official one and delete this one
 *
 */
public class SingleCertTrustManager implements X509TrustManager {
    private X509Certificate certificate;
    private X509TrustManager trustManager;

    public SingleCertTrustManager(String certificateAsString) throws IOException, GeneralSecurityException {
        String certDecode = URLDecoder.decode(certificateAsString, "UTF-8");
        InputStream inputStreamCertificate = new ByteArrayInputStream(certDecode.getBytes());
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            // Note: KeyStore requires it be loaded even if you don't load anything into it:
            keyStore.load(null);
        } catch (Exception e) {
        }
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
        certificate = (X509Certificate) certificateFactory.generateCertificate(inputStreamCertificate);
        keyStore.setCertificateEntry(UUID.randomUUID().toString(), certificate);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        for (TrustManager tm : trustManagerFactory.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                trustManager = (X509TrustManager) tm;
                break;
            }
        }
        if (trustManager == null) {
            throw new GeneralSecurityException("No X509TrustManager found");
        }
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        trustManager.checkServerTrusted(chain, authType);
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[] {certificate};
    }
}
