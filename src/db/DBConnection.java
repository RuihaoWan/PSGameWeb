package db;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
/** db�������ͳһ�ӿ�
 * 
 * @author vanri
 */
public interface DBConnection {
	/**
	 * �ر�����
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
	 * ��ȡ�û��Ĺ�ע�б�
	 * @param userId
	 * @return ���û��Ĺ�ע�б�(��itemId set��ʾ)
	 */
	public Set<String> getFavoriteItemIds(String userId);
	
	/**
	 * ��ȡ�û��Ĺ�ע�б�
	 * @param userId
	 * @return ���û��Ĺ�ע�б�(��item set��ʾ)
	 */
	public Set<Item> getFvoriteItems(String userId);
	
	/**
	 * �Ծ�γ��/�ؼ��ʽ�������(�����ݿ��޹�?)
	 * @param lat
	 * @param lon
	 * @param term
	 * @return Ŀ������
	 */
	public List<Item> searchItems(double lat,double lon,String term);
	
	/**
	 * �����ݿ����һ��item
	 * @param item
	 */
	public void saveItem(Item item);
	
	/**
	 * ��ȡ�û�������
	 * @param userId
	 * @return �û�������
	 */
	public String getFullname(String userId);
	
	/**
	 * У���û���Ϣ
	 * @param userId
	 * @param password
	 * @return �Ƿ���ڸ��û�
	 */
	public boolean verifyLogin(String userId, String password);
	
	/**
	 * ��ȡĿ��item�ķ�����Ϣ
	 * @param itemId
	 * @return ������set
	 */
	public Set<String> getCategories(String itemId);
	
	/**
	 * ��֤ע���û��Ƿ����ͬ��
	 * @param userId
	 * @param password
	 * @return
	 */
	public boolean verifyRegister(String userId);
	
	/**
	 * ��ע���û���Ϣ��ӵ����ݿ���
	 * @param obj
	 * @throws JSONException 
	 */
	public void saveUser(JSONObject obj) throws JSONException;
}
