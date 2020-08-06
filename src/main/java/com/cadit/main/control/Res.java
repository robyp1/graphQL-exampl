package com.cadit.main.control;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.net.URI;

@Path("res")
public class Res {

    @Context
    UriInfo uriInfo;

    // url es: http://localhost:8080/graphQL-example/app/res/getHelloMsg/Roby
    @GET
    @Path("/getHelloMsg/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHelloMsg(@PathParam("name") String name){
        //redirect
        URI getMessageForUri = uriInfo.getBaseUriBuilder().path(MessageResponse.class).path(MessageResponse.class, "getMessageFor").build(name);
        return Response.status(Response.Status.SEE_OTHER).header(HttpHeaders.LOCATION,getMessageForUri).build();
    }
}
