package ntou.easylearn.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BookmarkModel{
	private String id;
	private String name;
	private int position;
	private String user_id;
	private String version_id;
	private String version_pack_id;
	
	public BookmarkModel(String id, String name, int position, String user_id,
			String version_id, String version_pack_id) {
		super();
		this.id = id;
		this.name = name;
		this.position = position;
		this.user_id = user_id;
		this.version_id = version_id;
		this.version_pack_id = version_pack_id;
	}
	
	public BookmarkModel(JSONObject bookmarkData) {
		super();
		try {
			this.id = bookmarkData.getString("id");
			this.name = bookmarkData.getString("name");
			this.position = bookmarkData.getInt("position");
			this.user_id = bookmarkData.getString("user_id");
			this.version_id = bookmarkData.getString("version_id");
			this.version_pack_id = bookmarkData.getString("version_pack_id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void updateToDB() {
		DBManerger db = new DBManerger();
		db.updateBookmark(id, name, position, user_id, version_id, version_pack_id);
	}
	
	public void deleteToDB() {
		DBManerger db = new DBManerger();
		db.deleteBookmark(id);
	}

	public void addToDB() {
		DBManerger db = new DBManerger();
		db.addBookmark(id, name, position, user_id, version_id, version_pack_id);
	}

	public boolean isEqual(BookmarkModel rhs) {
		if (this.id != rhs.id)
			return false;
		if (this.name != rhs.name)
			return false;
		if (this.position != rhs.position)
			return false;
		if (this.user_id != rhs.user_id)
			return false;
		if (this.version_id != rhs.version_id)
			return false;
		if (this.version_pack_id != rhs.version_pack_id)
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

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
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
