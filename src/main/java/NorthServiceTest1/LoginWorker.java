package NorthServiceTest1;

import DataBase.MariaDataBaseConnector;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("")
public class LoginWorker {
    @Produces(MediaType.TEXT_PLAIN)
    @GET
    public String signIn(@HeaderParam("login") String l, @HeaderParam("password") String p) {
        try {
            String login = "";
            String pass = "";
            if (l != null) {
                login = URLDecoder.decode(l, "UTF-8");
            }
            if (p != null) {
                pass = URLDecoder.decode(p, "UTF-8");
            }
            JSONObject object = new JSONObject();
            object.put("login", login);
            object.put("password", pass);
            int i=isInDb(login,pass);
            object.put("id",i);
            if (i > 0) {
                object.put("result", "success");
//                return URLEncoder.encode(object.toString(),"UTF-8");
                return object.toString();
            } else {

                object.put("result", "fail");
                return object.toString();
//                return URLEncoder.encode(object.toString(),"UTF-8");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return "";
    }

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String signUp(@FormParam("login") String l, @FormParam("password") String p) {
        try {
            String login = URLDecoder.decode(l, "UTF-8");
            String pass = URLDecoder.decode(p, "UTF-8");
            System.err.println(login + " " + pass + " ");
            JSONObject object = new JSONObject();
            object.put("login", login);
            object.put("password", pass);

            if (isInDb(login, pass) <= 0) {
                object.put("result", "success");
                object.put("id",sign(login, pass));
                return object.toString();
            } else {

                object.put("result", "fail");
                return object.toString();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return "";
    }

    public static int isInDb(String login, String pass) {
        int y = 0;
        Connection connection = MariaDataBaseConnector.getConnection();
        try {
            if (!connection.isClosed()) {
                connection.prepareStatement("create table if not  exists users\n" +
                        "(\n" +
                        "\tid int auto_increment,\n" +
                        "\tlogin varchar(45) not null,\n" +
                        "\tpassword varchar(45) not null,\n" +
                        "\tconstraint users_pk\n" +
                        "\t\tprimary key (id)\n" +
                        ");").execute();
                PreparedStatement p = connection.prepareStatement("SELECT * FROM users WHERE login=? and password=?");
                p.setString(1, login);
                p.setString(2, pass);
                ResultSet rs = p.executeQuery();
                if (rs.next()) {
                    connection.close();
                    int i = rs.getInt(1);
                    return rs.getInt(1);
                } else {
                    connection.close();
                    return -1;
                }
            } else return -2;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return -1;
    }

    private static int sign(String login, String pass) {
        Connection connection = MariaDataBaseConnector.getConnection();
        try {
            if (!connection.isClosed()) {
                PreparedStatement p;
                p = connection.prepareStatement("INSERT INTO users(login,password) VALUES (?,?);");
                p.setString(1, login);
                p.setString(2, pass);
                p.execute();
                ProfileWorker.userInfoInit(connection);
                p = connection.prepareStatement(
                        "insert into userinfo (login,name,surname,profit,cars,email) values(?,'','',0,0,'email');");
                p.setString(1, login);
//                    p.setString(2,email);
                p.execute();

                connection.close();
                return isInDb(login,pass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
