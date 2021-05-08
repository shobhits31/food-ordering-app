package com.upgrad.foodorderingapp.api.controller;
import static com.upgrad.foodorderingapp.service.common.GenericErrorCode.*;
import com.upgrad.foodorderingapp.api.model.*;
import com.upgrad.foodorderingapp.service.businness.CategoryService;
import com.upgrad.foodorderingapp.service.businness.ItemService;
import com.upgrad.foodorderingapp.service.businness.RestaurantService;
import com.upgrad.foodorderingapp.service.common.ItemType;
import com.upgrad.foodorderingapp.service.entity.*;
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

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ItemService itemService;

    @RequestMapping(method = RequestMethod.GET,path = "",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurants() {
        List<RestaurantEntity> restaurantEntityList = restaurantService.restaurantsByRating();
        RestaurantListResponse restaurantListResponse = getRestaurantCategoryDetails(restaurantEntityList);
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET,path = "/name/{restaurant_name}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByName(@PathVariable final String restaurant_name) throws RestaurantNotFoundException {
        List<RestaurantEntity> restaurantEntityList = restaurantService.restaurantsByName(restaurant_name);
        RestaurantListResponse restaurantListResponse= getRestaurantCategoryDetails(restaurantEntityList);
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> restaurantByUUID( @PathVariable String category_id) throws CategoryNotFoundException {
        List<RestaurantEntity> restaurantEntities = restaurantService.restaurantByCategory(category_id);
        RestaurantListResponse restaurantListResponse = getRestaurantCategoryDetails(restaurantEntities);
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET,path = "/{restaurant_id}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getRestaurantsId(@PathVariable final String restaurant_id) throws RestaurantNotFoundException,CategoryNotFoundException {
        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurant_id);
        RestaurantDetailsResponse restaurantDetailsResponse = modelMapper.map(restaurantEntity,RestaurantDetailsResponse.class);
        restaurantDetailsResponse.setId(UUID.fromString(restaurantEntity.getUuid()));
        restaurantDetailsResponse.getAddress().setId(UUID.fromString(restaurantEntity.getAddress().getUuid()));
        restaurantDetailsResponse.getAddress().getState().setId(UUID.fromString(restaurantEntity.getAddress().getState().getUuid()));
        restaurantDetailsResponse.setAveragePrice(restaurantEntity.getAvgPrice());
        List<CategoryList> categoryLists = new ArrayList<>();
        List<CategoryEntity> categoryEntities = categoryService.getCategoriesByRestaurant(restaurant_id);

        for(CategoryEntity categoryEntity: categoryEntities){
            CategoryList categoryList = new CategoryList();
            categoryList.setId(UUID.fromString(categoryEntity.getUuid()));
            categoryList.setCategoryName(categoryEntity.getCategoryName());
            List<ItemEntity> itemEntities = itemService.getItemsByCategoryAndRestaurant(restaurant_id,categoryEntity.getUuid());

            List<ItemList> itemLists = new ArrayList<>();
            for(ItemEntity itemEntity: itemEntities){
                itemLists.add(setItemList(itemEntity));
            }
            categoryList.setItemList(itemLists);
            categoryLists.add(categoryList);
        }

        restaurantDetailsResponse.setCategories(categoryLists);

        return new ResponseEntity<RestaurantDetailsResponse>(restaurantDetailsResponse, HttpStatus.OK);
    }


    private RestaurantListResponse getRestaurantCategoryDetails(List<RestaurantEntity> restaurantEntities){
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();

        for(RestaurantEntity rt:restaurantEntities){
            String category="";
            List<CategoryEntity> categoryEntities=categoryService.getCategoriesByRestaurant(rt.getUuid());
            Collections.sort(categoryEntities, new Comparator<CategoryEntity>() {
                @Override
                public int compare(CategoryEntity categoryEntity, CategoryEntity t1) {
                    return categoryEntity.getCategoryName().compareToIgnoreCase(t1.getCategoryName());
                }
            });
            for(int i=0;i<categoryEntities.size();i++){
                if(i!=categoryEntities.size()-1)
                    category+=categoryEntities.get(i).getCategoryName()+",";
                else
                    category+=categoryEntities.get(i).getCategoryName();
            }

            RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = modelMapper.map(rt.getAddress(),RestaurantDetailsResponseAddress.class);
            restaurantDetailsResponseAddress.setId(UUID.fromString(rt.getAddress().getUuid()));
            restaurantDetailsResponseAddress.getState().setId(UUID.fromString(rt.getAddress().getState().getUuid()));
           // restaurantDetailsResponseAddress.setFlatBuildingName(rt.getAddress().getFlatBuilNumber());
            RestaurantList restaurantList = new RestaurantList().restaurantName(rt.getRestaurantName()).id(UUID.fromString(rt.getUuid()))
                    .photoURL(rt.getPhotoUrl()).customerRating(new BigDecimal(rt.getCustomerRating())).averagePrice(rt.getAvgPrice()).numberCustomersRated(rt.getNumberCustomersRated())
                    .categories(category).address(restaurantDetailsResponseAddress);
            restaurantListResponse.addRestaurantsItem(restaurantList);
        }
        return restaurantListResponse;
    }

    private ItemList setItemList(ItemEntity itemEntity) {
        ItemList itemList = new ItemList();
        itemList.setId(UUID.fromString(itemEntity.getUuid()));
        itemList.setItemName(itemEntity.getItemName());
        itemList.setPrice(itemEntity.getPrice());
        String itemType = itemEntity.getType()!=null && itemEntity.getType().ordinal()==0? ItemType.VEG.name():ItemType.NON_VEG.name();
        itemList.setItemType(ItemList.ItemTypeEnum.fromValue(itemType));
        return itemList;
    }

}
