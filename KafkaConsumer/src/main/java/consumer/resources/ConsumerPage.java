package consumer.resources;

import consumer.core.GeneralConsumer;
import consumer.logger.ServiceLogger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("{topic}")
public class ConsumerPage {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecords(@PathParam("topic") String topic) {
        ServiceLogger.LOGGER.info("======== Topic Endpoint Accessed (" + topic + ") ========");
        GeneralConsumer gc = new GeneralConsumer();
        gc.subscribeTo(topic);
        gc.pullData();

        return Response.status(Response.Status.OK).entity(topic).build();
    }

    @Path("alltopics")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTopics() {




        return Response.status(Response.Status.OK).entity("").build();
    }
}
