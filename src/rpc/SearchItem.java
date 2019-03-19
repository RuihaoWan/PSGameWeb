package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;
import external.TicketMasterAPI;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     * 搜索servlet
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * 收到get请求后执行返回
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		//if (session == null) {
		//	response.setStatus(403);
		//	return;
		//}

		String userId = request.getParameter("user_id");
        if(session!=null){
        	userId = session.getAttribute("user_id").toString(); 
        }                      	
		//解析get请求的步骤 1.从请求中解析参数 
		double lat = Double.parseDouble(request.getParameter("lat")); //从请求中解析lat参数
		double lon = Double.parseDouble(request.getParameter("lon")); //从请求中解析lon参数
		String term = request.getParameter("term");
		
		//2.根据解析的参数,执行功能 : 建立数据库连接
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
			//使用searchItem方法从TicketMaster SEARCH数据
			List<Item> items = connection.searchItems(lat, lon, term);
			//根据userId从数据库中获取favorite items list
			Set<String> favoriteItems = connection.getFavoriteItemIds(userId);
			JSONArray array = new JSONArray();
			for (Item item : items) {
				JSONObject obj = item.toJSONObject();
				//对item list里每一项数据进行check, 看其是否是用户的favorite item
				obj.put("favorite",favoriteItems.contains(item.getItemId()));
				array.put(obj);
			}
			//3.返回执行的结果 : 返回所有项目,并被标记了是否是favorite item
			RpcHelper.writeJsonArray(response, array);
		}catch(JSONException e){
			e.printStackTrace();
		}
		finally {
			connection.close();
		}
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//post与get方法使用一致
		doGet(request, response);
	}

}
