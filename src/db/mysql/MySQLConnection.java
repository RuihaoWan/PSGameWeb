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
 * mysql数据库操作类
 * @author vanri
 *
 */
public class MySQLConnection implements DBConnection{
	private Connection conn;
	/**
	 * constructor 根据mysqlUtil里的数据加载数据库连接
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
	 * 根据输入的userId与 item list向history里插入数据
	 */
	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		if(conn == null){
			System.err.println("DB connection failed");
			return;
		}
		try{
			//操作数据库步骤 1.建立sql string
			String sql = "INSERT IGNORE INTO history(user_id,item_id)VALUES(?,?)";//ignore的作用是忽略重复插入的错误
			//2.根据所建立的sql string建立 prepareStatement 对象
			PreparedStatement stmt = conn.prepareStatement(sql);
			//3.对sql语句中的保留项(?)进行赋值
			stmt.setString(1, userId);
			for(String itemId : itemIds){
				stmt.setString(2,itemId);
				//4.在适当时候执行sql,check一个SQLException
				stmt.execute();
			}
		}
		catch(SQLException e){
				e.printStackTrace();
		}
	}
	/**
	 * 根据输入的userId与itemId list从history里删除数据
	 */
	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		if(conn == null){
			System.err.println("DB connection failed");
			return;
		}
		try{
			//从数据库中删除userId == 输入userId,且itemId 存在于传入的itemId list的数据
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
	 * 从history数据库中根据userId选出itemId
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
	 * 从history数据库中根据userId选出items
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
			//从数据库选取数据步骤 1.建立选取sql语句
			String sql = "SELECT * FROM items WHERE item_id = ?";
			//2.根据sql语句创建preparedStatement
			PreparedStatement stmt = conn.prepareStatement(sql);
			for(String itemId : itemIds){
				//3.使用setString()方法填充sql语句里的项目
				stmt.setString(1, itemId);
				//4.使用 ResultSet rs = stmt.executeQuery(); 获取到返回值
				ResultSet rs = stmt.executeQuery();
				ItemBuilder builder = new ItemBuilder();
				while(rs.next()){
					//5.返回值为一个key-value的array,对array每一项进行解析,构建item List
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
	 * 根据lat,lon,term进行search
	 * 注意這是唯一調用ticketMasterApi裏searchItem方法的接口
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
	 * 將输入item存储到item db中
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
			//ignore 在数据库已有此项目时不出exception(主键重复)
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
	 * 根据userId从users数据库中获取其全名
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
	 * 从users数据库中查询用户名,密码是否存在
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
	 * 根据itemId在categories数据库中查询它的分类
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
