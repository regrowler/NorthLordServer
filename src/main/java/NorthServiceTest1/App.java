package NorthServiceTest1;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        System.err.println("asd1");
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        Server jettyServer = new Server(8080);
        jettyServer.setHandler(context);
        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/e");
        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.classnames",
                EntryPoint.class.getCanonicalName());
        ServletHolder log=context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/log");
        log.setInitParameter("jersey.config.server.provider.classnames",
                LoginWorker.class.getCanonicalName());
        ServletHolder pic=context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/pic");
        pic.setInitParameter("jersey.config.server.provider.classnames",
                PictureWorker.class.getCanonicalName());
        ServletHolder profile=context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/profile");
        profile.setInitParameter("jersey.config.server.provider.classnames",
                ProfileWorker.class.getCanonicalName());
        ServletHolder cars=context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/car");
        cars.setInitParameter("jersey.config.server.provider.classnames",
                CarsWorker.class.getCanonicalName());
        ServletHolder rentss=context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/rent");
        rentss.setInitParameter("jersey.config.server.provider.classnames",
                RentWorker.class.getCanonicalName());

        try {
            jettyServer.start();
            jettyServer.join();
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            jettyServer.destroy();
        }
    }
}
