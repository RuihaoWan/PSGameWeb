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
			//Step2 ��allCategories װ������favorite Item��category
			for(String itemId : favoriteItemIds){
				Set<String> categories = conn.getCategories(itemId);//get the categories from db by id
				for(String category : categories){
					allCategories.put(category, allCategories.getOrDefault(categories, 0) + 1);
				}
			}
			//
			List<Entry<String, Integer>> categoryList = new ArrayList<>(allCategories.entrySet());
			//categoryList ��categoryMap.Entry ������
			Collections.sort(categoryList,new Comparator<Entry<String, Integer>>(){
				@Override
				public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2){
					return Integer.compare(o2.getValue(), o1.getValue());
				}
			});
			//��categoryList����category�ĳ��ִ������н�������
			//Step3 ���й���
			Set<Item> visitedItems = new HashSet<>();
			for(Entry<String, Integer> category : categoryList){
				List<Item> items = conn.searchItems(lat, lon, category.getKey());
				//��ȡ���ݿ������е�Ŀ��category��items
				List<Item> filteredItems = new ArrayList<>();
				//����list�洢���˺������
				for(Item item : items){
					if(!favoriteItemIds.contains(item.getItemId())&&!visitedItems.contains(item)){
						//���favoriteItem list����û�����item,����visitedItem��Ҳû�����item�Ļ�
						//����Ҫ�Ƽ��û��Ѿ��ղص�item,Ҳ�����ظ��Ƽ�(��Ϊһ��item�����ж������,���Կ��ܻᱻ��η���)
						filteredItems.add(item);
					}
				}
				Collections.sort(filteredItems,new Comparator<Item>() {
					@Override
					public int compare(Item item1, Item item2){
						return Double.compare(item1.getDistance(), item2.getDistance());
						//���յ��������������
					}
				});
				visitedItems.addAll(items); //�����������itemװ����visitedItem�б����ظ�
				recommendItems.addAll(filteredItems);//������item ��װ�뵽recommdItem list��
			}
		}finally{
			conn.close();
		}
		return recommendItems;
	}
}
