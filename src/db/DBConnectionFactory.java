package db;

import db.mysql.MySQLConnection;
/**
 * ��������ģʽ�������ݿ������
 * @author vanri
 *
 */

public class DBConnectionFactory {
	private static final String DEFAULT_DB = "mysql";
	/**
	 * ���ݴ���Ĳ�������������db������
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
	 * ʹ��Ĭ�ϵ�db���� mysql
	 * @return
	 */
	public static DBConnection getConnection(){
		return getConnection(DEFAULT_DB);
	}
}
