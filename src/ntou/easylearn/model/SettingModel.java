package ntou.easylearn.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SettingModel {
	private boolean wifi_sync;
	private boolean mobile_network_sync;
	private String last_sync_time;
	private String user_id;

	public SettingModel(boolean wifi_sync, boolean mobile_network_sync,
			String last_sync_time, String user_id) {
		super();
		this.wifi_sync = wifi_sync;
		this.mobile_network_sync = mobile_network_sync;
		this.last_sync_time = last_sync_time;
		this.user_id = user_id;
	}

	public SettingModel(JSONObject settingData) {
		super();
		try {
			this.wifi_sync = settingData.getBoolean("wifi_sync");
			this.mobile_network_sync = settingData.getBoolean("mobile_network_sync");
			this.last_sync_time = settingData.getString("last_sync_time");
			this.user_id = settingData.getString("user_id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SettingModel(String userId) {
		try {
			DBManerger db = new DBManerger();
			JSONObject settingData = new JSONObject(db.getSetting(userId));

			this.wifi_sync = settingData.getBoolean("wifi_sync");
			this.mobile_network_sync = settingData.getBoolean("mobile_network_sync");
			this.last_sync_time = settingData.getString("last_sync_time");
			this.user_id = settingData.getString("user_id");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isEqual(SettingModel rhs){
		if (this.wifi_sync != rhs.wifi_sync)
			return false;
		if (this.mobile_network_sync != rhs.mobile_network_sync)
			return false;
		if (this.last_sync_time != rhs.last_sync_time)
			return false;

		return true;
	}
	
	public void addToDB(){
		DBManerger db = new DBManerger();
		db.addSetting(user_id);
	}
	
	public void updateToDB(){
		DBManerger db = new DBManerger();
		db.updateSetting(wifi_sync, mobile_network_sync, last_sync_time, user_id);
	}

	public boolean isWifi_sync() {
		return wifi_sync;
	}

	public void setWifi_sync(boolean wifi_sync) {
		this.wifi_sync = wifi_sync;
	}

	public boolean isMobile_network_sync() {
		return mobile_network_sync;
	}

	public void setMobile_network_sync(boolean mobile_network_sync) {
		this.mobile_network_sync = mobile_network_sync;
	}

	public String getLast_sync_time() {
		return last_sync_time;
	}

	public void setLast_sync_time(String last_sync_time) {
		this.last_sync_time = last_sync_time;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
}
