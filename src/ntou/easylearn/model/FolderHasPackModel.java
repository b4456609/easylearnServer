package ntou.easylearn.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FolderHasPackModel {
	private String folder_id;
	private String folder_user_id;
	private String pack_id;
	
	
	public FolderHasPackModel(String folder_id, String folder_user_id,
			String pack_id) {
		super();
		this.folder_id = folder_id;
		this.folder_user_id = folder_user_id;
		this.pack_id = pack_id;
	}

	public FolderHasPackModel(JSONObject folderHasPackData) {
		super();
		try {
			this.folder_id = folderHasPackData.getString("folder_id");
			this.folder_user_id = folderHasPackData.getString("folder_user_id");
			this.pack_id = folderHasPackData.getString("pack_id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addToDB() {
		DBManerger db = new DBManerger();
		db.addFolderHasPack(folder_id, pack_id, folder_user_id);
	}
	
	public void deleteToDB() {
		DBManerger db = new DBManerger();
		db.deleteFolderHasPack(folder_id, pack_id);
	}

	public boolean isEqual(FolderHasPackModel rhs) {
		if (this.folder_id != rhs.folder_id)
			return false;
		if (this.pack_id != rhs.pack_id)
			return false;

		return true;
	}

	public String getFolder_id() {
		return folder_id;
	}

	public void setFolder_id(String folder_id) {
		this.folder_id = folder_id;
	}

	public String getFolder_user_id() {
		return folder_user_id;
	}

	public void setFolder_user_id(String folder_user_id) {
		this.folder_user_id = folder_user_id;
	}

	public String getPack_id() {
		return pack_id;
	}

	public void setPack_id(String pack_id) {
		this.pack_id = pack_id;
	}


}
