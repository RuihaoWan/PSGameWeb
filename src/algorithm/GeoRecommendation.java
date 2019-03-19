package algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Map.Entry;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

public class GeoRecommendation {
	public List<Item> recommendItems(String userId, double lat, double lon){
		List<Item> recommendItems = new ArrayList<>();//build for result
		DBConnection conn = DBConnectionFactory.getConnection();
		try{
			//Step1 Get all favorite items
			Set<String> favoriteItemIds = conn.getFavoriteItemIds(userId);//get items by userId
			Map<String, Integer> allCategories = new HashMap();
			//Step2 将allCategories 装载所有favorite Item的category
			for(String itemId : favoriteItemIds){
				Set<String> categories = conn.getCategories(itemId);//get the categories from db by id
				for(String category : categories){
					allCategories.put(category, allCategories.getOrDefault(categories, 0) + 1);
				}
			}
			//
			List<Entry<String, Integer>> categoryList = new ArrayList<>(allCategories.entrySet());
			//categoryList 是categoryMap.Entry 的数组
			Collections.sort(categoryList,new Comparator<Entry<String, Integer>>(){
				@Override
				public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2){
					return Integer.compare(o2.getValue(), o1.getValue());
				}
			});
			//将categoryList按照category的出现次数进行降序排序
			//Step3 进行过滤
			Set<Item> visitedItems = new HashSet<>();
			for(Entry<String, Integer> category : categoryList){
				List<Item> items = conn.searchItems(lat, lon, category.getKey());
				//获取数据库中所有的目标category的items
				List<Item> filteredItems = new ArrayList<>();
				//建立list存储过滤后的数据
				for(Item item : items){
					if(!favoriteItemIds.contains(item.getItemId())&&!visitedItems.contains(item)){
						//如果favoriteItem list里面没有这个item,并且visitedItem里也没有这个item的话
						//不需要推荐用户已经收藏的item,也不用重复推荐(因为一个item可能有多个分类,所以可能会被多次访问)
						filteredItems.add(item);
					}
				}
				Collections.sort(filteredItems,new Comparator<Item>() {
					@Override
					public int compare(Item item1, Item item2){
						return Double.compare(item1.getDistance(), item2.getDistance());
						//按照地理距离升序排列
					}
				});
				visitedItems.addAll(items); //在这里把所有item装入已visitedItem中避免重复
				recommendItems.addAll(filteredItems);//排序后的item 都装入到recommdItem list中
			}
		}finally{
			conn.close();
		}
		return recommendItems;
	}
}
