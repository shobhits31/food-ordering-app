package com.upgrad.foodorderingapp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "address")
@NamedQueries(
        {
                @NamedQuery(name = "getAllAddress",
                        query = "select ae from AddressEntity ae")
        }
)
public class AddressEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "UUID")
    @Size(max = 200)
    private String uuid;

    @Column(name = "flat_buil_number")
    @Size(max = 255)
    private String flatBuildingName;

    @Column(name = "locality")
    @Size(max = 255)
    private String locality;

    @Column(name = "city")
    @Size(max = 30)
    private String city;

    @Column(name = "pincode")
    @Size(max = 30)
    private String pincode;

    @JoinColumn(name = "state_id")
    @ManyToOne
    private StateEntity  stateId;

    @Column(name = "active")
    private Integer active;

    public AddressEntity(@Size(max = 200) String uuid, @Size(max = 255) String flatBuildingName, @Size(max = 255) String locality, @Size(max = 30) String city, @Size(max = 30) String pincode, StateEntity stateId) {
        this.uuid = uuid;
        this.flatBuildingName = flatBuildingName;
        this.locality = locality;
        this.city = city;
        this.pincode = pincode;
        this.stateId = stateId;
    }

    public AddressEntity(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFlatBuildingName() {
        return flatBuildingName;
    }

    public void setFlatBuildingName(String flatBuildingName) {
        this.flatBuildingName = flatBuildingName;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public StateEntity getState() {
        return stateId;
    }

    public void setState(StateEntity stateId) {
        this.stateId = stateId;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

}
