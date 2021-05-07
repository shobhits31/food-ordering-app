package com.upgrad.foodorderingapp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "state")
@NamedQueries(
        {
                @NamedQuery(name = "getAllStates",
                        query = "select se from StateEntity se")
        }
)
public class StateEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @Size(max = 200)
    private String uuid;


    @Column(name = "STATE_NAME")
    @NotNull
    @Size(max = 50)
    private String stateName;

    public StateEntity(@Size(max = 200) String uuid, @NotNull @Size(max = 50) String stateName) {
        this.uuid = uuid;
        this.stateName = stateName;
    }

    public StateEntity(){}

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

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

}
