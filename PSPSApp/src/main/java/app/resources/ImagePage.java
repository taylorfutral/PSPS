package app.resources;


import app.core.Consumer;
import app.core.Producer;
import app.logger.ServiceLogger;
import app.models.GenericResponseModel;
import app.models.ImageUploadRequestModel;
import app.models.TopicRequestModel;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import sun.misc.BASE64Decoder;
import sun.security.krb5.internal.MethodData;

import javax.imageio.ImageIO;
import javax.ws.rs.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("image")
public class ImagePage {
    @Path("get")
    @GET
    @Produces("image/jpg")
    public Response getFullImage() {
        BufferedImage image = null;
        byte[] imageData = null;

        imageData = Consumer.pullImage();

        if(imageData == null) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.status(Response.Status.OK).entity(imageData).build();
    }

    @Path("upload")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadImage(String jsonText) {
        ServiceLogger.LOGGER.info(jsonText);
        ImageUploadRequestModel requestModel = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            requestModel = mapper.readValue(jsonText, ImageUploadRequestModel.class);
            ServiceLogger.LOGGER.info("Image Data: " + requestModel.getImage());
            String imageDataBase64 = requestModel.getImage().substring(requestModel.getImage().indexOf(",")+1);
            byte[] imageData = Base64.decodeBase64(imageDataBase64);
            Producer.pushData(requestModel.getTopics(), imageData);
        }
        catch (IOException e) {
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
            }
            else {
                ServiceLogger.LOGGER.warning("IOException.");
            }
        }
        return Response.status(Response.Status.OK).entity("uploaded image").build();
    }
}
