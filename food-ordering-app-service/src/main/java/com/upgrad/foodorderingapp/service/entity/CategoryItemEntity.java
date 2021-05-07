package com.upgrad.foodorderingapp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "category_item")
@NamedQueries(
        {
                @NamedQuery(name = "getAllCategoryItems", query = "select ct from CategoryItemEntity ct"),
                @NamedQuery(name = "getAllCategoryItemsByUuid", query = "select ct from CategoryItemEntity ct where ct.categoryEntity.uuid=:uuid")
        }
)
public class CategoryItemEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ITEM_ID")
    @NotNull
    private ItemEntity itemEntity;

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    @NotNull
    private CategoryEntity categoryEntity;

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

    public CategoryEntity getCategoryEntity() {
        return categoryEntity;
    }

    public void setCategoryEntity(CategoryEntity categoryEntity) {
        this.categoryEntity = categoryEntity;
    }

}

