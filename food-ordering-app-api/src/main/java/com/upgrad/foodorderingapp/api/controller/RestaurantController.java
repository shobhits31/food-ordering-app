package com.upgrad.foodorderingapp.api.controller;
import static com.upgrad.foodorderingapp.service.common.GenericErrorCode.*;
import com.upgrad.foodorderingapp.api.model.*;
import com.upgrad.foodorderingapp.service.businness.CategoryService;
import com.upgrad.foodorderingapp.service.businness.ItemService;
import com.upgrad.foodorderingapp.service.businness.RestaurantService;
import com.upgrad.foodorderingapp.service.common.ItemType;
import com.upgrad.foodorderingapp.service.entity.CategoryEntity;
import com.upgrad.foodorderingapp.service.entity.CategoryItemEntity;
import com.upgrad.foodorderingapp.service.entity.ItemEntity;
import com.upgrad.foodorderingapp.service.entity.RestaurantEntity;
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

    @RequestMapping(method = RequestMethod.GET,path = "/{restaurant_id}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getRestaurantsId(@PathVariable final String restaurant_id) throws RestaurantNotFoundException,CategoryNotFoundException {
        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurant_id);
        RestaurantDetailsResponse restaurantDetailsResponse= getRestaurantCategoryAndItemDetails(restaurantEntity);
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

    private RestaurantDetailsResponse getRestaurantCategoryAndItemDetails(RestaurantEntity restaurantEntities) throws CategoryNotFoundException{
        List<CategoryEntity> categoryItemEntities=categoryService.getCategoriesByRestaurant(restaurantEntities.getUuid());
        RestaurantDetailsResponse restaurantDetailsResponse = modelMapper.map(restaurantEntities,RestaurantDetailsResponse.class);
        restaurantDetailsResponse.setId(UUID.fromString(restaurantEntities.getUuid()));
        restaurantDetailsResponse.getAddress().setId(UUID.fromString(restaurantEntities.getAddress().getUuid()));
        restaurantDetailsResponse.getAddress().getState().setId(UUID.fromString(restaurantEntities.getAddress().getState().getUuid()));
        restaurantDetailsResponse.setAveragePrice(restaurantEntities.getAvgPrice());
        List<CategoryList> categoryList = new ArrayList<CategoryList>();
        for(CategoryEntity ct:categoryItemEntities){
            List<ItemEntity> itemLists = itemService.getItemsByCategoryAndRestaurant(restaurantEntities.getUuid(), ct.getUuid());
            List<ItemList> itemsList = new ArrayList<ItemList>();
            for(ItemEntity it:itemLists){
                ItemList itemList = new ItemList().itemName(it.getItemName()).price(it.getPrice()).id(UUID.fromString(it.getUuid())).itemType(it.getType().ordinal()==0?ItemList.ItemTypeEnum.VEG:ItemList.ItemTypeEnum.NON_VEG);
                itemsList.add(itemList);
            }
            CategoryList category= new CategoryList();
            category.setCategoryName(ct.getCategoryName());
            category.setId(UUID.fromString(ct.getUuid()));
            category.setItemList(itemsList);
            categoryList.add(category);
        }

        restaurantDetailsResponse.setCategories(categoryList);
        return restaurantDetailsResponse;
    }

}
