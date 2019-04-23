package NorthServiceTest1;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("")
public class EntryPoint {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String t(){
        return "test";
    }
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String t1(){
        return "test2";
    }
}
