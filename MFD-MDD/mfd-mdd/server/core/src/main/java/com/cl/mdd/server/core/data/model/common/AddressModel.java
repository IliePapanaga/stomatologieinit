package com.cl.mdd.server.core.data.model.common;


import com.cl.mdd.server.core.data.model.MDDModel;
import com.cl.mdd.server.core.validation.constraint.composite.City;
import com.cl.mdd.server.core.validation.constraint.composite.State;
import com.cl.mdd.server.core.validation.constraint.composite.Street;
import com.cl.mdd.server.core.validation.constraint.composite.ZipCode;
import com.cl.mdd.server.core.validation.group.Register;
import com.cl.mdd.server.core.validation.group.RequireCoordinates;
import com.cl.mdd.server.core.validation.group.Save;
import com.cl.mdd.server.core.validation.group.Update;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class AddressModel extends MDDModel {

    private String country;

    @NotNull(groups = {Register.class, Save.class, Update.class}, message = "{state.not.null}")
    @State
    private String state;

    @NotNull(groups = {Register.class, Save.class, Update.class}, message = "{city.not.null}")
    @City
    private String city;

    @NotNull(groups = {Register.class, Save.class, Update.class}, message = "{street.not.null}")
    @Street
    private String street;

    @NotNull(groups = {Register.class, Save.class, Update.class}, message = "{zipCode.not.null}")
    @ZipCode
    private String zipCode;

    @Column(name = "latitude")
    @NotNull(groups = RequireCoordinates.class, message = "{latitude.not.null}")
    private Double latitude;

    @Column(name = "longitude")
    @NotNull(groups = RequireCoordinates.class, message = "{longitude.not.null}")
    private Double longitude;

    public String getCountry() {
        return country;
    }

    public AddressModel setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getState() {
        return state;
    }

    public AddressModel setState(String state) {
        this.state = state;
        return this;
    }

    public String getCity() {
        return city;
    }

    public AddressModel setCity(String city) {
        this.city = city;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public AddressModel setStreet(String street) {
        this.street = street;
        return this;
    }

    public String getZipCode() {
        return zipCode;
    }

    public AddressModel setZipCode(String zipCode) {
        this.zipCode = zipCode;
        return this;
    }


    public Double getLatitude() {
        return latitude;
    }

    public AddressModel setLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public Double getLongitude() {
        return longitude;
    }

    public AddressModel setLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressModel that = (AddressModel) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                Objects.equals(country, that.country) &&
                Objects.equals(state, that.state) &&
                Objects.equals(city, that.city) &&
                Objects.equals(street, that.street) &&
                Objects.equals(zipCode, that.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, state, city, street, zipCode, latitude, longitude);
    }
}
