package com.upgrad.foodorderingapp.api.controller;

import com.upgrad.foodorderingapp.api.model.CategoryDetailsResponse;
import com.upgrad.foodorderingapp.api.model.ItemList;
import com.upgrad.foodorderingapp.api.model.ItemQuantityResponseItem;
import com.upgrad.foodorderingapp.service.businness.CategoryService;
import com.upgrad.foodorderingapp.service.businness.ItemService;
import com.upgrad.foodorderingapp.service.entity.CategoryItemEntity;
import com.upgrad.foodorderingapp.service.entity.RestaurantItemEntity;
import com.upgrad.foodorderingapp.service.exception.CategoryNotFoundException;
import com.upgrad.foodorderingapp.service.exception.RestaurantNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/item/restaurant")
public class ItemController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ItemService itemService;

    @RequestMapping(method = RequestMethod.GET,path = "/",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<ItemQuantityResponseItem>> getAllRestaurants(final String restaurant_id) throws RestaurantNotFoundException {
        List<RestaurantItemEntity> restaurantItemEntities = itemService.getAllRestaurantItemEntity(restaurant_id);
        List<ItemQuantityResponseItem> itemQuantityResponseItems = getItemListFromRestaurantItemEntity(restaurantItemEntities);
        return new ResponseEntity<List<ItemQuantityResponseItem>>(itemQuantityResponseItems, HttpStatus.OK);
    }

    public List<ItemQuantityResponseItem> getItemListFromRestaurantItemEntity(List<RestaurantItemEntity> restaurantItemEntities){
        List<ItemQuantityResponseItem> itemQuantityResponseItems = new ArrayList<ItemQuantityResponseItem>();
        for(RestaurantItemEntity restaurantItemEntity:restaurantItemEntities){
            ItemQuantityResponseItem it=new ItemQuantityResponseItem().id(UUID.fromString(restaurantItemEntity.getItemEntity().getUuid()))
                    .itemName(restaurantItemEntity.getItemEntity().getItemName()).itemPrice(restaurantItemEntity.getItemEntity().getPrice())
                    .type(restaurantItemEntity.getItemEntity().getType().equals("0")? ItemQuantityResponseItem.TypeEnum.VEG:ItemQuantityResponseItem.TypeEnum.NON_VEG);
            itemQuantityResponseItems.add(it);
        }
        return itemQuantityResponseItems;
    }
}
