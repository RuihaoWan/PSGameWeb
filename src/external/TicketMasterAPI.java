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
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json"; //�¼���ѯ�ӿ�
	private static final String DEFAULT_KEYWORD = ""; 
	private static final String API_KEY = "wNzhx1yPyoRrfSNjXqirnr7OyvN0h944"; //�û�key
	private static final String EMBEDDED = "_embedded"; //��ticketmaster���ص�json��һЩ�ؼ��ֽ��з�װ
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
	 * ��������
	 * @param lat
	 * @param lon
	 * @param keyword
	 * @return �����õ���itemList
	 */
	public List<Item> search(double lat,double lon,String keyword){
		if(keyword == null){ //���û������keyword,��Ĭ�ϴ���
			keyword = DEFAULT_KEYWORD;
		}try{
			keyword = java.net.URLEncoder.encode(keyword,"UTF-8");//���幦��δ֪
		}catch(Exception e){
			e.printStackTrace();
		}
		String geoHash = GeoHash.encodeGeohash(lat, lon, 8); //��lat��lon�����ַ�hash,����Ϊ8
		String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s", API_KEY,geoHash,keyword,50);//����ָ��format����string
		try{
			HttpURLConnection connection = (HttpURLConnection)new URL(URL + "?" + query).openConnection();//������Ŀ��url����http���ӵ�ʵ��
			//ע��openConnection()����ֱ������Զ��url,������ִ������������ʱ���������,���Ҫֱ������ʹ��.connect()
			connection.setRequestMethod("GET");//HttpURLConnection.setRequestMethod() ָ�����ӷ���
			int responseCode = connection.getResponseCode();//HttpURLConnection.getResponseCode() ��ȡ״̬����
			System.out.println("\nSending 'GET' request to URL:" + URL + "?" + query); 
			System.out.println("Response Code:" + responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream())); //bufferread���ѵ�ǰ�����ȡInputStreamReader���ʵ��ֵ
			String inputLine;
			StringBuilder response = new StringBuilder();
			while((inputLine = in.readLine())!=null){
				response.append(inputLine);
			}
			in.close();//��bufferedReader��ֵת��һ��StringBuilder��
			JSONObject obj = new JSONObject(response.toString());
			if(obj.isNull("_embedded")){
				return new ArrayList<>();
			}
			JSONObject embedded = obj.getJSONObject("_embedded");//embeddedΪ_embedded���JSONObject
			JSONArray events = embedded.getJSONArray("events");//��embedded���ȡevent
			return getItemList(events);//��events��JSONArrayתΪitem List
		}catch(Exception e){
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	/** This method is a common interface for search 
	 * @param lat
	 * @param lon
	 */
	private void queryAPI(double lat,double lon){ //��Ĭ��keyword��ѯ��ǰlat��lon�Ĳ��Ժ���
		List<Item> itemList = search(lat,lon,null);
		try{
			for(Item item:itemList){
				JSONObject jsonObject = item.toJSONObject(); //��item listתΪjsonArray�����
				System.out.println(jsonObject);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Main entry for sample TicketMaster API requests.
	 * ��Ԫ���Ժ���
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
	//Step 2: ���츨������getAddress,��ȡJSONObject���Address��Ϣ
	//		��ϸ��JSONObject�ṹ�μ�����
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
	//Step 3: ���츨��������ȡͼƬurl
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
	//Step 4: ���츨��������ȡ����
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
	//Step 1 �����ص�JSONArrayתΪһ��itemList
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();

		for (int i = 0; i < events.length(); ++i) {
			JSONObject event = events.getJSONObject(i);
			//������ģʽ,����һ��builder
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
			//ʹ��helper������ȡ address/����/ͼƬurl
			builder.setAddress(getAddress(event));
			builder.setCategories(getCategories(event));
			builder.setImageUrl(getImageUrl(event));
			//builder.build()����һ��new Item����
			/**������ģʽ:1.builder.set()->����builder������
			 * 			2.builder.build() -> ����item���췽��
			 * 			3.item���췽��item() -> ��builder��ֵ����item
			 * 			4.�ô�:item��ֵֻ���ڹ���ʱ������
			*/	
			itemList.add(builder.build());
		}
		return itemList;
	}
}
