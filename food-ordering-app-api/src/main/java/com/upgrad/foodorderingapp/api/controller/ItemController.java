package com.upgrad.foodorderingapp.api.controller;

import com.upgrad.foodorderingapp.api.model.CategoryDetailsResponse;
import com.upgrad.foodorderingapp.api.model.ItemList;
import com.upgrad.foodorderingapp.api.model.ItemListResponse;
import com.upgrad.foodorderingapp.api.model.ItemQuantityResponseItem;
import com.upgrad.foodorderingapp.service.businness.CategoryService;
import com.upgrad.foodorderingapp.service.businness.ItemService;
import com.upgrad.foodorderingapp.service.businness.RestaurantService;
import com.upgrad.foodorderingapp.service.common.ItemType;
import com.upgrad.foodorderingapp.service.entity.CategoryItemEntity;
import com.upgrad.foodorderingapp.service.entity.ItemEntity;
import com.upgrad.foodorderingapp.service.entity.RestaurantEntity;
import com.upgrad.foodorderingapp.service.entity.RestaurantItemEntity;
import com.upgrad.foodorderingapp.service.exception.CategoryNotFoundException;
import com.upgrad.foodorderingapp.service.exception.RestaurantNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import static com.upgrad.foodorderingapp.service.common.GenericErrorCode.*;
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

    @Autowired
    private RestaurantService restaurantService;

    @RequestMapping(method = RequestMethod.GET,path = "/{restaurant_id}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ItemListResponse> getAllRestaurants(@PathVariable final String restaurant_id) throws RestaurantNotFoundException {
        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurant_id);
        List<ItemEntity> itemEntityList = itemService.getItemsByPopularity(restaurantEntity);
        ItemListResponse itemListResponse = getItemListFromRestaurantItemEntity(itemEntityList);
        return new ResponseEntity<ItemListResponse>(itemListResponse, HttpStatus.OK);
    }

    public ItemListResponse getItemListFromRestaurantItemEntity(List<ItemEntity> itemEntities){
        ItemListResponse itemListResponse = new ItemListResponse();
        for(ItemEntity it:itemEntities){

            ItemList item=new ItemList().id(UUID.fromString(it.getUuid()))
                    .itemName(it.getItemName()).price(it.getPrice())
                    .itemType(it.getType().ordinal()==0? ItemList.ItemTypeEnum.VEG: ItemList.ItemTypeEnum.NON_VEG);
            itemListResponse.add(item);
        }
        return itemListResponse;
    }
}
