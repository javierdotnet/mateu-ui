package io.mateu.ui.core.rest;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("resources")
public class RestApplication extends ResourceConfig {
    public RestApplication() {
        System.out.println("POR AQUÍ TAMBIÉN PASAxxxx!!!!");

        //packages("org.foo.rest;org.bar.rest");
        packages("io.mateu.ui.rest");
        register(JacksonFeature.class);
        register(Converter1.class);
        register(new CORSFilter());
    }
}