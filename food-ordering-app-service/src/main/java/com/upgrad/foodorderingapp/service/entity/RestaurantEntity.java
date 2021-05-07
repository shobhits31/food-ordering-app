package com.upgrad.foodorderingapp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "restaurant")
@NamedQueries(
        {
                @NamedQuery(name = "getAllRestaurants",
                        query = "select ra from RestaurantEntity ra order by ra.customerRating desc"),
                @NamedQuery(name = "getRestaurantByName",
                        query = "select ra from RestaurantEntity ra where LOWER(ra.restaurantName) like LOWER(CONCAT('%',:rName,'%'))"),
                @NamedQuery(name = "getRestaurantById",
                        query = "select ra from RestaurantEntity ra where ra.uuid=:uuid")
        }
)
public class RestaurantEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @Size(max = 200)
    private String uuid;

    @Column(name = "RESTAURANT_NAME")
    @NotNull
    @Size(max = 50)
    private String restaurantName;

    @Column(name = "PHOTO_URL")
    @NotNull
    @Size(max = 255)
    private String photoUrl;

    @Column(name = "CUSTOMER_RATING",precision = 1,scale = 2)
    @NotNull
    private Double customerRating;

    @Column(name = "AVERAGE_PRICE_FOR_TWO")
    @NotNull
    private Integer averagePrice;

    @Column(name = "NUMBER_OF_CUSTOMERS_RATED")
    @NotNull
    private Integer numberOfCustomersRated;

    @JoinColumn(name = "ADDRESS_ID")
    @ManyToOne
    @NotNull
    private AddressEntity address;

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

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Double getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(Double customerRating) {
        this.customerRating = customerRating;
    }

    public Integer getAvgPrice() {
        return averagePrice;
    }

    public void setAvgPrice(Integer averagePrice) {
        this.averagePrice = averagePrice;
    }

    public Integer getNumberCustomersRated() {
        return numberOfCustomersRated;
    }

    public void setNumberCustomersRated(Integer numberOfCustomersRated) {
        this.numberOfCustomersRated = numberOfCustomersRated;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity addressId) {
        this.address = addressId;
    }
}
