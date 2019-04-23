package NorthServiceTest1;

import DataBase.MariaDataBaseConnector;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

@Path("")
public class RentWorker {
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public static String addRENT(@FormParam("login") String log,
                                 @FormParam("password") String pas,
                                 @FormParam("startdate") String sd,
                                 @FormParam("starttime") String st,
                                 @FormParam("enddate") String ed,
                                 @FormParam("endtime") String et,
                                 @FormParam("cost") String c,
                                 @FormParam("id") String i,
                                 @FormParam("name") String n) {
        try {
            String login = URLDecoder.decode(log, "UTF-8");
            String pass = URLDecoder.decode(pas, "UTF-8");
            String startd = URLDecoder.decode(sd, "UTF-8");
            String startt = URLDecoder.decode(st, "UTF-8");
            String endd = URLDecoder.decode(ed, "UTF-8");
            String endt = URLDecoder.decode(et, "UTF-8");
            String costs = URLDecoder.decode(c, "UTF-8");
            String name = URLDecoder.decode(n, "UTF-8");
            String ids = URLDecoder.decode(i, "UTF-8");
            int cost = Integer.parseInt(costs);
            int id = Integer.parseInt(ids);
            if (LoginWorker.isInDb(login, pass) > 0) {
                if (addRent(login, startd, startt, endd, endt, cost, id, name) == 0) {
                    return "succ";
                } else return "fail";
            }

        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return "faul";
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public static String get(@HeaderParam("login") String log,
                             @HeaderParam("password") String pas,
                             @HeaderParam("id") String i) {
        try {
            String login = URLDecoder.decode(log, "UTF-8");
            String pass = URLDecoder.decode(pas, "UTF-8");
            if (LoginWorker.isInDb(login, pass) > 0) {
                if (i != null) {
                    String ids = URLDecoder.decode(i, "UTF-8");
                    int id = Integer.parseInt(ids);
                    return getRentsById(id, login);
                } else {
                    String s = getRents(login);
                    return s;
                }
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return "fail";
    }
    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public static String updateREnt(@FormParam("login") String log,
                                    @FormParam("password") String pas,
                                    @FormParam("startdate") String sd,
                                    @FormParam("starttime") String st,
                                    @FormParam("enddate") String ed,
                                    @FormParam("endtime") String et,
                                    @FormParam("cost") String c,
                                    @FormParam("id") String i,
                                    @FormParam("name") String n){
        try {
            String login = URLDecoder.decode(log, "UTF-8");
            String pass = URLDecoder.decode(pas, "UTF-8");
            String startd = URLDecoder.decode(sd, "UTF-8");
            String startt = URLDecoder.decode(st, "UTF-8");
            String endd = URLDecoder.decode(ed, "UTF-8");
            String endt = URLDecoder.decode(et, "UTF-8");
            String costs = URLDecoder.decode(c, "UTF-8");
            String name = URLDecoder.decode(n, "UTF-8");
            String ids = URLDecoder.decode(i, "UTF-8");
            int cost = Integer.parseInt(costs);
            int id = Integer.parseInt(ids);
            if (LoginWorker.isInDb(login, pass) > 0) {
                if (update(login, startd, startt, endd, endt, cost, id, name) == 0) {
                    return "succ";
                } else return "fail";
            }
        }catch (Exception e){System.err.println(e.toString());}
        return "fail";
    }
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public static String deleterents(@HeaderParam("login") String log,
                                    @HeaderParam("password") String pas,
                                    @HeaderParam("mass") String mas) {
        try {
            String login = URLDecoder.decode(log, "UTF-8");
            String pass = URLDecoder.decode(pas, "UTF-8");
            String mass = URLDecoder.decode(mas, "UTF-8");
            JSONArray jsonArray = new JSONArray(mass);
            if (LoginWorker.isInDb(login, pass) > 0) {
                if (delete(jsonArray,login) == 0) {
                    //ProfileWorker.updateCarsCounterDec(login, -jsonArray.length());
                    return "succ";
                } else return "fail";
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return "fail";
    }

    private static int delete(JSONArray array,String log) {
        Connection connection = MariaDataBaseConnector.getConnection();
        try {
            int cash=0;
            StringBuilder builder = new StringBuilder();
            builder.append("delete from rents where id in(");
            for (int j = 0; j < array.length(); j++) {
                builder.append("?");
                if (j != array.length() - 1) {
                    builder.append(",");
                }
            }
            builder.delete(builder.length() - 1, builder.length() - 1);
            builder.append(");");
            PreparedStatement p = connection.prepareStatement(builder.toString());
            for (int j = 0; j < array.length(); j++) {
                JSONObject o=new JSONObject(array.getString(j));
                p.setInt(j + 1, o.getInt("id"));
                cash+=o.getInt("cost");
            }
            p.execute();
            p=connection.prepareStatement("select profit from userinfo where login=?");
            p.setString(1,log);
            ResultSet rs=p.executeQuery();
            rs.next();
            int i=rs.getInt(1);
            i-=cash;
            p=connection.prepareStatement("update userinfo set profit=?  where login=?");
            p.setInt(1,i);p.setString(2,log);
            p.execute();
            connection.close();
            return 0;
        } catch (Exception e) {
            System.err.println(e.toString());

            return -1;
        }
    }

    public static int update(String log,String sd, String st, String ed, String et, int cost, int id, String name){
        Connection connection=MariaDataBaseConnector.getConnection();
        try {
            PreparedStatement p1=connection.prepareStatement("select * from rents where id=?");
            p1.setInt(1,id);
            ResultSet rs=p1.executeQuery();
            if(rs.next()){
                int oldcost=rs.getInt(8);
                oldcost=cost-oldcost;
                ProfileWorker.updateMoneyCounter(log,oldcost);
                PreparedStatement p=connection.prepareStatement("update rents set startdate=?,starttime=?,enddate=?,endtime=?,cost=?,name=? where id=?");
                p.setString(1,sd);
                p.setString(2,st);
                p.setString(3,ed);
                p.setString(4,et);
                p.setInt(5,cost);
                p.setString(6,name);
                p.setInt(7,id);
                p.execute();
                return 0;
            }
        }catch (Exception e){System.err.println(e.toString());}
        return -1;
    }
    public static String getRents(String login) {
        Connection connection = MariaDataBaseConnector.getConnection();
        try {
            PreparedStatement p = connection.prepareStatement("SELECT * FROM work.rents r left outer join cars c on r.carid=c.id where r.owner=?;");
            p.setString(1, login);
            ResultSet rs = p.executeQuery();
            JSONArray array;
            JSONObject object;
            ArrayList<Integer> m=new ArrayList<>();
            if (rs.next()) {
                array = new JSONArray();
                object = new JSONObject();
                object.put("name", URLEncoder.encode(rs.getString(9), "UTF-8"));
                object.put("startdate", rs.getString(4));
                object.put("starttime", rs.getString(5));
                object.put("enddate", rs.getString(6));
                object.put("id", rs.getInt(1));
                object.put("endtime", rs.getString(7));
                object.put("cost", rs.getInt(8));
                object.put("label",rs.getString(12));
                object.put("model",rs.getString(13));
                array.put(object.toString());
                while (rs.next()) {
                    object = new JSONObject();
                    object.put("name", URLEncoder.encode(rs.getString(9), "UTF-8"));
                    object.put("id", rs.getInt(1));
                    object.put("startdate", rs.getString(4));
                    object.put("starttime", rs.getString(5));
                    object.put("enddate", rs.getString(6));
                    object.put("endtime", rs.getString(7));
                    object.put("cost", rs.getInt(8));
                    object.put("label",rs.getString(12));
                    object.put("model",rs.getString(13));
                    array.put(object.toString());
                }

//                StringBuilder s=new StringBuilder();
//
//                s.append("select * from cars where id in(");
//                for(int i =0;i<array.length();i++){\
//                }
                connection.close();
                return array.toString();
            } else {
                connection.close();
                return "fail";
            }

        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return "fail";
    }

    public static String getRentsById(int id, String login) {
        Connection connection = MariaDataBaseConnector.getConnection();
        try {
            PreparedStatement p = connection.prepareStatement("SELECT * FROM rents WHERE owner=? and carid=?");
            p.setString(1, login);
            p.setInt(2, id);
            ResultSet rs = p.executeQuery();
            JSONArray array;
            JSONObject object;
            if (rs.next()) {
                array = new JSONArray();
                object = new JSONObject();
                object.put("name", URLEncoder.encode(rs.getString(9), "UTF-8"));
                object.put("startdate", rs.getString(4));
                object.put("id", rs.getInt(1));
                object.put("starttime", rs.getString(5));
                object.put("enddate", rs.getString(6));
                object.put("endtime", rs.getString(7));
                object.put("cost", rs.getInt(8));
                array.put(object.toString());
                while (rs.next()) {
                    object = new JSONObject();
                    object.put("name", URLEncoder.encode(rs.getString(9), "UTF-8"));
                    object.put("startdate", rs.getString(4));
                    object.put("starttime", rs.getString(5));
                    object.put("enddate", rs.getString(6));
                    object.put("endtime", rs.getString(7));
                    object.put("cost", rs.getInt(8));
                    object.put("id", rs.getInt(1));
                    array.put(object.toString());
                }
                connection.close();
                return array.toString();
            } else {
                connection.close();
                return "fail";
            }

        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return "fail";
    }

    public static int addRent(String log, String sd, String st, String ed, String et, int cost, int id, String name) {
        Connection connection = MariaDataBaseConnector.getConnection();
        try {
            PreparedStatement p = connection.prepareStatement("insert into rents (owner,carid,startdate,starttime,enddate,endtime,cost,name) values (?,?,?,?,?,?,?,?);");
            p.setString(1, log);
            p.setInt(2, id);
            p.setString(3, sd);
            p.setString(4, st);
            p.setString(5, ed);
            p.setString(6, et);
            p.setInt(7, cost);
            p.setString(8, name);
            p.execute();
            connection.close();
            ProfileWorker.updateMoneyCounter(log,cost);
            return 0;
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return -1;
    }
}
