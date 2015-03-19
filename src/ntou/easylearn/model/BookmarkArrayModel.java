package ntou.easylearn.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BookmarkArrayModel {
	private ArrayList<BookmarkModel> array;

	public BookmarkArrayModel(String userId) {
		try {
			DBManerger db = new DBManerger();
			String data = db.getBookmark(userId);
			JSONArray json = new JSONArray(data);

			array = new ArrayList<BookmarkModel>();

			for (int i = 0; i < json.length(); i++) {
				array.add(new BookmarkModel(json.getJSONObject(i)));
			}

		} catch (JSONException e) {
			System.out.println("[BookmarkArrayModel]");
			e.printStackTrace();
		}
	}

	public ArrayList<BookmarkModel> getArray() {
		return array;
	}

	public void setArray(ArrayList<BookmarkModel> array) {
		this.array = array;
	}

}
