package ntou.easylearn.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NoteModel {
	private String id;
	private int color;
	private String content;
	private String create_time;
	private String user_id;	
	

	public NoteModel(String id, int color, String content,
			String create_time, String user_id) {
		super();
		this.id = id;
		this.color = color;
		this.content = content;
		this.create_time = create_time;
		this.user_id = user_id;
	}
	
	public NoteModel(JSONObject NoteData) {
		super();
		try {
			this.id = NoteData.getString("id");
			this.color = NoteData.getInt("color");
			this.content = NoteData.getString("content");
			this.create_time = NoteData.getString("create_time");
			this.user_id = NoteData.getString("user_id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public NoteModel(String noteId) {
		try {
			DBManerger db = new DBManerger();
			JSONArray data = new JSONArray(db.getNote(noteId));
			JSONObject NoteData = data.getJSONObject(0);

			this.id = NoteData.getString("id");
			this.color = NoteData.getInt("color");
			this.content = NoteData.getString("content");
			this.create_time = NoteData.getString("create_time");
			this.user_id = NoteData.getString("user_id");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addToDB(){
		DBManerger db = new DBManerger();
		db.addNote(id, color, content, create_time, user_id);
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
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
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
}
