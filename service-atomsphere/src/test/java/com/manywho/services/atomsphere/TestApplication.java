package com.manywho.services.atomsphere;

import com.manywho.sdk.services.servers.Servlet3Server;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class TestApplication extends Servlet3Server {
    public TestApplication() {
        this.addModule(new ApplicationAtomsphereModule());
        this.setApplication(Application.class);
        this.start();
    }
}
