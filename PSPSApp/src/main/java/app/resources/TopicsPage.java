package app.resources;


import app.core.Consumer;
import app.logger.ServiceLogger;
import app.models.AllTopicsResponseModel;
import app.models.GenericResponseModel;
import app.models.TopicRequestModel;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import sun.net.www.content.text.Generic;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("topics")
public class TopicsPage {
    @Path("all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response allTopics() {
        ServiceLogger.LOGGER.info("");
        String[] topics = Consumer.getTopics();
        if(topics == null) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        AllTopicsResponseModel responseModel = new AllTopicsResponseModel(20, topics);

        return Response.status(Response.Status.OK).entity(responseModel)
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @Path("subscribe")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response subscribeToTopic(String jsonText) {
        ServiceLogger.LOGGER.info("Received request to subscribe to topic: " + jsonText);
        TopicRequestModel requestModel;
        GenericResponseModel responseModel;
        try {
            ObjectMapper mapper = new ObjectMapper();
            requestModel = mapper.readValue(jsonText, TopicRequestModel.class);
            ServiceLogger.LOGGER.info("subscription topic: " + requestModel.getTopic());

            Consumer.subscribeTo(requestModel.getTopic());

            responseModel = new GenericResponseModel(10, "subscribed");

        }
        catch (IOException e) {
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new GenericResponseModel(-1, "json mapping");
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new GenericResponseModel(-1, "json parsing");
            }
            else {
                ServiceLogger.LOGGER.warning("IOException.");
                responseModel = new GenericResponseModel(-1, "exception");
            }
        }

        return Response.status(Response.Status.OK).entity(responseModel).build();
    }
}
