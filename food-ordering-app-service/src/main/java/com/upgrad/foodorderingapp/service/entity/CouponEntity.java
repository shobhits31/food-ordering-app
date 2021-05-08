package com.upgrad.foodorderingapp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "coupon")
@NamedQueries({
        @NamedQuery(name = "couponByCouponName", query = "select c from CouponEntity c where c.couponName = :couponName"),
        @NamedQuery(name = "getCouponByUUID", query = "select c from CouponEntity c where c.uuid = :couponUUID")
})
public class CouponEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "uuid", unique = true)
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "coupon_name")
    @Size(max = 255)
    private String couponName;

    @Column(name = "percent")
    @NotNull
    private Integer percent;

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

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }
}
