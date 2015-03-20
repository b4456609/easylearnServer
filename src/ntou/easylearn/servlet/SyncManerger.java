package ntou.easylearn.servlet;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntou.easylearn.model.DBManerger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class SyncManerger
 */
@WebServlet("/sync")
public class SyncManerger extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private JSONObject syncData;
	private JSONObject userData;
	private JSONArray folderData;
	private String userId;
	private DBManerger db;
	private Timestamp syncTimeStamp;
	private JSONObject responseJson;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SyncManerger() {
		super();

		// create current time stamp
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		syncTimeStamp = new java.sql.Timestamp(now.getTime());

		// initial set success sync
		responseJson = new JSONObject();
		try {
			responseJson.put("sync", "success");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// prepare db
		db = new DBManerger();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		try {
			// get json data from request
			String syncJsonData = request.getParameter("syncData");

			// extract sync data to user and setting
			syncData = new JSONObject(syncJsonData);
			userData = syncData.getJSONObject("user");
			folderData = syncData.getJSONArray("folder");

			// decide server or client has newer data by last_sync_time
			if (isClientNewer())
				syncBaseOnClient();
			else
				syncBaseOnServer();

			response.setContentType("application/json");
			response.getWriter().write(responseJson.toString());
		} catch (JSONException e) {
			exceptionHandler();
			e.printStackTrace();
		}
	}

	// decide server or client has newer data by last_sync_time
	private boolean isClientNewer() throws JSONException {

		// get user's last sync time from server
		JSONObject dbSetting = db.getSetting(userId);
		String dbSyncTime = dbSetting.getString("last_sync_time");
		Timestamp dbSyncTimeStamp = Timestamp.valueOf(dbSyncTime);

		// get user's last sync time from client
		Timestamp clientSyncTime = Timestamp.valueOf(userData.getJSONObject(
				"setting").getString("last_sync_time"));

		// compare who's data are newer
		// true mean clientSyncTime is after dbSyncTimeStamp
		if (dbSyncTimeStamp.compareTo(clientSyncTime) < 0)
			return true;
		else
			return false;
	}

	private void exceptionHandler() {
		responseJson.remove("sync");
		try {
			responseJson.put("sync", "fail");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void syncBaseOnServer() throws JSONException {
		// get setting by userId
		// add setting in result jsonArray
		responseJson.put("setting", db.getSetting(userId));

		// get folder jsonArray by userid
		JSONArray folderArray = db.getFolder(userId);

		for (int i = 0; i < folderArray.length(); i++) {
			// get packId array by folder and userId in folder_has_pack
			folderArray.getJSONObject(i).put(
					"pack",
					db.getPackIDArray(userId, folderArray.getJSONObject(i)
							.getString("id")));
		}
		// put folder in result jsonArray
		responseJson.put("folder", folderArray);

		// get packId jsonArray by userid in folder has pack
		JSONArray packArray = db.getPackIDArray(userId);
		for (int i = 0; i < packArray.length(); i++) {
			String packId = packArray.getJSONObject(i).getString("pack_id");
			// get pack by packId
			JSONObject pack = db.getPack(packId);

			// get pack's version jsonArray by userId , version_pack_id in
			// user_has_version and version
			JSONArray version = db.getPacksVersion(packId, userId);
			for (int j = 0; j < version.length(); j++) {
				String versionId = version.getJSONObject(i).getString("id");

				// get bookmark jsonArray by version and userid in bookmark
				// add bookmark jsonArray to Version
				version.getJSONObject(j).put("bookmark",
						db.getBookmark(userId, versionId));

				// get file jsonArray by version_id
				// add file jsonArray to Version
				version.getJSONObject(j).put("file", db.getFile(versionId));

				// get notes jsonArray by version_id from version_has_note and
				// note
				JSONArray notes = db.getNotes(versionId);
				for (int k = 0; k < notes.length(); k++) {
					// get comment jsonArray by noteid in comment
					String noteId = notes.getJSONObject(k).getString("id");
					notes.getJSONObject(k)
							.put("comment", db.getComment(noteId));
				}

				// put notes in packId
				version.getJSONObject(j).put("note", notes);

				// put version in packId
				pack.put("version", version);
			}
			// put pack in responseJson
			responseJson.put(packId, pack);
		}

	}

	private void syncBaseOnClient() throws JSONException {
		// update setting
		JSONObject setting = userData.getJSONObject("setting");
		db.updateSetting(setting.getBoolean("wifi_sync"),
				setting.getBoolean("mobile_network_sync"),
				syncTimeStamp.toString(), setting.getString("userId"));

		// Update folder
		folderSyncBaseOnClient();

		// Update pack
		packSyncBaseOnClient();

		// * Version
		// * Note
		//
		// * Comment
		//
		// * Bookmark
		// * File

	}

	private void packSyncBaseOnClient() {
		Iterator packIter = syncData.keys();
		
		// add and update folderHasPack in db
		while (packIter.hasNext()) {
			if(packIter.next().toString().equals("user") || packIter.next().toString().equals("folder"))
				continue;

//			// get i th object
//			JSONObject clientPackObject = folderHasPackData.getJSONObject(i);
//			PackModel clientPackModel = new PackModel(clientJsonObject);
//
//			// get dbJsonObject
//			JSONArray data = new JSONArray(db.getPack(clientPackModel.getId()));
//
//			// not found pack data in db, add it in db
//			if (data.length() == 0) {
//				clientPackModel.addToDB();
//			} else {
//				JSONObject dbJsonObject = data.getJSONObject(0);
//				PackModel dbPackModel = new PackModel(dbJsonObject);
//				if (!clientPackModel.isEqual(dbPackModel))
//					clientPackModel.updateToDB();
//			}

		}
	}

	private void folderSyncBaseOnClient() throws JSONException {
		// get folder jsonArray by userid
		JSONArray dbFolder = db.getFolder(userId);

		// delete folder in db
		for (int i = 0; i < dbFolder.length(); i++) {
			String itemId = dbFolder.getJSONObject(i).getString("id");

			int j = 0;
			for (; j < folderData.length(); i++) {
				if (itemId.equals(folderData.getJSONObject(j).getString("id")))
					break;
			}
			if (j == folderData.length()) {
				db.deleteFolder(itemId);
			}
		}

		// update and add folder in db
		for (int i = 0; i < folderData.length(); i++) {
			// get client folder data
			String clientFolderId = folderData.getJSONObject(i).getString("id");
			String clientFoldername = folderData.getJSONObject(i).getString(
					"name");

			// find folder data in db
			int j = 0;
			for (; j < dbFolder.length(); j++) {
				// get db folder data
				String dbFolderId = dbFolder.getJSONObject(i).getString("id");
				String dbFoldername = dbFolder.getJSONObject(i).getString(
						"name");

				// find the same id ,but different name
				if (clientFolderId.equals(dbFolderId)
						&& !clientFoldername.equals(dbFoldername)) {
					// update folder content
					db.updateFolder(clientFolderId, clientFoldername, userId);
					break;
				}

				// find the same id and name
				else if (clientFolderId.equals(dbFolderId)
						&& clientFoldername.equals(dbFoldername))
					break;
			}

			// not found folder in db, add it in db
			if (j == dbFolder.length())
				db.addFolder(clientFolderId, clientFoldername, userId);

			JSONArray dbPackArray = db.getPackIDArray(userId, folderData
					.getJSONObject(i).getString("id"));
			JSONArray packArray = folderData.getJSONObject(i).getJSONArray(
					"pack");
			packInFolderSync(clientFolderId, dbPackArray, packArray);
		}

	}

	private void packInFolderSync(String clientFolderId, JSONArray dbPackArray,
			JSONArray packArray) throws JSONException {
		// check pack in folder

		// delete folderHasPack in db
		for (int i = 0; i < dbPackArray.length(); i++) {
			String dbPackId = dbPackArray.getJSONObject(i).getString("pack_id");

			int j = 0;
			for (j = 0; j < packArray.length(); j++) {
				// get client pack id
				String clientPackId = packArray.getJSONObject(j).getString(
						"pack_id");
				if (clientPackId.equals(dbPackId))
					break;
			}

			// not found folder in client, delete it in db
			if (j == packArray.length())
				db.deleteFolderHasPack(clientFolderId, dbPackId);
		}

		// add folderHasPack in db
		for (int i = 0; i < packArray.length(); i++) {
			String packId = packArray.getJSONObject(i).getString("pack_id");

			// find folder data in db
			int j = 0;
			for (; j < dbPackArray.length(); j++) {
				// get db pack id
				String dbPackId = dbPackArray.getJSONObject(i).getString(
						"pack_id");

				// find the same id and name
				if (packId.equals(dbPackId))
					break;
			}

			// not found folder in db, add it in db
			if (j == dbPackArray.length())
				db.addFolderHasPack(clientFolderId, packId, userId);
		}
	}

	public JSONObject test() {
		userId = "00157016";
		try {
			syncBaseOnServer();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseJson;
	}

}
