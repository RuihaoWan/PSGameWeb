package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MySQLTableCreation {
	public static void main(String[] args){
		try{
			//Step1 Connect to MySQL
			System.out.println("Connecting to"+MySQLDBUtil.URL);
			Class.forName("com.mysql.jdbc.Driver").getConstructor().newInstance();
			/**
			 * 好处在于
			 * 1.耦合度低,不需要import相应类
			 * 2.不需要建立类的实例,只需要加载并初始化类时
			 * 3.在这个实例里,其实是注册这个driver,因为注册过程包含在driver的constructor中
			 */
			Connection conn = DriverManager.getConnection(MySQLDBUtil.URL);
			if(conn == null){
				return;
			}
			//Step2 Drop tables
			Statement stmt = conn.createStatement();
			String sql = "DROP TABLE IF EXISTS categories";
			stmt.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS history";
			stmt.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS items";
			stmt.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS users";
			stmt.executeUpdate(sql);
			// Step 3 Create new tables
			//1.创建items table,varchar是变长字符串,指明 item_id为主键
			sql = "CREATE TABLE items ("
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "name VARCHAR(255),"
					+ "rating FLOAT,"
					+ "address VARCHAR(255),"
					+ "image_url VARCHAR(255),"
					+ "url VARCHAR(255),"
					+ "distance FLOAT,"
					+ "PRIMARY KEY (item_id)"
					+ ")";
			stmt.executeUpdate(sql);
			//2.创建categories table,指明item_id为主键,且为item 里 item_id的外键
			sql = "CREATE TABLE categories ("
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "category VARCHAR(255) NOT NULL,"
					+ "PRIMARY KEY (item_id, category),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id)"
					+ ")";
			stmt.executeUpdate(sql);
			//3.创建user table,指明userid为主键,这个记录的是用户数据
			sql = "CREATE TABLE users ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "password VARCHAR(255) NOT NULL,"
					+ "first_name VARCHAR(255),"
					+ "last_name VARCHAR(255),"
					+ "PRIMARY KEY (user_id)"
					+ ")";
			stmt.executeUpdate(sql);
			//4.创建history table,指明userid+itemid为主键,itemid为item里itemid的外键,userid为users里userid的外键
			sql = "CREATE TABLE history ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
					+ "PRIMARY KEY (user_id, item_id),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id),"
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id)"
					+ ")";
			stmt.executeUpdate(sql);
			
			// Step 4: 创建一个用户数据.后删除
			sql = "INSERT INTO users VALUES ("
					+ "'1111', '3229c1097c00d497a0fd282d586be050', 'John', 'Smith')";
			System.out.println("Executing query: " + sql);
			stmt.executeUpdate(sql);

			System.out.println("Import done successfully");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
