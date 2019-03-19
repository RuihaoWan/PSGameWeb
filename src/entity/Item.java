package entity;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//class item 作为item数据库对应的entity
public class Item {
	private String itemId;//id,primary key
	private String name;//活动名称
	private double rating;//活动评分
	private String address;//活动地址
	private Set<String> categories;//活动分类(set)
	private String imageUrl;//活动图片url
	private String url;//活动链接url
	private double distance;//活动距离
	
	/**Step2 : override hashcode and equals
	 * 因为需要通过id相同来判断两个item是否相同
	 */
	@Override
	public int hashCode(){//为了重载item entity类的equal
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemId == null)?0:itemId.hashCode()); //itemId的hashCode作为其hashcode返回值的一部分,因此以itemId做比较对象
		return result;
	}
	
	@Override
	public boolean equals(Object obj){//重载equals函数,以itemId作为比较对象
		if(this == obj){//如果传入的是本身
			return true;
		}
		if(obj == null){
			return false;
		}
		if(getClass()!=obj.getClass()){//如果输入不是item类
			return false;
		}
		Item other = (Item) obj;
		if(itemId == null){
			if(other.itemId != null){
				return false;
			}
		}else if(!itemId.equals(other.itemId)){ //以itemId作为判断对象
			return false;
		}
		return true;
	}
	/**get函数
	 */
	public String getItemId() {
		return itemId;
	}
	public String getName() {
		return name;
	}
	public double getRating() {
		return rating;
	}
	public String getAddress() {
		return address;
	}
	public Set<String> getCategories() {
		return categories;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public String getUrl() {
		return url;
	}
	public double getDistance() {
		return distance;
	}
	//toJsonObject 负责将此item转为一个jsonobject
	public JSONObject toJSONObject(){
		JSONObject obj = new JSONObject();
		try{
			obj.put("item_id", itemId);
			obj.put("name", name);
			obj.put("rating", rating);
			obj.put("address", address);
			obj.put("categories", new JSONArray(categories)); //categories为一个set
			obj.put("image_url", imageUrl);
			obj.put("url", url);
			obj.put("distance", distance);
		}catch(JSONException e){ //在调用jsonobject.put函数的时候,需要检测jsonexception异常
			e.printStackTrace();
		}
		return obj;
	}
	private Item(ItemBuilder builder){ //建造者模式建造item
		this.itemId = builder.itemId;
		this.name = builder.name;
		this.rating = builder.rating;
		this.address = builder.address;
		this.categories = builder.categories;
		this.imageUrl = builder.imageUrl;
		this.url = builder.url;
		this.distance = builder.distance;

	}
	
	
	public static class ItemBuilder{ //子类itemBuilder
		private String itemId;
		private String name;
		private double rating;
		private String address;
		private Set<String> categories;
		private String imageUrl;
		private String url;
		private double distance;
		
		public void setItemId(String itemId) {
			this.itemId = itemId;
		}
		public void setName(String name) {
			this.name = name;
		}
		public void setRating(double rating) {
			this.rating = rating;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public void setCategories(Set<String> categories) {
			this.categories = categories;
		}
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public void setDistance(double distance) {
			this.distance = distance;
		}
		
		public Item build(){
			return new Item(this);
		}
		
		
	}
}
