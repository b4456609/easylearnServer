package ntou.easylearn.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FolderHasPackArrayModel {
	private ArrayList<FolderHasPackModel> array;

	public FolderHasPackArrayModel(String user_id) {
		try {
			DBManerger db = new DBManerger();
			String data = db.getFolderHasPack(user_id);
			JSONArray json = new JSONArray(data);

			array = new ArrayList<FolderHasPackModel>();

			for (int i = 0; i < json.length(); i++) {
				array.add(new FolderHasPackModel(json.getJSONObject(i)));
			}

		} catch (JSONException e) {
			System.out.println("[FolderHasPackArrayModel]");
			e.printStackTrace();
		}
	}

	public ArrayList<FolderHasPackModel> getArray() {
		return array;
	}

	public void setArray(ArrayList<FolderHasPackModel> array) {
		this.array = array;
	}

}
