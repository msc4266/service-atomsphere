package com.manywho.services.sql.drivers.connection;

import com.manywho.services.sql.ServiceConfiguration;
import com.manywho.services.sql.managers.SingleCertTrustManager;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SqlServerDriverConfiguration implements DriverConfigurationInterface {

    public String getNoSslConnectionString(ServiceConfiguration serviceConfiguration) {
        return String.format("jdbc:sqlserver://%s:%s;databaseName=%s",
                serviceConfiguration.getHost(),
                serviceConfiguration.getPort(),
                serviceConfiguration.getDatabaseName());
    }

    public String getSslConnectionString(ServiceConfiguration serviceConfiguration) {
        return String.format("jdbc:sqlserver://%s:%s;databaseName=%s;integratedSecurity=false;encrypt=true;trustServerCertificate=true",
                serviceConfiguration.getHost(),
                serviceConfiguration.getPort(),
                serviceConfiguration.getDatabaseName());
    }

    public String getSslCertificateConnectionString(ServiceConfiguration serviceConfiguration) {
        try {
            String certificate = URLEncoder.encode(serviceConfiguration.getServerPublicCertificate(), "UTF-8");

            return String.format("jdbc:sqlserver://%s:%s;databaseName=%s;integratedSecurity=false;encrypt=true;trustServerCertificate=false;trustManagerClass=%s;trustManagerConstructorArg=%s",
                    serviceConfiguration.getHost(),
                    serviceConfiguration.getPort(),
                    serviceConfiguration.getDatabaseName(),
                    SingleCertTrustManager.class.getName(),
                    certificate);

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
