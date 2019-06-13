package consumer.resources;


import consumer.core.GeneralConsumer;

import javax.imageio.ImageIO;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.ws.rs.core.Response;

@Path("image")
public class ImagePage {
    @GET
    @Produces("image/jpg")
    public Response getFullImage() {
        BufferedImage image = null;
        byte[] imageData = null;
//        try {
//            image = ImageIO.read(new File("0dog1.jpg"));
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ImageIO.write(image, "jpg", baos);
//            imageData = baos.toByteArray();
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }

        GeneralConsumer gc = new GeneralConsumer();
        gc.subscribeTo("dogs");
        imageData = gc.pullImage();

        // uncomment line below to send non-streamed
        return Response.status(Response.Status.OK).entity(imageData).build();
        // uncomment line below to send streamed
        // return Response.ok(new ByteArrayInputStream(imageData)).build();
    }
}
