package com.upgrad.foodorderingapp.service.entity;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name="orders")
@NamedQueries({
        @NamedQuery(name = "getPastOrders", query = "select o from OrderEntity o where o.customer.uuid = :customerUUID order by o.date desc")
})
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "uuid", unique = true)
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "bill")
    @NotNull
    private Double bill;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private CouponEntity couponId;

    @Column(name = "discount")
    @ColumnDefault("0")
    @NotNull
    private Double discount=0.0;

    @Column(name = "date")
    @NotNull
    private Date date;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private PaymentEntity paymentId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CustomerEntity customer;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private RestaurantEntity restaurant;

    public OrderEntity() {}

    public OrderEntity(@NotNull @Size(max = 200) String uuid, @NotNull Double bill, CouponEntity couponId, @NotNull Double discount, @NotNull Date date, PaymentEntity paymentId, CustomerEntity customer, AddressEntity address, RestaurantEntity restaurant) {
        this.uuid = uuid;
        this.bill = bill;
        this.couponId = couponId;
        this.discount = discount;
        this.date = date;
        this.paymentId = paymentId;
        this.customer = customer;
        this.address = address;
        this.restaurant = restaurant;
    }

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

    public Double getBill() {
        return bill;
    }

    public void setBill(Double bill) {
        this.bill = bill;
    }

    public CouponEntity getCouponId() {
        return couponId;
    }

    public void setCouponId(CouponEntity couponId) {
        this.couponId = couponId;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public PaymentEntity getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(PaymentEntity paymentId) {
        this.paymentId = paymentId;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public RestaurantEntity getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantEntity restaurant) {
        this.restaurant = restaurant;
    }
}
