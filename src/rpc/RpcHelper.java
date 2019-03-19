package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Helper ��,������JSONArray��JSONObjectд�뵽response��
 * @author vanri
 *
 */
public class RpcHelper {
	/**
	 * ��responseд��Ҫд���Object
	 * @param response response�ӿ�
	 * @param obj Ҫд��response��JSONObject
	 * @throws IOException
	 */
	public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException{
		//д��һ��JSONObject�Ĺ��� 1.ʹ��getWriter������ȡresponse��writer�ӿ�
		PrintWriter out = response.getWriter();
		try{
			//2.���÷��ص�����Ϊjson
			response.setContentType("application/json");
			response.addHeader("Access-Control-Allow-Origin", "*");
			out.println(obj);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			out.close();
		}
	}
	
	/**
	 * ��responseд��Ҫд���Array
	 * @param response 
	 * @param array
	 * @throws IOException
	 */
	public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException{
		PrintWriter out = response.getWriter();
		try{
			response.setContentType("application/json");
			response.addHeader("Access-Control-Allow-Origin", "*");
			out.println(array);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			out.close();
		}
	}
	public static JSONObject readJsonObject(HttpServletRequest request){
		StringBuilder sb = new StringBuilder();
		try(BufferedReader reader = request.getReader()){
			String line = null;
			while((line = reader.readLine())!=null){
				sb.append(line);
			}
			return new JSONObject(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		return new JSONObject();
	}
}
