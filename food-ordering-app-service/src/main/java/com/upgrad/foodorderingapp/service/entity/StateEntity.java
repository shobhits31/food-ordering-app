package com.upgrad.foodorderingapp.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Entity
@Table(name = "state")
@NamedQueries(
        {
                @NamedQuery(name = "stateByUUID",
                        query = "select s from StateEntity s where s.uuid = :stateUuid"),

                @NamedQuery(name = "getAllStates",
                        query = "select s from StateEntity s")
        }
)
public class StateEntity implements Serializable {

    public StateEntity() {
    }

    public StateEntity(String uuid, String stateName) {
        this.uuid = uuid;
        this.stateName = stateName;
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @NotNull
    private String uuid;

    @Column(name = "STATE_NAME")
    private String stateName;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        StateEntity that = (StateEntity) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(uuid, that.uuid)
                .append(stateName, that.stateName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(uuid)
                .append(stateName)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("uuid", uuid)
                .append("stateName", stateName)
                .toString();
    }
}
