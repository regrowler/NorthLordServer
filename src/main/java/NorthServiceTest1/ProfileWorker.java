package NorthServiceTest1;

import DataBase.MariaDataBaseConnector;
import org.eclipse.jetty.util.UrlEncoded;
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
public class ProfileWorker {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public static String getUserInfo(@HeaderParam("login") String l, @HeaderParam("password") String p) {
        try {
            String login=URLDecoder.decode(l,"UTF-8");String pass=URLDecoder.decode(p,"UTF-8");
            if(LoginWorker.isInDb(login,pass)>0){
                String res=getuser(l);
                return URLDecoder.decode(res,"UTF-8");
            }
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
        return "";
    }
    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public static String update(@FormParam("login") String l,
                                @FormParam("password")String p,
                                @FormParam("email")String email,
                                @FormParam("name")String name,
                                @FormParam("surname")String surname){
        try {
            System.err.println("update");
            String login=URLDecoder.decode(l,"UTF-8");String pass=URLDecoder.decode(p,"UTF-8");
            String n=URLDecoder.decode(name,"UTF-8");String s=URLDecoder.decode(surname,"UTF-8");
            String em=URLDecoder.decode(email,"UTF-8");
            if(LoginWorker.isInDb(login,pass)>0){
                if(updateUser(n,s,em,login)==0){
                    return "succ";
                }else return "fail";
            }
        }catch (Exception e){
            System.err.println(e.toString());
        }
        return "fail";
    }
    public static int updateUser(String name,String surname,String email,String login){
        Connection connection= MariaDataBaseConnector.getConnection();
        int i=0;
        try {
            PreparedStatement preparedStatement=connection.prepareStatement("update userinfo set name=?,surname=?,email=? where login=?");
            preparedStatement.setString(1,name);
            preparedStatement.setString(2,surname);
            preparedStatement.setString(3,email);
            preparedStatement.setString(4,login);
            preparedStatement.execute();
            connection.close();
            return 0;
        }
        catch (Exception e){
            System.err.println(e.toString());
        }

        return -1;
    }
    public static String getuser(String l) {
        Connection connection = MariaDataBaseConnector.getConnection();
        try {
            PreparedStatement p = connection.prepareStatement("SELECT * FROM userinfo WHERE login=?");
            p.setString(1, l);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", URLEncoder.encode(rs.getString(3), "UTF-8"));
                jsonObject.put("surname", URLEncoder.encode(rs.getString(4), "UTF-8"));
                jsonObject.put("profit", rs.getInt(5));
                jsonObject.put("cars", rs.getInt(6));
                jsonObject.put("email", URLEncoder.encode(rs.getString(7), "UTF-8"));
                connection.close();
                return jsonObject.toString();
            }
            connection.close();
            return "fail";
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return "fail";
    }
    public static void updateCarsCounterInc(String login)throws Exception{
        Connection connection= MariaDataBaseConnector.getConnection();
        int i=0;
        try {
            PreparedStatement p = connection.prepareStatement("SELECT * FROM userinfo WHERE login=?");
            p.setString(1, login);
            ResultSet rs = p.executeQuery();
            if(rs.next()){
                PreparedStatement preparedStatement=connection.prepareStatement("update userinfo set cars=? where login=?");
                i=rs.getInt(6)+1;
                preparedStatement.setInt(1,i);
                preparedStatement.setString(2,login);
                preparedStatement.execute();
            }
            connection.close();
        }catch (Exception e){
            System.err.println(e.toString());
            throw e;
        }
    }
    public static void updateCarsCounterDec(String login,int i)throws Exception{
        Connection connection= MariaDataBaseConnector.getConnection();
        int j=0;
        try {
            PreparedStatement p = connection.prepareStatement("SELECT * FROM userinfo WHERE login=?");
            p.setString(1, login);
            ResultSet rs = p.executeQuery();
            if(rs.next()){
                PreparedStatement preparedStatement=connection.prepareStatement("update userinfo set cars=? where login=?");
                j=rs.getInt(6)-i;
                preparedStatement.setInt(1,j);
                preparedStatement.setString(2,login);
                preparedStatement.execute();
            }
            connection.close();
        }catch (Exception e){
            System.err.println(e.toString());
            throw e;
        }
    }
    public static void updateMoneyCounter(String log,int i)throws Exception{
        Connection connection= MariaDataBaseConnector.getConnection();
        int j=0;
        try {
            PreparedStatement p = connection.prepareStatement("SELECT * FROM userinfo WHERE login=?");
            p.setString(1, log);
            ResultSet rs = p.executeQuery();
            if(rs.next()){
                PreparedStatement preparedStatement=connection.prepareStatement("update userinfo set profit=? where login=?");
                j=rs.getInt(5)+i;
                preparedStatement.setInt(1,j);
                preparedStatement.setString(2,log);
                preparedStatement.execute();
            }
            connection.close();
        }catch (Exception e){
            System.err.println(e.toString());
            throw e;
        }
    }

}
