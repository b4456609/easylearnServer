package ntou.easylearn.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserHasVersionArrayModel {
	private ArrayList<UserHasVersionModel> array;
	
	public UserHasVersionArrayModel(String userId){
		try {
			DBManerger db = new DBManerger();
			String data = db.getUserHasVersion(userId);
			JSONArray json = new JSONArray(data);

			array =  new ArrayList<UserHasVersionModel>();
			
			for(int i =0; i < json.length(); i++){
				array.add(new UserHasVersionModel(json.getJSONObject(i)));
			}
			
			
		} catch (JSONException e) {
			System.out.println("[UserHasVersionArrayModel]");
			e.printStackTrace();
		}		
	}

	public ArrayList<UserHasVersionModel> getArray() {
		return array;
	}

	public void setArray(ArrayList<UserHasVersionModel> array) {
		this.array = array;
	}


}
