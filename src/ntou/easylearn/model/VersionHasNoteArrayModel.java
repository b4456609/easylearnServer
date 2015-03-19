package ntou.easylearn.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VersionHasNoteArrayModel {
	private ArrayList<VersionHasNoteModel> array;
	
	
	public VersionHasNoteArrayModel(String versionId) {
		try {
			DBManerger db = new DBManerger();
			String data = db.getVersionHasNote(versionId);
			JSONArray json = new JSONArray(data);

			array =  new ArrayList<VersionHasNoteModel>();
			
			for(int i =0; i < json.length(); i++){
				array.add(new VersionHasNoteModel(json.getJSONObject(i)));
			}
			
			
		} catch (JSONException e) {
			System.out.println("[VersionHasNoteArrayModel]");
			e.printStackTrace();
		}	
	}


	public ArrayList<VersionHasNoteModel> getArray() {
		return array;
	}


	public void setArray(ArrayList<VersionHasNoteModel> array) {
		this.array = array;
	}

	

}
