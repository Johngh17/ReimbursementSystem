package org.crof;

import org.crof.models.Employee;
import org.crof.repositories.EmployeeDAO;
import org.crof.services.EmployeeService;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class EmployeeServiceTest {
    static EmployeeDAO employeeDAO;
    static SessionFactory sessionFactory;
    static EmployeeService employeeService;

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
    }

    @Test
    public void validateGetAdmin(){
        Employee employee = employeeService.getNextAdmin();
        assertTrue(employee.isAdmin());
    }

    @Test
    public void validateLoginValidator(){
        assertTrue(employeeService.validateLogin("Voldie","Avada"));
    }

    @Test
    public void validateUsernameAvailable(){
        assertTrue(employeeService.usernameAvailable("asdfghjkl"));
    }

    @Test
    public void validateUpdatePassword(){
        String newPassword = "Kedavra";
        String oldPassword = employeeService.getEmployee("Voldie").getPassword();
        employeeService.updatePassword("Voldie",newPassword);
        assertTrue(employeeService.getEmployee("Voldie").getPassword().equals(newPassword));
        employeeService.updatePassword("Voldie",oldPassword);
        assertTrue(employeeService.getEmployee("Voldie").getPassword().equals(oldPassword));
    }

    @Test
    public void validateGetEmployee(){
        String employeeName = "Voldie";
        Employee employee = employeeService.getEmployee(employeeName);
        assertTrue(employeeName.equals(employee.getUsername()));

        employeeName = "asdfghjkl";
        assertNull(employeeService.getEmployee(employeeName));

    }

    @Test
    public void validateUpdateInfo(){
        String username = "Voldie";
        Employee downloaded = employeeService.getEmployee(username);
        String authority = "John";
        String oldFName = downloaded.getFirstname();
        String oldLName = downloaded.getLastname();
        String newFName = "Thomas";
        String newLName = "Riddel";

        employeeService.updateInfo(username,username,newFName,newLName);
        downloaded = employeeDAO.load(username);
        assertEquals(newFName,downloaded.getFirstname());
        assertEquals(newLName,downloaded.getLastname());

        employeeService.updateInfo(authority,username,oldFName,oldLName);
        downloaded = employeeDAO.load(username);
        assertEquals(oldFName,downloaded.getFirstname());
        assertEquals(oldLName,downloaded.getLastname());
    }

    @Test
    public void validateCreateDeleteEmployee(){
        String firstname = "Orson";
        String lastname = "Card";
        String username = "SFAuthor";
        String password = "Ender";
        Employee downloaded = employeeService.getEmployee(username);
        if(null != downloaded){
            employeeService.deleteEmployee(downloaded);
        }

        employeeService.createEmployee(firstname,lastname,username,password);

        //assertEquals(employeeService.createEmployee(firstname,lastname,username,password),-1);

        assertFalse(employeeService.usernameAvailable(username));

        downloaded = employeeService.getEmployee(username);
        assertEquals(downloaded.getFirstname(), firstname);
        assertEquals(downloaded.getLastname(), lastname);
        assertEquals(downloaded.getUsername(), username);
        assertEquals(downloaded.getPassword(), password);

        employeeService.deleteEmployee(downloaded);
        assertTrue(employeeService.usernameAvailable(username));
    }

    @Test
    public void validateGetEmployeeSet(){
        Set<Employee> employees = employeeService.getEmployeeSet();
        assertTrue(employees.contains(employeeService.getEmployee("Voldie")));
        assertTrue(employees.contains(employeeService.getEmployee("John")));
    }

    @Test
    public void validateGetAllEmployees(){
        Map<Integer,Employee> employeeMap = employeeService.getAllEmployees();
        assertEquals(employeeMap.get(47).getUsername(),"Voldie");
        assertEquals(employeeMap.get(1).getUsername(),"John");
    }
}
