package com.upgrad.foodorderingapp.service.businness;
import static com.upgrad.foodorderingapp.service.common.GenericErrorCode.*;
import com.upgrad.foodorderingapp.service.dao.CategoryDao;
import com.upgrad.foodorderingapp.service.dao.ItemDao;
import com.upgrad.foodorderingapp.service.entity.CategoryItemEntity;
import com.upgrad.foodorderingapp.service.entity.ItemEntity;
import com.upgrad.foodorderingapp.service.entity.RestaurantEntity;
import com.upgrad.foodorderingapp.service.entity.RestaurantItemEntity;
import com.upgrad.foodorderingapp.service.exception.RestaurantNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final Logger log = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private CategoryDao categoryDao;

    public List<RestaurantItemEntity> getAllRestaurantItemEntity(final String restaurantId) throws RestaurantNotFoundException{
        List<RestaurantItemEntity> restaurantItemEntities = itemDao.getItemForRestaurantUUID(restaurantId);
        if(restaurantItemEntities.size()<1){
            throw new RestaurantNotFoundException(RNF_001.getCode(), RNF_001.getDefaultMessage());
        }
        restaurantItemEntities=getItemsByPopularity(restaurantItemEntities);
        return restaurantItemEntities;
    }

    public  List<RestaurantItemEntity> getItemsByPopularity(List<RestaurantItemEntity> restaurantItemEntities){
        HashMap<Integer,Integer> hmap= new HashMap<Integer,Integer>();
        for(RestaurantItemEntity rt:restaurantItemEntities){
            if(hmap.containsKey(rt.getItemEntity().getId())){
                hmap.replace(rt.getItemEntity().getId(),hmap.get(rt.getItemEntity().getId())+1);
            }else{
                hmap.put(rt.getItemEntity().getId(),1);
            }
        }
        hmap=hmap.entrySet()
                .stream()
                .sorted((Map.Entry.<Integer, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        List<RestaurantItemEntity> finalList = new ArrayList<RestaurantItemEntity>();
        int counter=0;
        for(Map.Entry<Integer,Integer> entry:hmap.entrySet()){
            if(counter>=5){
                break;
            }
            for(RestaurantItemEntity rt:restaurantItemEntities){
                if(rt.getId()==entry.getKey()){
                    finalList.add(rt);
                }
            }
            counter++;
        }
        return finalList;
    }

    public List<ItemEntity> getItemsByCategoryAndRestaurant(final String restaurantID,final String categoryId){
        List<RestaurantItemEntity> restaurantItemEntities = itemDao.getItemForRestaurantUUID(restaurantID);
        List<CategoryItemEntity> categoryItemEntities = categoryDao.getAllCategoryItems(categoryId);
        List<ItemEntity> itemEntityList = new ArrayList<ItemEntity>();
        for(CategoryItemEntity ct:categoryItemEntities){
            boolean found=false;
            for(RestaurantItemEntity rt:restaurantItemEntities){
                if(rt.getItemEntity().getUuid().equals(ct.getItemEntity().getUuid()))
                    found=true;
            }
            if(found)
                itemEntityList.add(ct.getItemEntity());
        }
        return itemEntityList;
    }

}
