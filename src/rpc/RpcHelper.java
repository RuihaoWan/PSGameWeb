package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Helper 类,用来将JSONArray与JSONObject写入到response里
 * @author vanri
 *
 */
public class RpcHelper {
	/**
	 * 向response写入要写入的Object
	 * @param response response接口
	 * @param obj 要写入response的JSONObject
	 * @throws IOException
	 */
	public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException{
		//写入一个JSONObject的过程 1.使用getWriter方法获取response的writer接口
		PrintWriter out = response.getWriter();
		try{
			//2.设置返回的类型为json
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
	 * 向response写入要写入的Array
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
