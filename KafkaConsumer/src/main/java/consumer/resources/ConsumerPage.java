package consumer.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ConsumerPage {
    @Path("{topic}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecords(@PathParam("topic") String topic) {




        return Response.status(Response.Status.OK).entity("").build();
    }

    @Path("alltopics")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTopics() {




        return Response.status(Response.Status.OK).entity("").build();
    }
}
