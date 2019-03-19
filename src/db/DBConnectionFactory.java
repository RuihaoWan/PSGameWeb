package db;

import db.mysql.MySQLConnection;
/**
 * 用来工厂模式创建数据库类的类
 * @author vanri
 *
 */

public class DBConnectionFactory {
	private static final String DEFAULT_DB = "mysql";
	/**
	 * 依据传入的参数来决定创建db的类型
	 * @param db
	 * @return
	 */
	public static DBConnection getConnection(String db){
		switch(db){
		case "mysql":
			return new MySQLConnection();
		case "mongodb":
			return null;
		default:
			throw new IllegalArgumentException("Invalid db:" + db);
		}
	}
	/**
	 * 使用默认的db类型 mysql
	 * @return
	 */
	public static DBConnection getConnection(){
		return getConnection(DEFAULT_DB);
	}
}
