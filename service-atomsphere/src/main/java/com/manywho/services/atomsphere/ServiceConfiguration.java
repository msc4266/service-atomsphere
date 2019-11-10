package com.manywho.services.atomsphere;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.configuration.Configuration;

public class ServiceConfiguration implements Configuration {
    @Configuration.Setting(name="Username", contentType= ContentType.String)
    private String username;

    @Configuration.Setting(name="Password", contentType= ContentType.Password)
    private String password;

    @Configuration.Setting(name="Account", contentType= ContentType.String)
    private String account;

    @Configuration.Setting(name = "Server Public Certificate", contentType = ContentType.String, required = false)
    private String serverPublicCertificate;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getServerPublicCertificate() {
        return serverPublicCertificate;
    }

	public String getAccount() {
		return account;
	}
}
