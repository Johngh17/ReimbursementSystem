package org.crof.services;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.crof.models.Employee;
import org.crof.models.ReimbursementRequest;
import org.crof.repositories.ReimbursementDAO;
import java.util.HashSet;
import java.util.Set;

public class ReimbursementService {
    EmployeeService employeeService;
    ReimbursementDAO reimbursementDAO;
    private Logger logger = LogManager.getLogger(ReimbursementService.class.getName());

    public ReimbursementService(ReimbursementDAO reimbursementDAO, EmployeeService employeeService){
        this.employeeService = employeeService;
        this.reimbursementDAO = reimbursementDAO;
    }

    /**
     * Create a reimbursement request, assign it to an admin, and then save it
     * @param requester the employee requesting reimbursement
     * @param amount the amount to be reimbursed
     * @param establishment the place the money was spent
     * @param location the city the establishment was in
     * @return the ID of the reimbursement
     */
    public Integer submitReimbursement(Employee requester, double amount, String establishment, String location){
        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
        reimbursementRequest.setAmount(amount);
        reimbursementRequest.setEstablishment(establishment);
        reimbursementRequest.setLocation(location);
        reimbursementRequest.setStatus("Pending");

        reimbursementRequest.setRequester(requester);

        Employee reviewer = employeeService.getNextAdmin();
        reimbursementRequest.setReviewer(reviewer);

        requester.getReimbursementRequestSet().add(reimbursementRequest);
        reviewer.getReviewableRequests().add(reimbursementRequest);

        return save(reimbursementRequest);
    }

    /**
     * Get a set of all of the reimbursement requests that match the requested statuses
     * @param pending whether pending reimbursements should be returned
     * @param approved whether approved reimbursements should be returned
     * @param denied whether denied reimbursements should be returned
     * @return all reimbursements that match the criteria
     */
    public Set<ReimbursementRequest> getAllReimbursementRequestsByStatus(boolean pending, boolean approved, boolean denied){
        Set<ReimbursementRequest> subset = new HashSet<>();

        //Go through all employees and get their reimbursement requests
        for(Employee employee: employeeService.getAllEmployees().values()) {
            for(ReimbursementRequest reimbursementRequest: employee.getReimbursementRequestSet()) {
                subset.add(reimbursementDAO.load(reimbursementRequest.getID()));
            }
        }

        //Filter the resulting set
        return filterRequestsByStatus(subset,pending,approved,denied);
    }

    /**
     * Filters through a singular employee's requests based on the statuses that should be shown
     * @param employee the employee whose requests are being listed
     * @param pending whether pending requests should be included
     * @param approved whether approved requests should be included
     * @param denied whether denied requests should be included
     * @return a set of requests that match the requested statuses
     */
    public Set<ReimbursementRequest> getEmployeeRequestsByStatus(Employee employee, boolean pending, boolean approved, boolean denied){
        return filterRequestsByStatus(employee.getReimbursementRequestSet(),pending,approved,denied);
    }

    /**
     * Filters an arbitrary set of requests based on whether they meet status requirements
     * @param reimbursementRequestSet the set to be filtered
     * @param pending whether this status is acceptable
     * @param approved whether this status is acceptable
     * @param denied whether this status is acceptable
     * @return a filtered set of reimbursement requests
     */
    private Set<ReimbursementRequest> filterRequestsByStatus(Set<ReimbursementRequest> reimbursementRequestSet, boolean pending, boolean approved, boolean denied){
        Set<ReimbursementRequest> subset = new HashSet<>();

        //Iterates through the entire set
        for(ReimbursementRequest reimbursementRequest: reimbursementRequestSet){

            //Adds them to the results set iff they match one of the criteria
            if((reimbursementRequest.getStatus().equals("Pending") && pending)
                    || (reimbursementRequest.getStatus().equals("Approved") && approved)
                    || (reimbursementRequest.getStatus().equals("Denied") && denied)) {
                subset.add(reimbursementRequest);
            }
        }
        return subset;
    }

    /**
     * Filters through all of the requests an administrative employee has been responsible for
     * @param employee the administrative employee
     * @param pending whether this status is acceptable
     * @param approved whether this status is acceptable
     * @param denied whether this status is acceptable
     * @return
     */
    public Set<ReimbursementRequest> getReviewerRequestsByStatus(Employee employee, boolean pending, boolean approved, boolean denied){
        return filterRequestsByStatus(employee.getReviewableRequests(),pending,approved,denied);
    }

    public String resolveRequest(int id, String newStatus, Employee finalReviewer){
        ReimbursementRequest reimbursementRequest = reimbursementDAO.load(id);
        reimbursementRequest.setStatus(newStatus);
        reimbursementRequest.setReviewer(finalReviewer);
        save(reimbursementRequest);
        return newStatus;
    }

    public Integer save(ReimbursementRequest reimbursementRequest){
        return reimbursementDAO.save(reimbursementRequest);
    }

    public ReimbursementRequest getReimbursementRequest(int id){
        return  reimbursementDAO.load(id);
    }
}
