package io.mateu.ui.core.rest;

import io.mateu.ui.core.server.ServerSideHelper;
import io.mateu.ui.core.shared.BaseService;
import io.mateu.ui.core.shared.Data;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * Created by miguel on 6/6/17.
 */
@Path("/recurso1")
public class Recurso1 {


    public static BaseService s = ServerSideHelper.findImplementation(BaseService.class);



    @GET @Path("/eco")
    public String eco(@QueryParam("msg") String msg) {
        return msg;
    }


    @POST @Path("/eco2")
    public Data eco2(@QueryParam("msg") String msg, @QueryParam("data") Data data) {
        return data;
    }

}
