package org.crof.repositories;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.crof.models.Employee;
import org.hibernate.NaturalIdLoadAccess;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.*;

public class EmployeeDAO implements DAO<Integer, Employee> {
    SessionFactory sessionFactory;
    Map<String,Employee> employees = new HashMap<>();
    Map<Integer,Employee> empByID = new HashMap<>();
    List<Employee> admins = new ArrayList<>();
    private Logger logger = LogManager.getLogger(EmployeeDAO.class.getName());

    public EmployeeDAO(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
        loadAll();
    }
    @Override
    public Integer save(Employee record) {
        logger.info("Saving employee: " + record.getFirstname() + " " + record.getLastname());
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        if(employees.containsKey(record.getUsername()))
        {
            session.update(session.merge(record));
        }
        else {
            session.save(record);
            updateIndices(record);
        }
        tx.commit();
        logger.info("Successfully saved");
        return record.getID();
    }

    @Override
    public Employee load(Integer id) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        Employee emp = (Employee) session.get(Employee.class,id);
        tx.commit();
        if(emp != null) {
            updateIndices(emp);
        }
        return emp;
    }

    public Employee load(String username){
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        NaturalIdLoadAccess loadAccess = session.byNaturalId(Employee.class);
        loadAccess.using("username",username);
        Employee loaded = (Employee) loadAccess.load();
        tx.commit();
        if(loaded != null) {
            updateIndices(loaded);
        }
        return loaded;
    }

    @Override
    public Map<Integer, Employee> loadAll() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        List<Employee> employeeList = session.createCriteria(Employee.class).list();
        tx.commit();

        for(Employee emp: employeeList){
            updateIndices(emp);
        }
        return empByID;
    }

    public Map<String, Employee> getEmployeesByUsername(){
        return employees;
    }

    public Map<Integer,Employee> getEmployees(){
        return empByID;
    }

    private void updateIndices(Employee record){
        employees.put(record.getUsername(), record);
        empByID.put(record.getID(), record);
        if(record.isAdmin()){
            admins.add(record);
        }
    }

    public List<Employee> getAdmins() {
        return admins;
    }

    public void deleteEmployee(Employee record){
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.delete(record);
        tx.commit();
        empByID.remove(record.getID());
        employees.remove(record.getUsername());
        if(admins.contains(record)){
            admins.remove(record);
        }
    }
}
