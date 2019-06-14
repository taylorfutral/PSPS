package app.resources;


import app.core.Consumer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.awt.image.BufferedImage;
import javax.ws.rs.core.Response;

@Path("image")
public class ImagePage {
    @GET
    @Produces("image/jpg")
    public Response getFullImage() {
        BufferedImage image = null;
        byte[] imageData = null;

        imageData = Consumer.pullImage();

        return Response.status(Response.Status.OK).entity(imageData).build();
    }
}
