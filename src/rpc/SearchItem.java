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
     * ����servlet
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * �յ�get�����ִ�з���
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
		//����get����Ĳ��� 1.�������н������� 
		double lat = Double.parseDouble(request.getParameter("lat")); //�������н���lat����
		double lon = Double.parseDouble(request.getParameter("lon")); //�������н���lon����
		String term = request.getParameter("term");
		
		//2.���ݽ����Ĳ���,ִ�й��� : �������ݿ�����
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
			//ʹ��searchItem������TicketMaster SEARCH����
			List<Item> items = connection.searchItems(lat, lon, term);
			//����userId�����ݿ��л�ȡfavorite items list
			Set<String> favoriteItems = connection.getFavoriteItemIds(userId);
			JSONArray array = new JSONArray();
			for (Item item : items) {
				JSONObject obj = item.toJSONObject();
				//��item list��ÿһ�����ݽ���check, �����Ƿ����û���favorite item
				obj.put("favorite",favoriteItems.contains(item.getItemId()));
				array.put(obj);
			}
			//3.����ִ�еĽ�� : ����������Ŀ,����������Ƿ���favorite item
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
		//post��get����ʹ��һ��
		doGet(request, response);
	}

}
