package org.crof;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.crof.models.Employee;
import org.crof.repositories.EmployeeDAO;
import org.crof.repositories.ReimbursementDAO;
import org.crof.services.EmployeeService;
import org.crof.services.ReimbursementService;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import java.util.Map;
import static org.crof.utils.Parser.*;

public class App 
{
    private SessionFactory sessionFactory;
    private EmployeeService employeeService;
    private ReimbursementService reimbursementService;
    private EmployeeDAO employeeDAO;
    private ReimbursementDAO reimbursementDAO;
    private Logger logger = LogManager.getLogger(App.class.getName());

    /**
     * Kicks off the application
     * @param args
     */
    public static void main( String[] args )
    {
        App app = new App();
        app.configure();
        app.run();
    }

    /**
     * Does all of the configuration for Hibernate but also sets up the services and DAOs
     */
    private void configure(){
        Configuration configuration = new Configuration().configure();
        if(null != configuration){
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
            this.sessionFactory = configuration.buildSessionFactory(builder.build());
        }
        employeeDAO = new EmployeeDAO(sessionFactory);
        employeeService = new EmployeeService(employeeDAO);
        reimbursementDAO = new ReimbursementDAO(sessionFactory);
        reimbursementService = new ReimbursementService(reimbursementDAO,employeeService);
    }

    /**
     * After initialization, we start up the server and start listening for requests
     */
    private void run(){
        //Start the server on port 7000
        Javalin javalin = Javalin.create().start(7000);

        //Proper handling of CORS is not part of project requirements
        javalin._conf.enableCorsForAllOrigins();

        //We should be able to serve the html and js if we want to do so
        javalin._conf.addStaticFiles("webapp/html",Location.CLASSPATH);
        javalin._conf.addStaticFiles("webapp/js",Location.CLASSPATH);

        //We need a certain number of paths to accomplish our user stories and be nice about it

        //We need a username verification path to check whether the username, as typed in when the input loses focus,
        //is available. This makes "createEmployee" safe without doing a lot of error messaging.
        javalin.get("/usernameAvailable/",
                        ctx -> ctx.result(employeeService.usernameAvailable(
                                ctx.queryParam("username"))?"available":"unavailable"));

        //We need a login path to check whether a submitted username/password combination is valid.
        //If it is, then redirect them to the correct homepage.
        javalin.post("/login/",
                        ctx -> ctx.redirect(login(ctx)));

        //We need a reimbursement request submission path. The reimbursement service will validate inputs if necessary.
        javalin.post("/reimbursementSubmission/",
                        ctx->ctx.result("" + submitReimbursement(ctx)));

        //We need reimbursement request history paths
        //We need a path to get an individual employee's requests based on filters
        javalin.get("/employeeReimbursements/",ctx->ctx.json(reimbursementService.getEmployeeRequestsByStatus(
                        employeeService.getEmployee(ctx.queryParam("username")),
                        Boolean.parseBoolean(ctx.queryParam("pending")),
                        Boolean.parseBoolean(ctx.queryParam("approved")),
                        Boolean.parseBoolean(ctx.queryParam("denied")))));

        //We need a path to get all of the requests an admin has been responsible for based on filters
        javalin.get("/adminReimbursements/",ctx->ctx.json(reimbursementService.getReviewerRequestsByStatus(
                        employeeService.getEmployee(ctx.queryParam("username")),
                        Boolean.parseBoolean(ctx.queryParam("pending")),
                        Boolean.parseBoolean(ctx.queryParam("approved")),
                        Boolean.parseBoolean(ctx.queryParam("denied")))));

        //We need a path to retrieve all reimbursement requests based on filters
        javalin.get("/allReimbursements/",ctx -> ctx.json(reimbursementService.getAllReimbursementRequestsByStatus(
                        Boolean.parseBoolean(ctx.queryParam("pending")),
                        Boolean.parseBoolean(ctx.queryParam("approved")),
                        Boolean.parseBoolean(ctx.queryParam("denied")))));

        //We need a path for a manager to change the status of a request
        javalin.post("/adminResolveRequest/",ctx->ctx.result(resolveRequest(ctx)));

        //We need a path to allow existing information to be changed
        javalin.post("/updateInfo/",ctx->ctx.result(updateInfo(ctx)));

        //We need a path for creating an employee
        javalin.post("/createEmployee/",ctx->ctx.result("" + createEmployee(ctx)));

        //We need a path for resetting a password
        javalin.post("/updatePassword/",ctx->ctx.redirect(updatePassword(ctx)));

        //We need a path for viewing all employees
        javalin.get("/viewAllEmployees/",ctx->ctx.json(employeeService.getEmployeeSet()));

        javalin.get("/viewEmployee/",ctx->ctx.json(employeeService.getEmployee(ctx.queryParam("username"))));
    }

