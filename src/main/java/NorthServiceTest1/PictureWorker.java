package NorthServiceTest1;

import org.json.JSONObject;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.OutputStream;

@Path("/")
public class PictureWorker {
    static String path="C:\\java_projects\\";
    @Produces("image/jpeg")
    @GET
    public static Response getPic(@QueryParam("id") String d){
        try {
            StringBuilder stringBuilder=new StringBuilder(path);
            stringBuilder.append(d);
            stringBuilder.append(".jpg");
            File f = new File(stringBuilder.toString());
            if(f.exists()){
                BufferedImage bi = ImageIO.read(f);
                return Response.ok(bi).build();
            }else {
                return Response.noContent().build();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return Response.noContent().build();
    }
    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String receive(@FormParam("id") String id,@FormParam("pic64") String pic) {
        int y=0;
        try {
            BufferedImage image = null;
            byte[] imageByte;

            BASE64Decoder decoder = new BASE64Decoder();
            imageByte = decoder.decodeBuffer(pic);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();

// write the image to a file
            StringBuilder stringBuilder=new StringBuilder(path);
            stringBuilder.append(id);
            stringBuilder.append(".jpg");
            File outputfile = new File(stringBuilder.toString());
            ImageIO.write(image, "jpeg", outputfile);
        }catch (Exception e){
            e.printStackTrace();
        }
        JSONObject object=new JSONObject();
        object.put("result",1);
        System.err.println(object.toString());
        return object.toString();
    }
}
