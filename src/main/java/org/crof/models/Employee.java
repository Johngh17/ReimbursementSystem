package org.crof.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;

    @NaturalId
    @Column(name="username",unique = true,nullable = false)
    @JsonIgnore
    private String username;

    @Column(name="passw")
    @JsonIgnore
    private String password;

    @Column(name="fname")
    private String firstname;

    @Column(name="lname")
    private String lastname;

    @Column(name="administrator")
    @JsonIgnore
    private boolean admin;

    @OneToMany(mappedBy = "requester")
    @JsonIgnore
    Set<ReimbursementRequest> reimbursementRequestSet;

    @OneToMany(mappedBy = "reviewer")
    @JsonIgnore
    Set<ReimbursementRequest> reviewableRequests;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Set<ReimbursementRequest> getReimbursementRequestSet() {
        return reimbursementRequestSet;
    }

    public void setReimbursementRequestSet(Set<ReimbursementRequest> reimbursementRequestSet) {
        this.reimbursementRequestSet = reimbursementRequestSet;
    }

    public Set<ReimbursementRequest> getReviewableRequests() {
        return reviewableRequests;
    }

    public void setReviewableRequests(Set<ReimbursementRequest> reviewableRequests) {
        this.reviewableRequests = reviewableRequests;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "ID=" + ID +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", admin=" + admin +
                '}';
    }
}
