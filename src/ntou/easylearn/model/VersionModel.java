package ntou.easylearn.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VersionModel {
	

	private String id;
	private String content;
	private String create_time;
	private String pack_id;
	private boolean is_public;
	private String creator_user_id;


	public VersionModel(String id, String content, String create_time,
			String pack_id, boolean is_public, String creator_user_id) {
		super();
		this.id = id;
		this.content = content;
		this.create_time = create_time;
		this.pack_id = pack_id;
		this.is_public = is_public;
		this.creator_user_id = creator_user_id;
	}

	public VersionModel(JSONObject versionData) {
		super();
		try {
			this.id = versionData.getString("id");
			this.content = versionData.getString("content");
			this.create_time = versionData.getString("create_time");
			this.pack_id = versionData.getString("pack_id");
			this.is_public = versionData.getBoolean("is_public");
			this.creator_user_id = versionData.getString("creator_user_id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public VersionModel(String versionId) {
		try {
			DBManerger db = new DBManerger();
			JSONArray data = new JSONArray(db.getVersion(versionId));
			JSONObject versionData = data.getJSONObject(0);

			this.id = versionData.getString("id");
			this.content = versionData.getString("content");
			this.create_time = versionData.getString("create_time");
			this.pack_id = versionData.getString("pack_id");
			this.is_public = versionData.getBoolean("is_public");
			this.creator_user_id = versionData.getString("creator_user_id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateToDB() {
		DBManerger db = new DBManerger();
		db.updateVersion(id, content, create_time, pack_id, is_public);
	}

	public void addToDB() {
		DBManerger db = new DBManerger();
		db.addVersion(id, content, create_time, pack_id, is_public,creator_user_id);
	}

	public boolean isEqual(VersionModel rhs) {
		if (this.id != rhs.id)
			return false;
		if (this.content != rhs.content)
			return false;
		if (this.create_time != rhs.create_time)
			return false;
		if (this.pack_id != rhs.pack_id)
			return false;
		if (this.is_public != rhs.is_public)
			return false;
		return true;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getPack_id() {
		return pack_id;
	}

	public void setPack_id(String pack_id) {
		this.pack_id = pack_id;
	}

	public boolean isIs_public() {
		return is_public;
	}

	public void setIs_public(boolean is_public) {
		this.is_public = is_public;
	}

	public String getCreator_user_id() {
		return creator_user_id;
	}

	public void setCreator_user_id(String creator_user_id) {
		this.creator_user_id = creator_user_id;
	}

}