    /**
     * This unwraps the context and passes it to the corresponding method in the employee service
     * @param context contains the username to update and the associated password
     * @return
     */
    private String updatePassword(Context context){
        Map<String,String> body = parseBody(context.body());
        logger.info("Updating password for user " + body.get("username"));
        employeeService.updatePassword(body.get("username"), body.get("password"));
        return route(employeeService.getEmployee(body.get("username")), "http://localhost:7000/EmployeeHomepage.html");
    }

    /**
     * Unwraps the context and passes it to the corresponding method in the employee service
     * @param context contains the authorizing user, the username to update, and the information to change
     * @return "Updated" if successful, otherwise a message about insufficient permissions
     */
    private String updateInfo(Context context){
        Map<String,String> body = parseBody(context.body());
        logger.info("Updating info for user: " + body.get("username"));
        return employeeService.updateInfo(body.get("authority"),body.get("username"),body.get("firstname"),body.get("lastname"));
    }

    /**
     * Unwraps the context and passes it to the corresponding method in the request service
     * @param context contains the id of the request to update and the status to which it should be updated
     * @return the status specified
     */
    private String resolveRequest(Context context){
        Map<String,String> body = parseBody(context.body());
        return reimbursementService.resolveRequest(Integer.parseInt(body.get("id")),body.get("status"),employeeService.getEmployee(body.get("authority")));
    }

    /**
     * Take in a context, parse it, and pass that data to the employee service to create an employee
     * @param context contains the first name, last name, username, and password of a new employee
     * @return -1 if the username was already taken, other the id of the employee
     */
    private int createEmployee(Context context){
        Map<String,String> body = parseBody(context.body());
        String firstname = body.get("firstname");
        String lastname = body.get("lastname");
        String username = body.get("username");
        String password = body.get("password");
        return employeeService.createEmployee(firstname,lastname,username,password);
    }

    /**
     * Takes a context object containing a username and password, parses it, checks whether the login is valid, then redirects
     * @param context contains the username and password being used to log in
     * @return a new page based on whether the username/password was accepted and the role of the employee
     */
    private String login(Context context){
        Map<String,String> body = parseBody(context.body());
        String username = body.get("username");
        String password = body.get("password");
        String destination = "../index.html";
        if(employeeService.validateLogin(username,password)){
            Employee employee = employeeService.getEmployee(username);
            destination = route(employee,"http://localhost:7000/EmployeeHomepage.html");
        }

        return destination;
    }

    /**
     * Routes the user to different pages based on whether they are a manager or not
     * @param employee the employee who is logging into the system
     * @param basePage the url of the page that regular employees see
     * @return modified URL based on the role of the employee
     */
    private String route(Employee employee, String basePage){
        String[] pieces = basePage.split("\\.");
        return employee.isAdmin()?pieces[0] + "Manager." + pieces[1]:basePage;
    }

    /**
     * Validates a reimbursement request according to business logic
     * @param context
     * @return
     */
    private Integer submitReimbursement(Context context){
        Map<String, String> body = parseBody(context.body());
        return reimbursementService.submitReimbursement(
                employeeService.getEmployee(body.get("username")),
                Double.parseDouble(body.get("amount")),
                body.get("establishment"),
                body.get("location"));
    }
}
