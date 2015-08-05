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
	private long syncTimeStamp;
	private JSONObject responseJson;
	private JSONObject syncInfo;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SyncManerger() {
		super();

	}

	private void initial() {

		// initial set success sync
		responseJson = new JSONObject();
		syncInfo = new JSONObject();

		// create current time stamp
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		syncTimeStamp = now.getTime();

		try {
			syncInfo.put("status", "success");
			syncInfo.put("timestamp", syncTimeStamp);
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
		System.out.println("[Sync]start");
		String clientOrigin = request.getHeader("origin");
		response.setHeader("Access-Control-Allow-Origin", clientOrigin);
		response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400");

		initial();

		try {
			// get json data from request
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			String syncJsonData = request.getParameter("sync_data");
			System.out.println("[Sync]data" + syncJsonData);

			// extract sync data to user and setting
			syncData = new JSONObject(syncJsonData);
			userData = syncData.getJSONObject("user");
			userId = userData.getString("id");
			folderData = syncData.getJSONArray("folder");

			if (!isConflict()) {
				// Update pack
				packSyncBaseOnClient();

				// decide server or client has newer data by last_sync_time
				if (isClientNewer()) {
					syncBaseOnClient();
				}
				syncBaseOnServer();

				responseJson.put("sync", syncInfo);

				// update sync time to db
				db.syncTime(new Timestamp(syncTimeStamp).toString(), userId);
			} else {
				syncInfo.remove("status");
				syncInfo.put("status", "conflict");
				responseJson.put("sync", syncInfo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			exceptionHandler();
		} finally {
			response.setContentType("application/json");
			response.getWriter().write(responseJson.toString());
			System.out.println("[Sync]success" + responseJson.toString());
			System.out.println();
		}
	}

	private boolean isConflict() throws JSONException {
		JSONObject setting = userData.getJSONObject("setting");
		JSONObject dbSetting = db.getSetting(userId);

		System.out.println("[isConflict]user version:" +setting.getInt("version"));
		System.out.println("[isConflict]user modified:" + setting.getBoolean("modified"));
		
		// new user no setting in db
		if (dbSetting.length() == 0) {
			db.addUser(userId, userData.getString("name"));
			db.addSetting(userId);
			return false;
		}

		
		System.out.println("[isConflict]db version:" +dbSetting.getInt("version"));

		// old user new device
		if (setting.getInt("version") == 0) {
			System.out.println("[isConflict] (new user)");
			return false;
		}
		
		// conflict happen
		if ((dbSetting.getInt("version") != setting.getInt("version"))
				&& setting.getBoolean("modified")) {
			System.out.println("[isConflict] true");
			return true;
		}
		System.out.println("[isConflict] false");
		return false;
	}

	// decide server or client has newer data by last_sync_time
	private boolean isClientNewer() throws JSONException {

		int settingVersion = userData.getJSONObject("setting")
				.getInt("version");
		int dbSettingVersion = db.getSetting(userId).getInt("version");

		boolean modified = userData.getJSONObject("setting").getBoolean(
				"modified");

		
		// conflict happen
		if ((settingVersion == dbSettingVersion) && modified) {
			System.out.println("[isClientNewer] true");
			return true;
		}
		System.out.println("[isClientNewer] false");
		return false;
	}

	private void exceptionHandler() {
		System.out.println("[Sync]fail");
		syncInfo.remove("status");
		try {
			syncInfo.put("status", "fail");
			responseJson.put("sync", syncInfo);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void syncBaseOnServer() throws JSONException {
		System.out.println("[Sync]syncBaseOnServer");
		// get setting by userId
		// add setting in result jsonArray
		JSONObject setting = db.getSetting(userId);
		setting.put("modified", false);
		responseJson.put("setting", setting);

		// get folder jsonArray by userid
		JSONArray folderArray = db.getFolder(userId);

		for (int i = 0; i < folderArray.length(); i++) {
			// get packId array by folder and userId in folder_has_pack
			folderArray.getJSONObject(i).put(
					"pack",
					db.getPackIDArray(userId, folderArray.getJSONObject(i)
							.getString("id")));
		}

		// for test put all folder in
		for (int folderArrayIter = 0; folderArrayIter < folderArray.length(); folderArrayIter++) {
			String foldername = folderArray.getJSONObject(folderArrayIter)
					.getString("name");
			if (foldername.equals("全部的懶人包")) {
				folderArray.remove(folderArrayIter);

			}
		}

		JSONArray allPackIdArray = db.getAllPackIDArray();
		JSONObject allFolder = new JSONObject();
		allFolder.put("id", "allfolder");
		allFolder.put("name", "全部的懶人包");
		allFolder.put("pack", allPackIdArray);
		folderArray.put(allFolder);


		// put folder in result jsonArray
		responseJson.put("folder", folderArray);

		// get packId jsonArray by user id in folder has pack
		// JSONArray packArray = db.getPackIDArray(userId);
		JSONArray packArray = allPackIdArray;
		for (int i = 0; i < packArray.length(); i++) {
			// String packId = packArray.getJSONObject(i).getString("pack_id");
			String packId = packArray.getString(i);
			// get pack by packId
			JSONObject pack = db.getPack(packId);
			System.out.println(pack);

			// get pack's version jsonArray by userId , version_pack_id in
			// user_has_version and version
			JSONArray version = db.getPacksVersion(packId);
			for (int j = 0; j < version.length(); j++) {
				JSONObject versionItem = version.getJSONObject(j);
				versionItem.put("modified", false);
				String versionId = versionItem.getString("id");
				
				//put empty user_view_count for app
				versionItem.put("user_view_count", 0);

				// get bookmark jsonArray by version and userid in bookmark
				// add bookmark jsonArray to Version
				versionItem.put("bookmark", db.getBookmark(userId, versionId));

				// get file jsonArray by version_id
				// add file jsonArray to Version
				versionItem.put("file", db.getFile(versionId));

				// get notes jsonArray by version_id from version_has_note and
				// note
				JSONArray notes = db.getNotes(versionId);
				for (int k = 0; k < notes.length(); k++) {
					// get comment jsonArray by noteid in comment
					String noteId = notes.getJSONObject(k).getString("id");
					notes.getJSONObject(k).put("comment",
							db.getComments(noteId));
				}

				// put notes in version
				versionItem.put("note", notes);

			}
			// put version in pack
			pack.put("version", version);
			// remove pack id
			pack.remove("id");
			// put pack in responseJson
			responseJson.put(packId, pack);
		}

	}

	private void syncBaseOnClient() throws JSONException {
		System.out.println("[Sync]syncBaseOnClient");

		// get db version and add one
		int version = userData.getJSONObject("setting").getInt("version");
		JSONObject DBsetting = db.getSetting(userId);
		if (DBsetting.length() != 0) {
			version = DBsetting.getInt("version");
			version++;
		}
		// update setting
		JSONObject setting = userData.getJSONObject("setting");
		db.updateSetting(setting.getBoolean("wifi_sync"), setting
				.getBoolean("mobile_network_sync"),
				new Timestamp(syncTimeStamp).toString(), version, userId);


		// remove all bookmark convenient for sync
		db.deleteBookmark(userId);


		// Update folder
		folderSyncBaseOnClient();

	}

	private void packSyncBaseOnClient() throws JSONException {
		System.out.println("[packSyncBaseOnClient()]");
		Iterator packIter = syncData.keys();

		// add and update folderHasPack in db
		while (packIter.hasNext()) {
			String packId = packIter.next().toString();

			// skip other non pack data
			if (packId.equals("user") || packId.equals("folder"))
				continue;

			// get json object
			JSONObject pack = syncData.getJSONObject(packId);
			

			// update pack or add pack
			// check is already in db?
			if (db.getPack(packId).length() == 0) {
				// add pack
				db.addPack(packId, pack.getString("name"),
						pack.getString("description"),
						pack.getLong("create_time"), pack.getString("tags"),
						pack.getBoolean("is_public"),
						pack.getString("creator_user_id"),
						pack.getString("cover_filename"));

			}
			// yes update it
			else {
				db.updatePack(packId, pack.getString("name"),
						pack.getString("description"),
						pack.getLong("create_time"), pack.getString("tags"),
						pack.getBoolean("is_public"));

			}
			

			// version
			// get pack's version
			JSONArray versionArray = pack.getJSONArray("version");
			versionSyncBaseOnclient(packId, versionArray);
		}
		
		//remove private backup version and remove private 
		db.handleUserVersion(userId);
	}

	private void updateVersion(String id, String content, long create_time,
			String packId, boolean is_public, int version, String versionId)
			throws JSONException {
		System.out.println("[updateVersionStart]" + content);
		String dbContent = db.getVersion(versionId).getString("content");
		System.out.println("[updateVersionStart]" + dbContent);
		StringBuffer dbContentBuffer = new StringBuffer(dbContent);
		StringBuffer contentBuffer = new StringBuffer(content);
		int index = 0;
		int clientIndex = contentBuffer.indexOf("<span class=\"note", index);
		int dbIndex = dbContentBuffer.indexOf("<span class=\"note", index);
		
		if(clientIndex == -1 && dbIndex == -1) return;
		else if(clientIndex >= 0 && dbIndex == -1){
			//update use client
			db.updateVersion(id, content);
			return;
		}
		else if(clientIndex == -1 && dbIndex >= 0){
			//return do nothing
			//db has new
			return;
		}
		
		while(true){
			clientIndex = contentBuffer.indexOf("<span class=\"note", index);
			dbIndex = dbContentBuffer.indexOf("<span class=\"note", index);
			System.out.println("clientIndex"+clientIndex);
			System.out.println("dbIndex"+dbIndex);
			if(clientIndex == -1 && dbIndex == -1){
				//result
				db.updateVersion(id, contentBuffer.toString());
				return;
			}
			else if (clientIndex != -1 && dbIndex == -1){
				versionInsert(index, clientIndex, contentBuffer, dbContentBuffer);				
			}
			else if (clientIndex == -1 && dbIndex != -1){
				versionInsert(index, dbIndex, dbContentBuffer, contentBuffer);
			}
			else if(clientIndex == dbIndex){
				index = clientIndex + 1;
			}
			else if(clientIndex < dbIndex){
				versionInsert(index, clientIndex, contentBuffer, dbContentBuffer);
			}
			else if(clientIndex > dbIndex ){
				versionInsert(index, dbIndex, dbContentBuffer, contentBuffer);
			}
			else{
				System.out.println("error");
			}
		}
		
	}
	
	
	private int versionInsert(int index,int clientIndex, StringBuffer contentBuffer, StringBuffer dbContentBuffer){
		System.out.println("[versionInsert]clientIndex"+clientIndex);
		System.out.println("[versionInsert]index"+index);
		System.out.println("[versionInsert]contentBuffer"+contentBuffer);
		System.out.println("[versionInsert]dbContentBuffer"+dbContentBuffer);
		index = clientIndex;
		int last = contentBuffer.indexOf(">", index);
		String newStr = contentBuffer.substring(index, last + 1);
		dbContentBuffer.insert(index, newStr);
		index = last;
		
		index = contentBuffer.indexOf("</span>", index);
		// deal with last index
		dbContentBuffer.insert(index, "</span>");
		index = index + 7;
		return index;
	}

	private void versionSyncBaseOnclient(String packId, JSONArray versionArray)
			throws JSONException {
		System.out.println("[versionSyncBaseOnclient()]");
		for (int i = 0; i < versionArray.length(); i++) {
			JSONObject version = versionArray.getJSONObject(i);

			// get version id
			String versionId = version.getString("id");

			//update version count
			db.updateVersionCount(versionId, version.getInt("view_count") + version.getInt("user_view_count"));
			
			// update version or add version
			// check is already in db?
			if (db.getVersion(versionId).length() == 0) {
				// add pack
				db.addVersion(version.getString("id"),
						version.getString("content"),
						version.getLong("create_time"), packId,
						version.getBoolean("is_public"),
						version.getString("creator_user_id"),
						version.getInt("version"),
						version.getString("private_id"));
			} else {
				// yes update it
				updateVersion(version.getString("id"),
						version.getString("content"),
						version.getLong("create_time"), packId,
						version.getBoolean("is_public"),
						version.getInt("version"), versionId);
			}

			// bookmark
			// get bookmark array
			JSONArray bookmarkArray = version.getJSONArray("bookmark");
			// add all to db
			for (int j = 0; j < bookmarkArray.length(); j++) {
				JSONObject bookmark = bookmarkArray.getJSONObject(j);
				db.addBookmark(bookmark.getString("id"),
						bookmark.getString("name"),
						bookmark.getInt("position"), userId, versionId, packId);
			}

			// note
			// get version's note array
			JSONArray noteArray = version.getJSONArray("note");
			noteSyncBaseOnclient(versionId, packId, noteArray);

			// file
			// get file array
			JSONArray fileArray = version.getJSONArray("file");
			fileSyncBaseOnclient(versionId, packId, fileArray);

		}
	}

	private void fileSyncBaseOnclient(String versionId, String packId,
			JSONArray fileArray) throws JSONException {
		// get db file array
		JSONArray dbFileArray = db.getFile(versionId);


		// delete file
		for (int j = 0; j < dbFileArray.length(); j++) {
			// get file name
			String dbname = dbFileArray.getString(j);

			int i;
			for (i = 0; i < fileArray.length(); i++) {
				// get file name
				String name = fileArray.getString(i);

				if (dbname.equals(name))
					break;
			}

			if (i == fileArray.length())
				db.deleteFile(dbname, versionId);
		}

		// add file
		for (int i = 0; i < fileArray.length(); i++) {
			// get file name
			String name = fileArray.getString(i);

			if (db.getFile(versionId, name).length() == 0) {
				db.addFile(name, versionId, packId);
			}
		}
	}

	private void noteSyncBaseOnclient(String versionId, String packId,
			JSONArray noteArray) throws JSONException {
		for (int i = 0; i < noteArray.length(); i++) {

			JSONObject note = noteArray.getJSONObject(i);

			// get note id
			String noteId = note.getString("id");

			// update note or add note
			// check is already in db?
			if (db.getNote(noteId).length() == 0) {
				// add note
				db.addNote(note.getString("id"), note.getString("content"),
						note.getLong("create_time"), userId);
			}
			
			if(db.getVersionHasNote(versionId, noteId).length() == 0){
				// add version has note table
				db.addVersionHasNote(versionId, packId, noteId);
			}

			// get comment
			JSONArray commentArray = note.getJSONArray("comment");
			commentSyncBaseOnclient(commentArray, noteId);

		}

	}

	private void commentSyncBaseOnclient(JSONArray commentArray, String noteId)
			throws JSONException {
		for (int i = 0; i < commentArray.length(); i++) {
			JSONObject comment = commentArray.getJSONObject(i);

			// get comment id
			String commentId = comment.getString("id");
			if (db.getComment(commentId).length() == 0) {
				// add comment
				db.addComment(commentId, comment.getString("content"),
						comment.getLong("create_time"), noteId, userId);
			}
		}
	}

	private void folderSyncBaseOnClient() throws JSONException {
		db.deleteUserFolder(userId);

		for (int i = 0; i < folderData.length(); i++) {
			// get client folder data
			String clientFolderId = folderData.getJSONObject(i).getString("id");
			String clientFoldername = folderData.getJSONObject(i).getString(
					"name");

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

		// add folderHasPack in db
		for (int i = 0; i < packArray.length(); i++) {
			String packId = packArray.getString(i);

			// find folder data in db
			int j = 0;
			for (; j < dbPackArray.length(); j++) {
				// get db pack id
				String dbPackId = dbPackArray.getString(j);

				// find the same id and name
				if (packId.equals(dbPackId))
					break;
			}

			// not found folder in db, add it in db
			if (j == dbPackArray.length())
				db.addFolderHasPack(clientFolderId, packId, userId);
		}
	}
}
