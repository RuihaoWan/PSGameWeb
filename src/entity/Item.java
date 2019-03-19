package entity;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//class item ��Ϊitem���ݿ��Ӧ��entity
public class Item {
	private String itemId;//id,primary key
	private String name;//�����
	private double rating;//�����
	private String address;//���ַ
	private Set<String> categories;//�����(set)
	private String imageUrl;//�ͼƬurl
	private String url;//�����url
	private double distance;//�����
	
	/**Step2 : override hashcode and equals
	 * ��Ϊ��Ҫͨ��id��ͬ���ж�����item�Ƿ���ͬ
	 */
	@Override
	public int hashCode(){//Ϊ������item entity���equal
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemId == null)?0:itemId.hashCode()); //itemId��hashCode��Ϊ��hashcode����ֵ��һ����,�����itemId���Ƚ϶���
		return result;
	}
	
	@Override
	public boolean equals(Object obj){//����equals����,��itemId��Ϊ�Ƚ϶���
		if(this == obj){//���������Ǳ���
			return true;
		}
		if(obj == null){
			return false;
		}
		if(getClass()!=obj.getClass()){//������벻��item��
			return false;
		}
		Item other = (Item) obj;
		if(itemId == null){
			if(other.itemId != null){
				return false;
			}
		}else if(!itemId.equals(other.itemId)){ //��itemId��Ϊ�ж϶���
			return false;
		}
		return true;
	}
	/**get����
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
	//toJsonObject ���𽫴�itemתΪһ��jsonobject
	public JSONObject toJSONObject(){
		JSONObject obj = new JSONObject();
		try{
			obj.put("item_id", itemId);
			obj.put("name", name);
			obj.put("rating", rating);
			obj.put("address", address);
			obj.put("categories", new JSONArray(categories)); //categoriesΪһ��set
			obj.put("image_url", imageUrl);
			obj.put("url", url);
			obj.put("distance", distance);
		}catch(JSONException e){ //�ڵ���jsonobject.put������ʱ��,��Ҫ���jsonexception�쳣
			e.printStackTrace();
		}
		return obj;
	}
	private Item(ItemBuilder builder){ //������ģʽ����item
		this.itemId = builder.itemId;
		this.name = builder.name;
		this.rating = builder.rating;
		this.address = builder.address;
		this.categories = builder.categories;
		this.imageUrl = builder.imageUrl;
		this.url = builder.url;
		this.distance = builder.distance;

	}
	
	
	public static class ItemBuilder{ //����itemBuilder
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
