package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.jdbc.Statement;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.TicketMasterAPI;

/**
 * mysql���ݿ������
 * @author vanri
 *
 */
public class MySQLConnection implements DBConnection{
	private Connection conn;
	/**
	 * constructor ����mysqlUtil������ݼ������ݿ�����
	 */
	public MySQLConnection(){
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void close() {
		if(conn != null){
			try{
				conn.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	/**
	 * ���������userId�� item list��history���������
	 */
	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		if(conn == null){
			System.err.println("DB connection failed");
			return;
		}
		try{
			//�������ݿⲽ�� 1.����sql string
			String sql = "INSERT IGNORE INTO history(user_id,item_id)VALUES(?,?)";//ignore�������Ǻ����ظ�����Ĵ���
			//2.������������sql string���� prepareStatement ����
			PreparedStatement stmt = conn.prepareStatement(sql);
			//3.��sql����еı�����(?)���и�ֵ
			stmt.setString(1, userId);
			for(String itemId : itemIds){
				stmt.setString(2,itemId);
				//4.���ʵ�ʱ��ִ��sql,checkһ��SQLException
				stmt.execute();
			}
		}
		catch(SQLException e){
				e.printStackTrace();
		}
	}
	/**
	 * ���������userId��itemId list��history��ɾ������
	 */
	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		if(conn == null){
			System.err.println("DB connection failed");
			return;
		}
		try{
			//�����ݿ���ɾ��userId == ����userId,��itemId �����ڴ����itemId list������
			String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			for(String itemId : itemIds){
				stmt.setString(2, itemId);
				stmt.execute();
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * ��history���ݿ��и���userIdѡ��itemId
	 */
	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		if(conn == null){
			return new HashSet<>();
		}
		Set<String> favoriteItemIds = new HashSet<>();
		try{
			String sql = "SELECT item_id FROM history WHERE user_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				String itemId = rs.getString("item_id");
				favoriteItemIds.add(itemId);
			}	
		}catch(SQLException e){
			e.printStackTrace();
		}
		return favoriteItemIds;
	}
	/**
	 * ��history���ݿ��и���userIdѡ��items
	 */
	@Override
	public Set<Item> getFvoriteItems(String userId) {
		if(conn == null){
			System.err.println("DB connection failed");
			return new HashSet<>();
		}
		Set<Item> favoriteItems = new HashSet<>();
		Set<String> itemIds = getFavoriteItemIds(userId);
		try{
			//�����ݿ�ѡȡ���ݲ��� 1.����ѡȡsql���
			String sql = "SELECT * FROM items WHERE item_id = ?";
			//2.����sql��䴴��preparedStatement
			PreparedStatement stmt = conn.prepareStatement(sql);
			for(String itemId : itemIds){
				//3.ʹ��setString()�������sql��������Ŀ
				stmt.setString(1, itemId);
				//4.ʹ�� ResultSet rs = stmt.executeQuery(); ��ȡ������ֵ
				ResultSet rs = stmt.executeQuery();
				ItemBuilder builder = new ItemBuilder();
				while(rs.next()){
					//5.����ֵΪһ��key-value��array,��arrayÿһ����н���,����item List
					builder.setItemId(rs.getString("item_id"));
					builder.setName(rs.getString("name"));
					builder.setAddress(rs.getString("address"));
					builder.setImageUrl(rs.getString("image_url"));
					builder.setUrl(rs.getString("url"));
					builder.setCategories(getCategories(itemId));
					builder.setRating(rs.getDouble("rating"));
					builder.setDistance(rs.getDouble("distance"));
					favoriteItems.add(builder.build());
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return favoriteItems;
	}
	/**
	 * ����lat,lon,term����search
	 * ע���@��Ψһ�{��ticketMasterApi�YsearchItem�����Ľӿ�
	 */
	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		TicketMasterAPI tmAPI = new TicketMasterAPI();
		List<Item> items = tmAPI.search(lat, lon, term);
		for (Item item : items) {
			saveItem(item);
		}
		return items;
	}

	/**
	 * ������item�洢��item db��
	 */
	@Override
	public void saveItem(Item item) {
		if(conn == null){
			System.err.println("DB connection failed!");
			return;
		}
		try{
			//following code to inject SQL
			String sql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?, ?, ?)";
			//ignore �����ݿ����д���Ŀʱ����exception(�����ظ�)
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, item.getItemId());
			stmt.setString(2, item.getName());
			stmt.setDouble(3, item.getRating());
			stmt.setString(4, item.getAddress());
			stmt.setString(5, item.getImageUrl());
			stmt.setString(6, item.getUrl());
			stmt.setDouble(7, item.getDistance());
			stmt.execute();
			
			sql = "INSERT IGNORE INTO categories VALUES (?, ?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, item.getItemId());
			for (String category : item.getCategories()) {
				stmt.setString(2, category);
				stmt.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ����userId��users���ݿ��л�ȡ��ȫ��
	 */
	@Override
	public String getFullname(String userId) {
		if(conn == null){
			return "";
		}
		String name = "";
		try {
			String sql = "SELECT first_name,last_name from users WHERE user_id=?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			if(rs.next()){
				name = String.join(" ", rs.getString("first_name"),rs.getString("last_name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}
	
	/**
	 * ��users���ݿ��в�ѯ�û���,�����Ƿ����
	 */
	@Override
	public boolean verifyLogin(String userId, String password) {
		if(conn == null){
			return false;
		}
		try{
			String sql = "SELECT user_id from users WHERE user_id = ?and password = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, password);
			ResultSet rs = statement.executeQuery();
			if(rs.next()){
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * ����itemId��categories���ݿ��в�ѯ���ķ���
	 */
	@Override
	public Set<String> getCategories(String itemId){
		if(conn == null){
			System.err.println("DB connection failed");
			return null;
		}
		Set<String> categories = new HashSet<>();
		try{
			String sql = "SELECT category FROM categories WHERE item_id=?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, itemId);
			ResultSet rs = statement.executeQuery();
			while(rs.next()){
				categories.add(rs.getString("category"));
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		return categories;
	}

	@Override
	public boolean verifyRegister(String userId) {
		if(conn == null){
			return false;
		}
		try{
			String sql = "SELECT user_id from users WHERE user_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			if(rs.next()){
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void saveUser(JSONObject obj) throws JSONException {
		if(conn == null){
			System.err.println("DB connection failed");
			return;
		}
		try{
			String sql = "INSERT IGNORE INTO users VALUES(?,?,?,?)";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1,obj.getString("user_id"));
			statement.setString(2, obj.getString("password"));
			statement.setString(3, obj.getString("first_name"));
			statement.setString(4, obj.getString("last_name"));
			statement.execute();
		}catch(SQLException e){
			e.printStackTrace();
		}catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
