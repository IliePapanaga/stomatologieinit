package com.cl.mdd.server.core.data.model.query.model;

import java.time.ZonedDateTime;


/**
 * System user office model.
 * <p/>
 * This is a read only model.
 */
public class SystemUserPracticeModel {

    private String id;
    private String firstName;
    private String lastName;
    private String status;
    private String officeName;
    private String country;
    private String state;
    private String city;
    private String street;
    private String zipCode;
    /**
     * practice locations count
     */
    private Long locations;
    private String officePhone;
    private String officeManagerName;
    private ZonedDateTime lastActivity;
    private Double officeRating;
    private Long totalFeedback;

    public SystemUserPracticeModel() {
    }

    public SystemUserPracticeModel(String id, String firstName, String lastName,
                                   String status, String officeName,
                                   String country, String state, String city, String street, String zipCode,
                                   Long locations, String officePhone,
                                   String officeManagerName, ZonedDateTime lastActivity, Double officeRating, Long totalFeedback) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.officeName = officeName;
        this.country = country;
        this.state = state;
        this.city = city;
        this.street = street;
        this.zipCode = zipCode;
        this.locations = locations;
        this.officePhone = officePhone;
        this.officeManagerName = officeManagerName;
        this.lastActivity = lastActivity;
        this.officeRating = officeRating;
        this.totalFeedback = totalFeedback;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getStatus() {
        return status;
    }

    public String getOfficeName() {
        return officeName;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public Long getLocations() {
        return locations;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public String getOfficeManagerName() {
        return officeManagerName;
    }

    public ZonedDateTime getLastActivity() {
        return lastActivity;
    }

    public Double getOfficeRating() {
        return officeRating;
    }

    public Long getTotalFeedback() {
        return totalFeedback;
    }

    public SystemUserPracticeModel setStatus(String status) {
        this.status = status;
        return this;
    }

    public SystemUserPracticeModel setLocations(Long locations) {
        this.locations = locations;
        return this;
    }
}
