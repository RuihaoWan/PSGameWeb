package db;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
/** db操作类的统一接口
 * 
 * @author vanri
 */
public interface DBConnection {
	/**
	 * 关闭连接
	 */
	public void close();
	
	/**
	 * Insert the favorite items for a user.
	 * @param userId
	 * @param itemIds
	 */
	public void setFavoriteItems(String userId,List<String> itemIds);
	
	/**
	 * Delete the favorite items for a user
	 */
	public void unsetFavoriteItems(String userId,List<String> itemIds);
	
	/**
	 * 获取用户的关注列表
	 * @param userId
	 * @return 该用户的关注列表(以itemId set表示)
	 */
	public Set<String> getFavoriteItemIds(String userId);
	
	/**
	 * 获取用户的关注列表
	 * @param userId
	 * @return 该用户的关注列表(以item set表示)
	 */
	public Set<Item> getFvoriteItems(String userId);
	
	/**
	 * 以经纬度/关键词进行搜索(与数据库无关?)
	 * @param lat
	 * @param lon
	 * @param term
	 * @return 目标数据
	 */
	public List<Item> searchItems(double lat,double lon,String term);
	
	/**
	 * 向数据库存入一个item
	 * @param item
	 */
	public void saveItem(Item item);
	
	/**
	 * 获取用户的名字
	 * @param userId
	 * @return 用户的名称
	 */
	public String getFullname(String userId);
	
	/**
	 * 校验用户信息
	 * @param userId
	 * @param password
	 * @return 是否存在该用户
	 */
	public boolean verifyLogin(String userId, String password);
	
	/**
	 * 获取目标item的分类信息
	 * @param itemId
	 * @return 其分类的set
	 */
	public Set<String> getCategories(String itemId);
	
	/**
	 * 验证注册用户是否存在同名
	 * @param userId
	 * @param password
	 * @return
	 */
	public boolean verifyRegister(String userId);
	
	/**
	 * 将注册用户信息添加到数据库中
	 * @param obj
	 * @throws JSONException 
	 */
	public void saveUser(JSONObject obj) throws JSONException;
}
