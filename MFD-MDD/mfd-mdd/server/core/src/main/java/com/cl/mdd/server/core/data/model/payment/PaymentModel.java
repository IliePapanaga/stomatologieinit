package com.cl.mdd.server.core.data.model.payment;

import com.cl.mdd.server.core.data.model.MDDModel;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentModel extends MDDModel {

    private String id;

    private LocalDate date;

    private String jobDayId;

    private String jobInterviewId;

    private String jobApplicationId;

    private String practiceId;

    private String practiceFirstName;

    private String practiceLastName;

    private String office;

    private String location;

    private String label;

    private String proFirstName;

    private String proLastName;

    private String status;

    private BigDecimal amount;

    private String method;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getJobDayId() {
        return jobDayId;
    }

    public void setJobDayId(String jobDayId) {
        this.jobDayId = jobDayId;
    }

    public String getJobInterviewId() {
        return jobInterviewId;
    }

    public void setJobInterviewId(String jobInterviewId) {
        this.jobInterviewId = jobInterviewId;
    }

    public String getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(String jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }

    public String getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
    }

    public String getPracticeFirstName() {
        return practiceFirstName;
    }

    public void setPracticeFirstName(String practiceFirstName) {
        this.practiceFirstName = practiceFirstName;
    }

    public String getPracticeLastName() {
        return practiceLastName;
    }

    public void setPracticeLastName(String practiceLastName) {
        this.practiceLastName = practiceLastName;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getProFirstName() {
        return proFirstName;
    }

    public void setProFirstName(String proFirstName) {
        this.proFirstName = proFirstName;
    }

    public String getProLastName() {
        return proLastName;
    }

    public void setProLastName(String proLastName) {
        this.proLastName = proLastName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public PaymentModel withId(String id) {
        this.id = id;
        return this;
    }

    public PaymentModel withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public PaymentModel withJobDayId(String jobDayId) {
        this.jobDayId = jobDayId;
        return this;
    }

    public PaymentModel withJobInterviewId(String jobInterviewId) {
        this.jobInterviewId = jobInterviewId;
        return this;
    }

    public PaymentModel withJobApplicationId(String jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
        return this;
    }

    public PaymentModel withPracticeId(String practiceId) {
        this.practiceId = practiceId;
        return this;
    }

    public PaymentModel withPracticeFirstName(String firstName) {
        this.practiceFirstName = firstName;
        return this;
    }

    public PaymentModel withPracticeLastName(String lastName) {
        this.practiceLastName = lastName;
        return this;
    }

    public PaymentModel withOffice(String office) {
        this.office = office;
        return this;
    }

    public PaymentModel withLocation(String location) {
        this.location = location;
        return this;
    }

    public PaymentModel withLabel(String label) {
        this.label = label;
        return this;
    }

    public PaymentModel withProFirstName(String proFirstName) {
        this.proFirstName = proFirstName;
        return this;
    }

    public PaymentModel withProLastName(String proLastName) {
        this.proLastName = proLastName;
        return this;
    }

    public PaymentModel withStatus(String status) {
        this.status = status;
        return this;
    }

    public PaymentModel withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public PaymentModel withMethod(String method) {
        this.method = method;
        return this;
    }
}
