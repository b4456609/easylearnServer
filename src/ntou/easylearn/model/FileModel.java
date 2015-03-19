package ntou.easylearn.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FileModel {
	private String filename;
	private String version_id;
	private String version_pack_id;
	
	public FileModel(String filename, String version_id,
			String version_pack_id) {
		super();
		this.filename = filename;
		this.version_id = version_id;
		this.version_pack_id = version_pack_id;
	}
	
	public FileModel(JSONObject fileData) {
		super();
		try {
			this.filename = fileData.getString("filename");
			this.version_id = fileData.getString("version_id");
			this.version_pack_id = fileData.getString("version_pack_id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addToDB() {
		DBManerger db = new DBManerger();
		db.addFile(filename, version_id, version_pack_id);
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
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
