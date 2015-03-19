package ntou.easylearn.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FolderArrayModel {
	private ArrayList<FolderModel> array;
	
	public FolderArrayModel(String userId){
		try {
			DBManerger db = new DBManerger();
			String data = db.getFolder(userId);
			JSONArray json = new JSONArray(data);

			array =  new ArrayList<FolderModel>();
			
			for(int i =0; i < json.length(); i++){
				array.add(new FolderModel(json.getJSONObject(i)));
			}
			
			
		} catch (JSONException e) {
			System.out.println("[FolderArrayModel]");
			e.printStackTrace();
		}		
	}

	public ArrayList<FolderModel> getArray() {
		return array;
	}

	public void setArray(ArrayList<FolderModel> array) {
		this.array = array;
	}


}
