package ntou.easylearn.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VersionHasNoteModel {
	private String version_id;
	private String version_pack_id;
	private String note_id;
	private int position;
	private int position_length;
	
	
	public VersionHasNoteModel(JSONObject versionHasNoteData) {
		super();
		try {
			this.version_id = versionHasNoteData.getString("version_id");
			this.version_pack_id = versionHasNoteData.getString("version_pack_id");
			this.note_id = versionHasNoteData.getString("note_id");
			this.position = versionHasNoteData.getInt("position");
			this.position_length = versionHasNoteData.getInt("position_length");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public VersionHasNoteModel(String versionId) {
		try {
			DBManerger db = new DBManerger();
			JSONArray data = new JSONArray(db.getVersionHasNote(versionId, versionId));
			JSONObject versionHasNoteData = data.getJSONObject(0);

			this.version_id = versionHasNoteData.getString("id");
			this.version_pack_id = versionHasNoteData.getString("version_pack_id");
			this.note_id = versionHasNoteData.getString("note_id");
			this.position = versionHasNoteData.getInt("position");
			this.position_length = versionHasNoteData.getInt("position_length");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addToDB() {
		DBManerger db = new DBManerger();
		db.addVersionHasNote(version_id, version_pack_id, note_id, position, position_length);
	}

	public boolean isEqual(VersionHasNoteModel rhs) {
		if (this.version_id != rhs.version_id)
			return false;
		if (this.version_pack_id != rhs.version_pack_id)
			return false;
		if (this.note_id != rhs.note_id)
			return false;
		if (this.position != rhs.position)
			return false;
		if (this.position_length != rhs.position_length)
			return false;

		return true;
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

	public String getNote_id() {
		return note_id;
	}

	public void setNote_id(String note_id) {
		this.note_id = note_id;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getPosition_length() {
		return position_length;
	}

	public void setPosition_length(int position_length) {
		this.position_length = position_length;
	}

	

}
