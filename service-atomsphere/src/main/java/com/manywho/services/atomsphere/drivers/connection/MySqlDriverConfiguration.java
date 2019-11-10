package com.manywho.services.sql.drivers.connection;

import com.manywho.services.sql.ServiceConfiguration;

public class MySqlDriverConfiguration implements DriverConfigurationInterface {

    public String getNoSslConnectionString(ServiceConfiguration serviceConfiguration) {
        return String.format("jdbc:mysql://%s:%s/%s",
                serviceConfiguration.getHost(),
                serviceConfiguration.getPort(),
                serviceConfiguration.getDatabaseName());
    }

    public String getSslConnectionString(ServiceConfiguration serviceConfiguration) {
        return String.format("jdbc:mysql://%s:%s/%s?useSSL=true&trustServerCertificate=true",
                serviceConfiguration.getHost(),
                serviceConfiguration.getPort(),
                serviceConfiguration.getDatabaseName());
    }

    public String getSslCertificateConnectionString(ServiceConfiguration serviceConfiguration) {
        return String.format("jdbc:mysql://%s:%s/%s?useSSL=true&trustServerCertificate=false&serverSslCert=%s",
                serviceConfiguration.getHost(),
                serviceConfiguration.getPort(),
                serviceConfiguration.getDatabaseName(),
                serviceConfiguration.getServerPublicCertificate());
    }
}
