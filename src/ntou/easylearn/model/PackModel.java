package ntou.easylearn.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PackModel {
	private String id;
	private String name;
	private String description;
	private String create_time;
	private String tags;
	private String creator_user_id;
	private boolean is_public;


	public PackModel(String id, String name, String description,
			String create_time, String tags, boolean is_public, String creator_user_id) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.create_time = create_time;
		this.tags = tags;
		this.is_public = is_public;
		this.creator_user_id = creator_user_id;
	}

	public PackModel(JSONObject packData) {
		super();
		try {
			this.id = packData.getString("id");
			this.name = packData.getString("name");
			this.description = packData.getString("description");
			this.create_time = packData.getString("create_time");
			this.tags = packData.getString("tags");
			this.is_public = packData.getBoolean("is_public");
			this.creator_user_id = packData.getString("creator_user_id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public PackModel(String PackId) {
		try {
			DBManerger db = new DBManerger();

			JSONArray data = new JSONArray(db.getPack(PackId));
			JSONObject packData = data.getJSONObject(0);

			this.id = packData.getString("id");
			this.name = packData.getString("name");
			this.description = packData.getString("description");
			this.create_time = packData.getString("create_time");
			this.tags = packData.getString("tags");
			this.is_public = packData.getBoolean("is_public");
			this.creator_user_id = packData.getString("creator_user_id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateToDB() {
		DBManerger db = new DBManerger();
		db.updatePack(id, name, description, create_time, tags, is_public);
	}

	public void addToDB() {
		DBManerger db = new DBManerger();
		db.addPack(id,name,description,create_time,tags,is_public,creator_user_id);
	}

	public boolean isEqual(PackModel rhs) {
		if (this.id != rhs.id)
			return false;
		if (this.name != rhs.name)
			return false;
		if (this.description != rhs.description)
			return false;
		if (this.create_time != rhs.create_time)
			return false;
		if (this.tags != rhs.tags)
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getCreator_user_id() {
		return creator_user_id;
	}

	public void setCreator_user_id(String creator_user_id) {
		this.creator_user_id = creator_user_id;
	}

	public boolean isIs_public() {
		return is_public;
	}

	public void setIs_public(boolean is_public) {
		this.is_public = is_public;
	}
}
