package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import algorithm.GeoRecommendation;
import entity.Item;
import entity.Item.ItemBuilder;

/**
 * Servlet implementation class RecommendItem
 */
@WebServlet("/recommendation")
public class RecommendItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecommendItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    //**Step1: 重写get函数使其接受http请求,获得推荐数据并返回*/
    //get 函数,当用户访问recommendation service时返回recommendation数据
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}

                             
		String userId = session.getAttribute("user_id").toString(); 

		//String userId = request.getParameter("user_id");
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		//保存http请求的内容
		GeoRecommendation recommendation = new GeoRecommendation();
		//src/algorithm/geoRecommendation 的实例,其recommendItems 方法返回推荐数据
		List<Item> items = recommendation.recommendItems(userId, lat, lon);
		//items 为推荐系统返回的推荐内容
		JSONArray array = new JSONArray();
		for(Item item : items){
			array.put(item.toJSONObject());
			//item.toJSONObject 方法定义在item entity中,将item的内容转为一个jsonObject对象
		}
		RpcHelper.writeJsonArray(response, array);
		//
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
