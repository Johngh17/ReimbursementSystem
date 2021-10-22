package org.crof.repositories;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.crof.models.ReimbursementRequest;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReimbursementDAO implements DAO<Integer,ReimbursementRequest> {

    SessionFactory sessionFactory;
    Map<Integer, ReimbursementRequest> reimbursements = new HashMap<>();
    private Logger logger = LogManager.getLogger(ReimbursementDAO.class.getName());

    public ReimbursementDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        loadAll();
    }

    @Override
    public Integer save(ReimbursementRequest record) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        if(reimbursements.containsKey(record.getID()))
        {
            session.update(record);
        }
        else {
            session.save(record);
            updateIndices(record);
        }
        tx.commit();
        return record.getID();
    }

    @Override
    public ReimbursementRequest load(Integer id) {
        if(!reimbursements.containsKey(id)) {
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            ReimbursementRequest reimbursementRequest = (ReimbursementRequest) session.load(ReimbursementRequest.class, id);
            tx.commit();
            updateIndices(reimbursementRequest);

            return reimbursementRequest;
        }
        else{
            return reimbursements.get(id);
        }
    }

    @Override
    public Map<Integer, ReimbursementRequest> loadAll() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        List<ReimbursementRequest> reimbursementsList = session.createCriteria(ReimbursementRequest.class).list();
        tx.commit();

        for(ReimbursementRequest reimbursementRequest: reimbursementsList){
            updateIndices(reimbursementRequest);
        }

        return reimbursements;
    }

    private void updateIndices(ReimbursementRequest record){
        reimbursements.put(record.getID(), record);
    }

    public void delete(ReimbursementRequest record){
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.delete(record);
        tx.commit();
        reimbursements.remove(record.getID());
    }
}