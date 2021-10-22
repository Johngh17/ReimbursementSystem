package org.crof;

import org.crof.models.Employee;
import org.crof.models.ReimbursementRequest;
import org.crof.repositories.EmployeeDAO;
import org.crof.repositories.ReimbursementDAO;
import org.crof.services.EmployeeService;
import org.crof.services.ReimbursementService;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class ReimbursementServiceTest {
    static ReimbursementService reimbursementService;
    static ReimbursementDAO reimbursementDAO;
    static EmployeeService employeeService;
    static EmployeeDAO employeeDAO;
    static SessionFactory sessionFactory;

    @BeforeClass
    public static void configure(){
        Configuration configuration = new Configuration().configure();
        if(null != configuration){
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
            sessionFactory = configuration.buildSessionFactory(builder.build());
        }
        employeeDAO = new EmployeeDAO(sessionFactory);
        employeeService = new EmployeeService(employeeDAO);
        employeeDAO.loadAll();
        reimbursementDAO = new ReimbursementDAO(sessionFactory);
        reimbursementService = new ReimbursementService(reimbursementDAO,employeeService);
    }

    @Test
    public void validateSubmitRequest(){

    }

    @Test
    public void validateGetAllReimbursementRequestsByStatus(){

    }

    @Test
    public void validateFilterRequestsByStatus(){

    }

    @Test
    public void validateGetEmployeeRequestsByStatus(){

    }

    @Test
    public void validateGetReviewerRequestsByStatus(){

    }

    @Test
    public void validateResolveRequest(){
        //Get a known request, tied to a user who will not be deleted
        int id = 40;
        ReimbursementRequest reimbursementRequest = reimbursementService.getReimbursementRequest(id);

        //Get the old information so that it can be reset
        String oldStatus = reimbursementRequest.getStatus();
        Employee oldReviewer = reimbursementRequest.getReviewer();

        //Save the new information
        String newStatus = "Denied";
        Employee admin = employeeService.getNextAdmin();

        //Actually make the change
        reimbursementService.resolveRequest(id,newStatus,admin);

        //Make sure that the change is reflected
        ReimbursementRequest loaded = reimbursementService.getReimbursementRequest(id);
        assertEquals(loaded.getStatus(),newStatus);
        assertEquals(loaded.getReviewer(),admin);

        //Undo the changes
        reimbursementService.resolveRequest(id,oldStatus,oldReviewer);

        //Make sure that the changes were rolled back
        loaded = reimbursementService.getReimbursementRequest(id);
        assertEquals(loaded.getStatus(),oldStatus);
        assertEquals(loaded.getReviewer(),oldReviewer);
    }

    @Test
    public void validateSaveAndDelete(){

    }

    @Test
    public void validateGetReimbursementRequest(){

    }
}
