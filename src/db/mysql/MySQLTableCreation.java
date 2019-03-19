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
			 * �ô�����
			 * 1.��϶ȵ�,����Ҫimport��Ӧ��
			 * 2.����Ҫ�������ʵ��,ֻ��Ҫ���ز���ʼ����ʱ
			 * 3.�����ʵ����,��ʵ��ע�����driver,��Ϊע����̰�����driver��constructor��
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
			//1.����items table,varchar�Ǳ䳤�ַ���,ָ�� item_idΪ����
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
			//2.����categories table,ָ��item_idΪ����,��Ϊitem �� item_id�����
			sql = "CREATE TABLE categories ("
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "category VARCHAR(255) NOT NULL,"
					+ "PRIMARY KEY (item_id, category),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id)"
					+ ")";
			stmt.executeUpdate(sql);
			//3.����user table,ָ��useridΪ����,�����¼�����û�����
			sql = "CREATE TABLE users ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "password VARCHAR(255) NOT NULL,"
					+ "first_name VARCHAR(255),"
					+ "last_name VARCHAR(255),"
					+ "PRIMARY KEY (user_id)"
					+ ")";
			stmt.executeUpdate(sql);
			//4.����history table,ָ��userid+itemidΪ����,itemidΪitem��itemid�����,useridΪusers��userid�����
			sql = "CREATE TABLE history ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
					+ "PRIMARY KEY (user_id, item_id),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id),"
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id)"
					+ ")";
			stmt.executeUpdate(sql);
			
			// Step 4: ����һ���û�����.��ɾ��
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
