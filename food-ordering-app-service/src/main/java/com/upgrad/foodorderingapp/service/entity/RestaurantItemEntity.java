package com.upgrad.foodorderingapp.service.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "restaurant_item")
@NamedQueries(
        {
                @NamedQuery(name = "getAllRestaurantItems",
                        query = "select it from RestaurantItemEntity it")
        }
)
public class RestaurantItemEntity implements Serializable {


    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private ItemEntity itemEntity;


    @ManyToOne
    private RestaurantEntity restaurantEntity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ItemEntity getItemEntity() {
        return itemEntity;
    }

    public void setItemEntity(ItemEntity itemEntity) {
        this.itemEntity = itemEntity;
    }

    public RestaurantEntity getRestaurantEntity() {
        return restaurantEntity;
    }

    public void setRestaurantEntity(RestaurantEntity restaurantEntity) {
        this.restaurantEntity = restaurantEntity;
    }
}
