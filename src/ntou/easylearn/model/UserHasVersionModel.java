package ntou.easylearn.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserHasVersionModel {
	private String user_id;
	private String version_id;
	private String version_pack_id;
	
	
	public UserHasVersionModel(String user_id, String version_id,
			String version_pack_id) {
		super();
		this.user_id = user_id;
		this.version_id = version_id;
		this.version_pack_id = version_pack_id;
	}

	public UserHasVersionModel(JSONObject userHasVersion) {
		super();
		try {
			this.user_id = userHasVersion.getString("user_id");
			this.version_id = userHasVersion.getString("version_id");
			this.version_pack_id = userHasVersion.getString("version_pack_id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addToDB() {
		DBManerger db = new DBManerger();
		db.addUserHasVersion(user_id, version_id, version_pack_id);
	}
	
	public void deleteToDB() {
		DBManerger db = new DBManerger();
		db.deleteUserHasVersion(user_id, version_id);
	}

	public boolean isEqual(UserHasVersionModel rhs) {
		if (this.user_id != rhs.user_id)
			return false;
		if (this.version_pack_id != rhs.version_pack_id)
			return false;

		return true;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getVersion_id() {
		return version_id;
	}

	public void setVersion_id(String version_id) {
		this.version_id = version_id;
	}

	public String getVersion_pack_id() {
		return version_pack_id;
	}

	public void setVersion_pack_id(String version_pack_id) {
		this.version_pack_id = version_pack_id;
	}


}
