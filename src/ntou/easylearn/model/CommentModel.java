package ntou.easylearn.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommentModel {
	
	private String id;
	private String content;
	private String create_time;
	private String note_id;
	private String user_id;
	
	public CommentModel(String id, String content, String create_time,
			String note_id, String user_id) {
		super();
		this.id = id;
		this.content = content;
		this.create_time = create_time;
		this.note_id = note_id;
		this.user_id = user_id;
	}
	
	public CommentModel(JSONObject commentData) {
		super();
		try {
			this.id = commentData.getString("id");
			this.content = commentData.getString("content");
			this.create_time = commentData.getString("create_time");
			this.note_id = commentData.getString("note_id");
			this.user_id = commentData.getString("user_id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	public void addToDB(){
		DBManerger db = new DBManerger();
		db.addComment(id, content, create_time, note_id, user_id);
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

	public String getNote_id() {
		return note_id;
	}

	public void setNote_id(String note_id) {
		this.note_id = note_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	
	
}
