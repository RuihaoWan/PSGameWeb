package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json"; //事件查询接口
	private static final String DEFAULT_KEYWORD = ""; 
	private static final String API_KEY = "wNzhx1yPyoRrfSNjXqirnr7OyvN0h944"; //用户key
	private static final String EMBEDDED = "_embedded"; //将ticketmaster返回的json中一些关键字进行封装
	private static final String EVENTS = "events";
	private static final String NAME = "name";
	private static final String ID = "id";
	private static final String URL_STR = "url";
	private static final String RATING = "rating";
	private static final String DISTANCE = "distance";
	private static final String VENUES = "venues";
	private static final String ADDRESS = "address";
	private static final String LINE1 = "line1";
	private static final String LINE2 = "line2";
	private static final String LINE3 = "line3";
	private static final String CITY = "city";
	private static final String IMAGES = "images";
	private static final String CLASSIFICATIONS = "classifications";
	private static final String SEGMENT = "segment";


	/**
	 * 搜索函数
	 * @param lat
	 * @param lon
	 * @param keyword
	 * @return 搜索得到的itemList
	 */
	public List<Item> search(double lat,double lon,String keyword){
		if(keyword == null){ //如果没有输入keyword,以默认代替
			keyword = DEFAULT_KEYWORD;
		}try{
			keyword = java.net.URLEncoder.encode(keyword,"UTF-8");//具体功能未知
		}catch(Exception e){
			e.printStackTrace();
		}
		String geoHash = GeoHash.encodeGeohash(lat, lon, 8); //将lat与lon进行字符hash,精度为8
		String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s", API_KEY,geoHash,keyword,50);//更具指定format构建string
		try{
			HttpURLConnection connection = (HttpURLConnection)new URL(URL + "?" + query).openConnection();//构建与目标url进行http链接的实例
			//注意openConnection()并不直接连接远方url,而是在执行其他函数的时候进行连接,如果要直接连接使用.connect()
			connection.setRequestMethod("GET");//HttpURLConnection.setRequestMethod() 指明连接方法
			int responseCode = connection.getResponseCode();//HttpURLConnection.getResponseCode() 获取状态代码
			System.out.println("\nSending 'GET' request to URL:" + URL + "?" + query); 
			System.out.println("Response Code:" + responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream())); //bufferread类已当前编码获取InputStreamReader类的实际值
			String inputLine;
			StringBuilder response = new StringBuilder();
			while((inputLine = in.readLine())!=null){
				response.append(inputLine);
			}
			in.close();//将bufferedReader的值转入一个StringBuilder里
			JSONObject obj = new JSONObject(response.toString());
			if(obj.isNull("_embedded")){
				return new ArrayList<>();
			}
			JSONObject embedded = obj.getJSONObject("_embedded");//embedded为_embedded这个JSONObject
			JSONArray events = embedded.getJSONArray("events");//从embedded里获取event
			return getItemList(events);//将events从JSONArray转为item List
		}catch(Exception e){
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	/** This method is a common interface for search 
	 * @param lat
	 * @param lon
	 */
	private void queryAPI(double lat,double lon){ //已默认keyword查询当前lat与lon的测试函数
		List<Item> itemList = search(lat,lon,null);
		try{
			for(Item item:itemList){
				JSONObject jsonObject = item.toJSONObject(); //将item list转为jsonArray并输出
				System.out.println(jsonObject);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Main entry for sample TicketMaster API requests.
	 * 单元测试函数
	 */
	public static void main(String[] args) {
		TicketMasterAPI tmApi = new TicketMasterAPI();
		// Mountain View, CA
		// tmApi.queryAPI(37.38, -122.08);
		// London, UK
		// tmApi.queryAPI(51.503364, -0.12);
		// Houston, TX
		tmApi.queryAPI(29.682684, -95.295410);
	}
	
	/**
	 * Helper methods
	 */

	//  {
	//    "name": "laioffer",
              //    "id": "12345",
              //    "url": "www.laioffer.com",
	//    ...
	//    "_embedded": {
	//	    "venues": [
	//	        {
	//		        "address": {
	//		           "line1": "101 First St,",
	//		           "line2": "Suite 101",
	//		           "line3": "...",
	//		        },
	//		        "city": {
	//		        	"name": "San Francisco"
	//		        }
	//		        ...
	//	        },
	//	        ...
	//	    ]
	//    }
	//    ...
	//  }
	//Step 2: 构造辅助函数getAddress,获取JSONObject里的Address信息
	//		详细的JSONObject结构参见官网
	private String getAddress(JSONObject event) throws JSONException {
		if (!event.isNull(EMBEDDED)) {
			JSONObject embedded = event.getJSONObject(EMBEDDED);
			
			if (!embedded.isNull(VENUES)) {
				JSONArray venues = embedded.getJSONArray(VENUES);
				
				for (int i = 0; i < venues.length(); ++i) {
JSONObject venue = venues.getJSONObject(i);
					
					StringBuilder sb = new StringBuilder();
					
					if (!venue.isNull(ADDRESS)) {
						JSONObject address = venue.getJSONObject(ADDRESS);
						
						if (!address.isNull(LINE1)) {
							sb.append(address.getString(LINE1));
						}
						if (!address.isNull(LINE2)) {
							sb.append('\n');
							sb.append(address.getString(LINE2));
						}
						if (!address.isNull(LINE3)) {
							sb.append('\n');
							sb.append(address.getString(LINE3));
						}
					}
					
					if (!venue.isNull(CITY)) {
						JSONObject city = venue.getJSONObject(CITY);
						
						if (!city.isNull(NAME)) {
							sb.append('\n');
							sb.append(city.getString(NAME));
						}
					}
					
					String addr = sb.toString();
					if (!addr.equals("")) {
						return addr;
					}
				}
			}
		}
		return "";
	}
	
	// {"images": [{"url": "www.example.com/my_image.jpg"}, ...]}
	//Step 3: 构造辅助函数获取图片url
	private String getImageUrl(JSONObject event) throws JSONException {
		if (!event.isNull(IMAGES)) {
			JSONArray array = event.getJSONArray(IMAGES);
			for (int i = 0; i < array.length(); i++) {
				JSONObject image = array.getJSONObject(i);
				if (!image.isNull(URL_STR)) {
					return image.getString(URL_STR);
				}
			}
		}
		return "";
	}

	// {"classifications" : [{"segment": {"name": "music"}}, ...]}
	//Step 4: 构造辅助函数获取分类
	private Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>();

		if (!event.isNull(CLASSIFICATIONS)) {
			JSONArray classifications = event.getJSONArray(CLASSIFICATIONS);
			
			for (int i = 0; i < classifications.length(); ++i) {
				JSONObject classification = classifications.getJSONObject(i);
				
				if (!classification.isNull(SEGMENT)) {
					JSONObject segment = classification.getJSONObject(SEGMENT);
					
					if (!segment.isNull(NAME)) {
						categories.add(segment.getString(NAME));
					}
				}
			}
		}

		return categories;
	}

	// Convert JSONArray to a list of item objects.
	//Step 1 将返回的JSONArray转为一个itemList
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();

		for (int i = 0; i < events.length(); ++i) {
			JSONObject event = events.getJSONObject(i);
			//构造者模式,构造一个builder
			ItemBuilder builder = new ItemBuilder();
			
			if (!event.isNull(NAME)) {
				builder.setName(event.getString(NAME));
			}
			if (!event.isNull(ID)) {
				builder.setItemId(event.getString(ID));
			}
			if (!event.isNull(URL_STR)) {
				builder.setUrl(event.getString(URL_STR));
			}
			if (!event.isNull(RATING)) {
				builder.setRating(event.getDouble(RATING));
			}
			if (!event.isNull(DISTANCE)) {
				builder.setDistance(event.getDouble(DISTANCE));
			}
			//使用helper方法获取 address/分类/图片url
			builder.setAddress(getAddress(event));
			builder.setCategories(getCategories(event));
			builder.setImageUrl(getImageUrl(event));
			//builder.build()返回一个new Item对象
			/**建造者模式:1.builder.set()->设置builder的内容
			 * 			2.builder.build() -> 调用item构造方法
			 * 			3.item构造方法item() -> 将builder的值赋给item
			 * 			4.好处:item的值只能在构造时被设置
			*/	
			itemList.add(builder.build());
		}
		return itemList;
	}
}
