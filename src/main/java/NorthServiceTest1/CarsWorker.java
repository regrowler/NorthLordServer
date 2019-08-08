package NorthServiceTest1;

import DataBase.MariaDataBaseConnector;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("")
public class CarsWorker {
    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public static String addCar(@FormParam("login") String l,
                                @FormParam("password") String p,
                                @FormParam("label") String la,
                                @FormParam("model") String mo,
                                @FormParam("cost") String co,
                                @FormParam("rentcost") String re) {

        try {
            String login = URLDecoder.decode(l, "UTF-8");
            String pass = URLDecoder.decode(p, "UTF-8");
            String label = URLDecoder.decode(la, "UTF-8");
            String model = URLDecoder.decode(mo, "UTF-8");
            String costs = URLDecoder.decode(co, "UTF-8");
            String rents = URLDecoder.decode(re, "UTF-8");
            int cost = Integer.parseInt(costs);
            int rent = Integer.parseInt(rents);
            if (LoginWorker.isInDb(login, pass) > 0) {
                if (add(login, la, mo, cost, rent) == 0) {
                    JSONObject object=new JSONObject();
                    object.put("result","success");
                    return object.toString();
                } else {
                    JSONObject object=new JSONObject();
                    object.put("result","fail");
                    return object.toString();
                }
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return "fail";
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public static String updateCar(@FormParam("login") String l,
                                   @FormParam("password") String p,
                                   @FormParam("label") String la,
                                   @FormParam("model") String mo,
                                   @FormParam("cost") String co,
                                   @FormParam("rentcost") String re,
                                   @FormParam("id") String i) {
        try {
            String login = URLDecoder.decode(l, "UTF-8");
            String pass = URLDecoder.decode(p, "UTF-8");
            String label = URLDecoder.decode(la, "UTF-8");
            String model = URLDecoder.decode(mo, "UTF-8");
            String costs = URLDecoder.decode(co, "UTF-8");
            String rents = URLDecoder.decode(re, "UTF-8");
            String ids = URLDecoder.decode(i, "UTF-8");
            int cost = Integer.parseInt(costs);
            int rent = Integer.parseInt(rents);
            int id = Integer.parseInt(ids);
            if (LoginWorker.isInDb(login, pass) > 0) {
                if (update(label, model, cost, rent, id) == 0) {
                    JSONObject object=new JSONObject();
                    object.put("result","success");
                    return object.toString();
                } else {
                    JSONObject object=new JSONObject();
                    object.put("result","fail");
                    return object.toString();
                }
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return "fail";
    }

    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public static String deletecars(@HeaderParam("login") String log,
                                    @HeaderParam("password") String pas,
                                    @HeaderParam("mas") String i
                                    ) {
        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
//            StringBuilder out = new StringBuilder();
//            String line;
//            String log="g";
//            String pas="1";
//            while ((line = reader.readLine()) != null) {
//                out.append(line);
//            }
//            System.out.println(out.toString());   //Prints the string content read from input stream
//            reader.close();
//            String mas=out.toString();

            String login = URLDecoder.decode(log, "UTF-8");
            String pass = URLDecoder.decode(pas, "UTF-8");
            String mass = URLDecoder.decode(i, "UTF-8");
            JSONArray jsonArray = new JSONArray(mass);
            if (LoginWorker.isInDb(login, pass) > 0) {
                if (delete(jsonArray) == 0) {
                    ProfileWorker.updateCarsCounterDec(login, jsonArray.length());
                    JSONObject object=new JSONObject();
                    object.put("result","success");
                    return object.toString();
                }else {
                    JSONObject object=new JSONObject();
                    object.put("result","fail");
                    return object.toString();
                }
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return "fail";
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public static String get(@HeaderParam("login") String log,
                             @HeaderParam("password") String pas,
                             @HeaderParam("id") String i) {
        try {
            String login = URLDecoder.decode(log, "UTF-8");
            String pass = URLDecoder.decode(pas, "UTF-8");

            if (LoginWorker.isInDb(login, pass) >0 ) {
                if (i != null) {
                    String ids = URLDecoder.decode(i, "UTF-8");
                    int id = Integer.parseInt(ids);
                    return getCarById(id);
                } else {
                    String s = getCars(login);
                    return s;
                }
            }else {
                JSONArray array=new JSONArray();
                JSONObject object=new JSONObject();
                object.put("results",array);
                object.put("result","fail");
                return object.toString();
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return "fail";
    }

    public static String getCars(String login) {
        Connection connection = MariaDataBaseConnector.getConnection();
        initCarsTable(connection);
        try {
            PreparedStatement p = connection.prepareStatement("SELECT * FROM cars WHERE owner=?");
            p.setString(1, login);
            ResultSet rs = p.executeQuery();
            JSONArray array;
            JSONObject object;
            JSONObject res=new JSONObject();
            if (rs.next()) {
                array = new JSONArray();
                object = new JSONObject();
                int t=rs.getInt(7);
                if(rs.getInt(7)==1){
                    object.put("id", rs.getInt(1));
                    object.put("label", URLEncoder.encode(rs.getString(3), "UTF-8"));
                    object.put("model", URLEncoder.encode(rs.getString(4), "UTF-8"));
                    object.put("cost", rs.getInt(5));
                    object.put("rent", rs.getInt(6));
                    array.put(object);


                }else {
//                    res.put("result","fail");
                }
                while (rs.next()) {
                    if(rs.getInt(7)==1) {
                        object = new JSONObject();
                        object.put("id", rs.getInt(1));
                        object.put("label", URLEncoder.encode(rs.getString(3), "UTF-8"));
                        object.put("model", URLEncoder.encode(rs.getString(4), "UTF-8"));
                        object.put("cost", rs.getInt(5));
                        object.put("rent", rs.getInt(6));
                        array.put(object);
                    }
                }
                object=new JSONObject();
                res.put("result","success");
                res.put("results",array);
                return res.toString();
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return "fail";

    }

    public static String getCarById(int id) {
        Connection connection = MariaDataBaseConnector.getConnection();
        initCarsTable(connection);
        try {
            PreparedStatement p = connection.prepareStatement("SELECT * from cars where id=?");
            p.setInt(1, id);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("label", URLEncoder.encode(rs.getString(3), "UTF-8"));
                jsonObject.put("model", URLEncoder.encode(rs.getString(4), "UTF-8"));
                jsonObject.put("cost", rs.getInt(5));
                jsonObject.put("rent", rs.getInt(6));
                JSONArray array=new JSONArray();
                array.put(jsonObject);
                JSONObject object=new JSONObject();
                object.put("results",array);
                object.put("result","success");
                return object.toString();
            } else {
                JSONArray array=new JSONArray();
                JSONObject object=new JSONObject();
                object.put("results",array);
                object.put("result","fail");
                return object.toString();
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return "fail";
    }

    public static int delete(JSONArray array) {
        Connection connection = MariaDataBaseConnector.getConnection();
        initCarsTable(connection);
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("update cars set vis=0 where id in(");
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
                p.setInt(j + 1, array.getInt(j));
            }
            p.execute();
            connection.close();
            return 0;
        } catch (Exception e) {
            System.err.println(e.toString());

            return -1;
        }
    }

    public static int add(String login, String label, String model, int cost, int rent) {
        Connection connection = MariaDataBaseConnector.getConnection();
        initCarsTable(connection);
        try {
            PreparedStatement p = connection.prepareStatement("insert into cars (owner,label,model,cost,rentcost,vis) values(?,?,?,?,?,?);");
            p.setString(1, login);
            p.setString(2, label);
            p.setString(3, model);
            p.setInt(4, cost);
            p.setInt(5, rent);
            p.setInt(6,1);
            p.execute();
            ProfileWorker.updateCarsCounterInc(login);
            connection.close();
            return 0;
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return -1;
    }
    public static void initCarsTable(Connection connection){
        try {
            connection.prepareStatement("create table if not exists cars\n" +
                    "(\n" +
                    "\tid int auto_increment,\n" +
                    "\towner varchar(200) not null,\n" +
                    "\tlabel varchar(200) not null,\n" +
                    "\tmodel varchar(200) not null,\n" +
                    "\tcost int not null,\n" +
                    "\trentcost int not null,\n" +
                    "\tvis int null,\n" +
                    "\tconstraint cars_pk\n" +
                    "\t\tprimary key (id)\n" +
                    ");\n" +
                    "\n").execute();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public static int update(String label, String model, int cost, int rent, int id) {
        Connection connection = MariaDataBaseConnector.getConnection();
        initCarsTable(connection);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update cars set label=?,model=?,cost=?,rentcost=? where id=?");
            preparedStatement.setString(1, label);
            preparedStatement.setString(2, model);
            preparedStatement.setInt(3, cost);
            preparedStatement.setInt(4, rent);
            preparedStatement.setInt(5, id);
            preparedStatement.execute();
            connection.close();
            return 0;
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return -1;
    }
}