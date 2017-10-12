package io.mateu.ui.core.rest;

/**
 * Created by miguel on 23/7/17.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

/**
 * Registers a customized {@link JacksonJaxbJsonProvider}.
 */
public class JacksonFeature implements Feature {

    private static final ObjectMapper mapper =
            new ObjectMapper(){{
                registerModule(new JavaTimeModule());
                //registerModule(new GuavaModule());  // or whatever you want...

                // We want ISO dates, not Unix timestamps!:
                configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            }};

    private static final JacksonJaxbJsonProvider provider =
            new JacksonJaxbJsonProvider(){{
                setMapper(mapper);
            }};

    /** This method is what actually gets called,
     when your ResourceConfig registers a Feature. */
    @Override
    public boolean configure(FeatureContext context) {

        System.out.println("CONFIGURANDO JacksonFeature...");

        context.register(provider);
        return true;
    }
}