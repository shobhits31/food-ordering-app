package com.upgrad.foodorderingapp.api.controller;

import com.upgrad.foodorderingapp.api.model.CategoryDetailsResponse;
import com.upgrad.foodorderingapp.api.model.CategoryListResponse;
import com.upgrad.foodorderingapp.api.model.ItemList;
import com.upgrad.foodorderingapp.service.businness.CategoryService;
import com.upgrad.foodorderingapp.service.businness.CustomerService;
import com.upgrad.foodorderingapp.service.common.ItemType;
import com.upgrad.foodorderingapp.service.entity.CategoryEntity;
import com.upgrad.foodorderingapp.service.entity.CategoryItemEntity;
import com.upgrad.foodorderingapp.service.entity.ItemEntity;
import com.upgrad.foodorderingapp.service.exception.CategoryNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.upgrad.foodorderingapp.api.model.ItemList.ItemTypeEnum.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CategoryService categoryService;

    @RequestMapping(method = RequestMethod.GET,path = "/",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<CategoryListResponse>> getAllCategories() throws CategoryNotFoundException {
        List<CategoryEntity> categoryEntityList = categoryService.getAllCategories();
        List<CategoryListResponse> categoryEntityResponseList = convertEntityToResponseList(categoryEntityList);
        return new ResponseEntity<List<CategoryListResponse>>(categoryEntityResponseList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,path = "/{category_id}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryDetailsResponse> getAllCategories(final String category_id) throws CategoryNotFoundException {
        List<CategoryItemEntity> categoryItemEntity = categoryService.getCategoryById(category_id);
        CategoryDetailsResponse categoryDetailsResponseList = getItemListFromCategoryItemEntity(categoryItemEntity);
        return new ResponseEntity<CategoryDetailsResponse>(categoryDetailsResponseList, HttpStatus.OK);
    }



    private List<CategoryListResponse> convertEntityToResponseList (List<CategoryEntity> categoryEntityList){
        List<CategoryListResponse> categoryResponseList=new ArrayList<CategoryListResponse>();
        for(CategoryEntity ct:categoryEntityList){
            CategoryListResponse listResponse = new CategoryListResponse().id(UUID.fromString(ct.getUuid())).categoryName(ct.getCategory_name());
            categoryResponseList.add(listResponse);
        }
        return categoryResponseList;
    }

    private CategoryDetailsResponse getItemListFromCategoryItemEntity(List<CategoryItemEntity> categoryItemEntityList){
        CategoryDetailsResponse categoryDetailsResponse = new CategoryDetailsResponse();
        categoryDetailsResponse.id(UUID.fromString(categoryItemEntityList.get(0).getCategoryEntity().getUuid()));
        categoryDetailsResponse.categoryName(categoryItemEntityList.get(0).getCategoryEntity().getCategory_name());
        List<ItemList> itemList = new ArrayList<ItemList>();
        for(CategoryItemEntity ct:categoryItemEntityList){
            ItemList item = new ItemList().id(UUID.fromString(ct.getItemEntity().getUuid())).itemName(ct.getItemEntity().getItem_name())
                    .price(ct.getItemEntity().getPrice()).itemType(ct.getItemEntity().getType().equals("0")?ItemList.ItemTypeEnum.VEG:ItemList.ItemTypeEnum.NON_VEG);
            itemList.add(item);
        }
        categoryDetailsResponse.setItemList(itemList);
        return categoryDetailsResponse;
    }

}
