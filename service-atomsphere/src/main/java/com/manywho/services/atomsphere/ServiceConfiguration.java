package com.manywho.services.atomsphere;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.configuration.Configuration;

public class ServiceConfiguration implements Configuration {
    @Configuration.Setting(name="Username", contentType= ContentType.String, required = false)
    private String username;

    @Configuration.Setting(name="Password", contentType= ContentType.Password, required = false)
    private String password;

    @Configuration.Setting(name="Account", contentType= ContentType.String)
    private String account;

	@Configuration.Setting(name="Use Identity Service Credentials", contentType= ContentType.Boolean, required = false)
    private Boolean useIDPCredentials;

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

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setServerPublicCertificate(String serverPublicCertificate) {
		this.serverPublicCertificate = serverPublicCertificate;
	}
    public Boolean useIDPCredentials() {
		return useIDPCredentials;
	}

	public void setUseIDPCredentials(Boolean useIDPCredentials) {
		this.useIDPCredentials = useIDPCredentials;
	}

}
