package ntou.easylearn.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FolderModel {
	private String id;
	private String name;
	private String user_id;
	
	
	public FolderModel(String id, String name, String user_id) {
		super();
		this.id = id;
		this.name = name;
		this.user_id = user_id;
	}
	
	public FolderModel(JSONObject folderData) {
		super();
		try {
			this.id = folderData.getString("id");
			this.name = folderData.getString("name");
			this.user_id = folderData.getString("user_id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateToDB() {
		DBManerger db = new DBManerger();
		db.updateFolder(id, name, user_id);
	}

	public void addToDB() {
		DBManerger db = new DBManerger();
		db.addFolder(id, name, user_id);
	}

	public boolean isEqual(FolderModel rhs) {
		if (this.id != rhs.id)
			return false;
		if (this.name != rhs.name)
			return false;
		if (this.user_id != rhs.user_id)
			return false;

		return true;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
}
