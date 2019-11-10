package com.manywho.services.sql.drivers.connection;

import com.google.common.base.Strings;
import com.manywho.services.sql.ServiceConfiguration;

public interface DriverConfigurationInterface {

    String getNoSslConnectionString(ServiceConfiguration serviceConfiguration);
    String getSslConnectionString(ServiceConfiguration serviceConfiguration);
    String getSslCertificateConnectionString(ServiceConfiguration serviceConfiguration);

    default String getStringConnection(ServiceConfiguration serviceConfiguration) {
        if (serviceConfiguration.getNoUseSsl()) {
            return this.getNoSslConnectionString(serviceConfiguration);
        } else if (Strings.isNullOrEmpty(serviceConfiguration.getServerPublicCertificate())) {
            return this.getSslConnectionString(serviceConfiguration);
        } else {
            return this.getSslCertificateConnectionString(serviceConfiguration);
        }
    }
}
