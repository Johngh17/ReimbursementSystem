package org.crof.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name="reimbursements")
public class ReimbursementRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int ID;

    @Column(name="amount")
    private double amount;

    @ManyToOne
    @JoinColumn(name = "requester")
    private Employee requester;

    @ManyToOne
    @JoinColumn(name= "reviewer")
    private Employee reviewer;

    @Column(name="establishment")
    private String establishment;

    @Column(name="establishment_location")
    private String location;

    @Column(name="status")
    private String status;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Employee getRequester() {
        return requester;
    }

    public void setRequester(Employee requester) {
        this.requester = requester;
    }

    public Employee getReviewer() {
        return reviewer;
    }

    public void setReviewer(Employee reviewer) {
        this.reviewer = reviewer;
    }

    public String getEstablishment() {
        return establishment;
    }

    public void setEstablishment(String establishment) {
        this.establishment = establishment;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ReimbursementRequest{" +
                "ID=" + ID +
                ", amount=" + amount +
                ", requester=" + requester +
                ", reviewer=" + reviewer +
                ", establishment='" + establishment + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
