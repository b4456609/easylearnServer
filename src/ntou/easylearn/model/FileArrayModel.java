package ntou.easylearn.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

public class FileArrayModel {
private ArrayList<FileModel> array;
	
	public FileArrayModel(String verionId){
		try {
			DBManerger db = new DBManerger();
			String data = db.getFile(verionId);
			JSONArray json = new JSONArray(data);

			array =  new ArrayList<FileModel>();
			
			for(int i =0; i < json.length(); i++){
				array.add(new FileModel(json.getJSONObject(i)));
			}
			
			
		} catch (JSONException e) {
			System.out.println("[FolderArrayModel]");
			e.printStackTrace();
		}		
	}

	public ArrayList<FileModel> getArray() {
		return array;
	}

	public void setArray(ArrayList<FileModel> array) {
		this.array = array;
	}
}
