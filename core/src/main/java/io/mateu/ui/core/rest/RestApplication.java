package io.mateu.ui.core.rest;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("resources")
public class RestApplication extends ResourceConfig {
    public RestApplication() {
        //packages("org.foo.rest;org.bar.rest");
        packages("io.mateu.ui.rest");
        register(JacksonFeature.class);
        register(new CORSFilter());
    }
}