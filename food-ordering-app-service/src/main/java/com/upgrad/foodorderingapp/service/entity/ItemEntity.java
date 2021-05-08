package com.upgrad.foodorderingapp.service.entity;

import com.upgrad.foodorderingapp.service.common.ItemType;

import javax.persistence.*;
import javax.print.attribute.standard.MediaSize;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "item")
@NamedQueries(
        {
                @NamedQuery(name = "getAllItems",
                        query = "select it from ItemEntity it"),
                @NamedQuery(name = "itemByUUID", query = "select q from ItemEntity q where q.uuid = :uuid")

        }
)
public class ItemEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @Size(max = 200)
    private String uuid;

    @Column(name = "ITEM_NAME")
    @NotNull
    @Size(max = 30)
    private String itemname;

    @Column(name = "PRICE")
    @NotNull
    private Integer price;

    @Column(name = "TYPE")
    @NotNull
    @Size(max = 10)
    private ItemType type;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "category_item", joinColumns = @JoinColumn(name = "ITEM_ID"), inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID"))
    private List<CategoryEntity> categories;

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

    public String getItemName() {
        return itemname;
    }

    public void setItemName(String item_name) {
        this.itemname = item_name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }


}
