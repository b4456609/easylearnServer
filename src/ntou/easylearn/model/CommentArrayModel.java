package ntou.easylearn.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommentArrayModel {
	private ArrayList<CommentModel> array;

	public CommentArrayModel(String note_id) {
		try {
			DBManerger db = new DBManerger();
			String data = db.getComment(note_id);
			JSONArray json = new JSONArray(data);
			array = new ArrayList<CommentModel>();

			for (int i = 0; i < json.length(); i++) {
				array.add(new CommentModel(json.getJSONObject(i)));
			}

		} catch (JSONException e) {
			System.out.println("[CommentArrayModel]");
			e.printStackTrace();
		}
	}

	public ArrayList<CommentModel> getArray() {
		return array;
	}

	public void setArray(ArrayList<CommentModel> array) {
		this.array = array;
	}

}
