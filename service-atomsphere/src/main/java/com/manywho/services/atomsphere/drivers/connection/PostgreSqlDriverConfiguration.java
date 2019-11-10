package com.manywho.services.sql.drivers.connection;

import com.manywho.services.sql.ServiceConfiguration;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class PostgreSqlDriverConfiguration implements DriverConfigurationInterface {

    public String getNoSslConnectionString(ServiceConfiguration serviceConfiguration) {
        return String.format("jdbc:postgresql://%s:%s/%s",
                serviceConfiguration.getHost(),
                serviceConfiguration.getPort(),
                serviceConfiguration.getDatabaseName());
    }

    public String getSslConnectionString(ServiceConfiguration serviceConfiguration) {
        return String.format("jdbc:postgresql://%s:%s/%s?ssl=true&sslmode=allow&sslfactory=org.postgresql.ssl.NonValidatingFactory",
                serviceConfiguration.getHost(),
                serviceConfiguration.getPort(),
                serviceConfiguration.getDatabaseName());
    }

    public String getSslCertificateConnectionString(ServiceConfiguration serviceConfiguration) {
        try {
            String certificate = URLEncoder.encode(serviceConfiguration.getServerPublicCertificate(), "UTF-8");

            return String.format("jdbc:postgresql://%s:%s/%s?ssl=true&sslmode=verify-full&sslfactory=org.postgresql.ssl.SingleCertValidatingFactory&sslfactoryarg=%s",
                    serviceConfiguration.getHost(),
                    serviceConfiguration.getPort(),
                    serviceConfiguration.getDatabaseName(),
                    certificate);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
