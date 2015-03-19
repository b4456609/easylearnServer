package ntou.easylearn.servlet;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntou.easylearn.model.BookmarkArrayModel;
import ntou.easylearn.model.BookmarkModel;
import ntou.easylearn.model.CommentArrayModel;
import ntou.easylearn.model.DBManerger;
import ntou.easylearn.model.FileArrayModel;
import ntou.easylearn.model.FolderArrayModel;
import ntou.easylearn.model.FolderHasPackArrayModel;
import ntou.easylearn.model.FolderHasPackModel;
import ntou.easylearn.model.FolderModel;
import ntou.easylearn.model.NoteModel;
import ntou.easylearn.model.PackModel;
import ntou.easylearn.model.SettingModel;
import ntou.easylearn.model.UserHasVersionArrayModel;
import ntou.easylearn.model.UserHasVersionModel;
import ntou.easylearn.model.VersionHasNoteArrayModel;
import ntou.easylearn.model.VersionHasNoteModel;
import ntou.easylearn.model.VersionModel;

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
	private JSONObject settingData;
	private JSONArray folderData;
	private JSONArray folderHasPackData;
	private JSONArray packData;
	private JSONArray versionHasNoteData;
	private JSONArray noteData;
	private JSONArray commentData;
	private JSONArray fileData;
	private JSONArray versionData;
	private JSONArray userHasVersionData;
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

			// extract json data to its json object
			readSyncJsonData(syncJsonData);

			// decide server or client has newer data by last_sync_time
			if (isClientNewer())
				syncBaseOnClient();
			else
				syncBaseOnServer();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			exceptionHandler();
		}

		response.setContentType("application/json");
		response.getWriter().write(responseJson.toString());
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
		
		//get setting by userid
		SettingModel dbSetting = new SettingModel(userId);
		responseJson.put("setting", new JSONObject(dbSetting));
		
		//get folder array
		FolderArrayModel folderArray = new FolderArrayModel(userId);
		JSONArray folderArrayJson = new JSONArray(folderArray.getArray());

		//get user's folder and pack pair
		FolderHasPackArrayModel folderHasPackArray = new FolderHasPackArrayModel(userId);
		
		//get folder's pack
		for(int i=0; i < folderArrayJson.length(); i++){
			JSONObject item= folderArrayJson.getJSONObject(i);
			JSONArray packLst = new JSONArray();
			for(FolderHasPackModel FolderHasPackItem : folderHasPackArray.getArray()){
				if(item.getString("id").equals(FolderHasPackItem.getFolder_id()))
					packLst.put(FolderHasPackItem.getPack_id());					
			}
			item.append("pack", packLst);
		}
		
		//put folder json data in result
		responseJson.put("folder", folderArrayJson);

		UserHasVersionArrayModel userHasVersionArrayModel = new UserHasVersionArrayModel(userId);

		for(FolderHasPackModel item : folderHasPackArray.getArray()){
			PackModel packModel = new PackModel(item.getPack_id());
			JSONObject packJson = new JSONObject(packModel);
			
			JSONArray versionArray = new JSONArray();
			//find user has which version
			for(UserHasVersionModel versionItem: userHasVersionArrayModel.getArray()){
				if(versionItem.getVersion_pack_id().equals(item.getPack_id())){
					VersionModel versionModel = new VersionModel(versionItem.getVersion_id());
					JSONObject versionJsonItem = new JSONObject(versionModel);					

					JSONArray noteArray = notesInVersion(versionItem.getVersion_id());
					JSONArray bookMarkArray = bookmarksInVersion(versionItem.getVersion_id());
					JSONArray fileArray = fileInVersion(versionItem.getVersion_id());
					
					versionJsonItem.put("note", noteArray);
					versionJsonItem.put("bookmark", bookMarkArray);
					versionJsonItem.put("file", fileArray);
					
					versionArray.put(versionJsonItem);
				}
			}
			
			packJson.put("version", versionArray);			
			responseJson.put(item.getPack_id(),packJson);
		}
		
		

//		JSONObject dbSetting = new JSONObject(db.getSetting(userId));
//		JSONArray dbFolder = new JSONArray(db.getFolder(userId));
//		JSONArray dbFolderHasPack = new JSONArray(db.getFolderHasPack(userId));
//
//		JSONArray dbPack = new JSONArray();
//		for (int i = 0; i < dbFolderHasPack.length(); i++) {
//			String data = db.getPack(dbFolderHasPack.getJSONObject(i).getString("pack_id"));
//			JSONArray dataArray = new JSONArray(data);
//			dbPack.put(dataArray.getJSONObject(0));
//		}
//		JSONArray dbUserHasVerion = new JSONArray(db.getUserHasVersion(userId));
//
//		JSONArray dbVersion = new JSONArray();
//		for (int i = 0; i < dbUserHasVerion.length(); i++) {
//			String data = db.getVersion(dbUserHasVerion.getJSONObject(i).getString("version_id"));
//			JSONArray dataArray = new JSONArray(data);
//			dbVersion.put(dataArray.getJSONObject(0));
//		}
//		
//		JSONArray dbFile = new JSONArray();
//		for (int i = 0; i < dbVersion.length(); i++) {
//			String data = db.getFile(dbVersion.getJSONObject(i).getString("id"));
//			JSONArray dataArray = new JSONArray(data);
//			dbFile.put(dataArray.getJSONObject(0));
//		}
//
//		JSONArray dbVerionHasNote = new JSONArray();
//		for (int i = 0; i < dbUserHasVerion.length(); i++) {
//			String data = db.getVersionHasNote(dbUserHasVerion.getJSONObject(i).getString("version_id"));
//			JSONArray dataArray = new JSONArray(data);
//			dbVerionHasNote.put(dataArray.getJSONObject(0));
//		}
//
//		JSONArray dbNote = new JSONArray();
//		for (int i = 0; i < dbVerionHasNote.length(); i++) {
//			String data = db.getNote(dbVerionHasNote.getJSONObject(i).getString("note_id"));
//			JSONArray dataArray = new JSONArray(data);
//			dbNote.put(dataArray.getJSONObject(0));
//		}
//
//		JSONArray dbComment = new JSONArray();
//		for (int i = 0; i < dbNote.length(); i++) {
//			String data = db.getComment(dbNote.getJSONObject(i).getString("id"));
//			JSONArray dataArray = new JSONArray(data);
//			dbComment.put(dataArray.getJSONObject(0));
//		}
//		
//		responseJson.put("setting", dbSetting);
//		responseJson.put("folder", dbFolder);
//		responseJson.put("folder_has_pack", dbFolderHasPack);
//		responseJson.put("pack", dbPack);
//		responseJson.put("user_has_version", dbUserHasVerion);
//		responseJson.put("version", dbVersion);
//		responseJson.put("file", dbFile);
//		responseJson.put("version_has_note", dbVerionHasNote);
//		responseJson.put("note", dbNote);
//		responseJson.put("comment", dbComment);
	}

	private JSONArray fileInVersion(String version_id) throws JSONException {
		FileArrayModel fileArrayModel = new FileArrayModel(version_id);
		JSONArray result = new JSONArray(fileArrayModel.getArray());
		
		return result;
	}

	private JSONArray bookmarksInVersion(String version_id) {
		BookmarkArrayModel bookmarkArrayModel = new BookmarkArrayModel(userId);
		JSONArray result = new JSONArray();
		for (BookmarkModel item : bookmarkArrayModel.getArray()) {
			if (item.getVersion_id().equals(version_id)) {
				JSONObject itemJSONObject = new JSONObject(item);
				result.put(itemJSONObject);
			}
		}
		return result;
	}

	private JSONArray notesInVersion(String version_id) throws JSONException {
		JSONArray result = new JSONArray();
		VersionHasNoteArrayModel versionHasNoteArrayModel = new VersionHasNoteArrayModel(
				version_id);
		for (VersionHasNoteModel item : versionHasNoteArrayModel.getArray()) {
			if (item.getVersion_id().equals(version_id)) {
				NoteModel noteItem = new NoteModel(item.getNote_id());
				JSONObject noteItemJSON = new JSONObject(noteItem);

				JSONArray commentArray = commentsInNote(noteItem.getId());

				noteItemJSON.put("comment", commentArray);
				result.put(noteItemJSON);
			}
		}
		return result;
	}

	private JSONArray commentsInNote(String noteId) throws JSONException {
		CommentArrayModel commentArrayModel = new CommentArrayModel(noteId);
		JSONArray commentArray = new JSONArray(commentArrayModel.getArray());

		return commentArray;
	}

	private void syncBaseOnClient() throws JSONException {
		// update setting
		JSONObject dbSetting = new JSONObject(db.getSetting(userId));
//		db.updateSetting(dbSetting.getString("wifi_sync"),
//				dbSetting.getString("mobile_newtworksync"),
//				syncTimeStamp.toString(), userId);

		// update folder
		folderSyncBaseOnClient();

		// update folder_has_pack
		folderHasPackSyncBaseOnClient();

		// update user_has_version
		userHasVersionSyncBaseOnClient();

		// update pack
		packSyncBaseOnClient();

		// update version_has_note
		versionHasNoteSyncBaseOnClient();

		// update note
		noteSyncBaseOnClient();

		// update comment
		commentSyncBaseOnClient();

		// update file
		fileSyncBaseOnClient();

		// update version
		versionSyncBaseOnClient();
	}

	private void userHasVersionSyncBaseOnClient() throws JSONException {
		JSONArray dbuserHasVersion = new JSONArray(db.getUserHasVersion(userId));
		// delete userHasVersion in db
		for (int i = 0; i < dbuserHasVersion.length(); i++) {
			// get db's id
			String dbVersionId = dbuserHasVersion.getJSONObject(i).getString(
					"version_id");

			// find db's folder is in client's
			int j = 0;
			for (j = 0; j < userHasVersionData.length(); j++) {
				// get client folder id
				String clientVersionId = userHasVersionData.getJSONObject(i)
						.getString("version_id");
				if (clientVersionId.equals(dbVersionId))
					break;
			}

			// not found folder in client, delete it in db
			if (j == folderHasPackData.length())
				db.deleteUserHasVersion(userId, dbVersionId);
		}

	}

	private void versionSyncBaseOnClient() throws JSONException {
		// add folderHasPack in db
		for (int i = 0; i < versionData.length(); i++) {
			// get client pack id
			JSONObject clientJsonObject = versionData.getJSONObject(i);
			VersionModel clientVersionModel = new VersionModel(clientJsonObject);

			JSONArray data = new JSONArray(db.getVersion(clientVersionModel
					.getId()));

			// not found pack data in db, add it in db
			if (data.length() == 0) {
				clientVersionModel.addToDB();
			}
			// update record if the data is not same
			else {
				JSONObject dbJsonObject = data.getJSONObject(0);
				VersionModel dbVersionModel = new VersionModel(dbJsonObject);
				if (!clientVersionModel.isEqual(dbVersionModel))
					clientVersionModel.updateToDB();
			}
		}

	}

	private void fileSyncBaseOnClient() throws JSONException {
		// add comment in db
		for (int i = 0; i < fileData.length(); i++) {
			// get client comment data
			String clientFilename;

			clientFilename = fileData.getJSONObject(i).getString("filename");

			JSONArray dbFile = new JSONArray(db.getFile(clientFilename));
			if (dbFile.length() == 0)
				db.addFile(clientFilename,
						fileData.getJSONObject(i).getString("version_id"),
						fileData.getJSONObject(i).getString("version_pack_id"));
		}
	}

	private void commentSyncBaseOnClient() throws JSONException {
		// add comment in db
		for (int i = 0; i < commentData.length(); i++) {
			// get client comment data
			String clientCommentId = commentData.getJSONObject(i).getString(
					"id");

			JSONArray dbNote = new JSONArray(db.getComment(clientCommentId));
			if (dbNote.length() == 0)
				db.addComment(clientCommentId, commentData.getJSONObject(i)
						.getString("content"), commentData.getJSONObject(i)
						.getString("create_time"), commentData.getJSONObject(i)
						.getString("note_id"), userId);
		}

	}

	private void noteSyncBaseOnClient() throws JSONException {
		// add note in db
		for (int i = 0; i < noteData.length(); i++) {
			// get client note data
			String clientNoteId = noteData.getJSONObject(i).getString("id");

			JSONArray dbNote = new JSONArray(db.getNote(clientNoteId));
			if (dbNote.length() == 0)
				db.addNote(clientNoteId,
						noteData.getJSONObject(i).getInt("color"), noteData
								.getJSONObject(i).getString("content"),
						noteData.getJSONObject(i).getString("create_time"),
						userId);
		}
	}

	private void versionHasNoteSyncBaseOnClient() throws JSONException {

		// add folderHasPack in db
		for (int i = 0; i < versionHasNoteData.length(); i++) {
			// get client folder data
			String clientVersionId = versionHasNoteData.getJSONObject(i)
					.getString("version_id");
			String clientNoteId = versionHasNoteData.getJSONObject(i)
					.getString("note_id");

			JSONArray dbVersionHasNote = new JSONArray(db.getVersionHasNote(
					clientVersionId, clientNoteId));
			if (dbVersionHasNote.length() == 0)
				db.addVersionHasNote(clientVersionId, versionHasNoteData
						.getJSONObject(i).getString("version_pack_id"),
						clientNoteId, versionHasNoteData.getJSONObject(i)
								.getInt("position"), versionHasNoteData
								.getJSONObject(i).getInt("position_length"));
		}
	}

	private void packSyncBaseOnClient() throws JSONException {
		// add and update folderHasPack in db
		for (int i = 0; i < packData.length(); i++) {

			// get i th object
			JSONObject clientJsonObject = folderHasPackData.getJSONObject(i);
			PackModel clientPackModel = new PackModel(clientJsonObject);

			// get dbJsonObject
			JSONArray data = new JSONArray(db.getPack(clientPackModel.getId()));

			// not found pack data in db, add it in db
			if (data.length() == 0) {
				clientPackModel.addToDB();
			} else {
				JSONObject dbJsonObject = data.getJSONObject(0);
				PackModel dbPackModel = new PackModel(dbJsonObject);
				if (!clientPackModel.isEqual(dbPackModel))
					clientPackModel.updateToDB();
			}

		}

	}

	private void folderHasPackSyncBaseOnClient() throws JSONException {
		JSONArray dbFolderHasPack = new JSONArray(db.getFolderHasPack(userId));

		// delete folderHasPack in db
		for (int i = 0; i < dbFolderHasPack.length(); i++) {
			// get db's id
			String dbFolderId = dbFolderHasPack.getJSONObject(i).getString(
					"folder_id");
			String dbPackId = dbFolderHasPack.getJSONObject(i).getString(
					"pack_id");

			// find db's folder is in client's
			int j = 0;
			for (j = 0; j < folderHasPackData.length(); j++) {
				// get client folder id
				String clientFolderId = folderHasPackData.getJSONObject(i)
						.getString("folder_id");
				String clientPackId = folderHasPackData.getJSONObject(i)
						.getString("pack_id");
				if (clientFolderId.equals(dbFolderId)
						&& clientPackId.equals(dbPackId))
					break;
			}

			// not found folder in client, delete it in db
			if (j == folderHasPackData.length())
				db.deleteFolderHasPack(dbFolderId, dbPackId);
		}

		// add folderHasPack in db
		for (int i = 0; i < folderHasPackData.length(); i++) {
			// get client folder data
			String clientFolderId = folderHasPackData.getJSONObject(i)
					.getString("folder_id");
			String clientPackId = folderHasPackData.getJSONObject(i).getString(
					"pack_id");

			// find folder data in db
			int j = 0;
			for (; j < dbFolderHasPack.length(); j++) {
				// get db folder data
				String dbFolderId = dbFolderHasPack.getJSONObject(i).getString(
						"folder_id");
				String dbPackId = dbFolderHasPack.getJSONObject(i).getString(
						"pack_id");

				// find the same id and name
				if (clientFolderId.equals(dbFolderId)
						&& clientPackId.equals(dbPackId))
					break;
			}

			// not found folder in db, add it in db
			if (j == dbFolderHasPack.length())
				db.addFolderHasPack(clientFolderId, clientPackId, userId);
		}
	}

	private void folderSyncBaseOnClient() throws JSONException {
		JSONArray dbFolder = new JSONArray(db.getFolder(userId));

		// delete folder in db
		for (int i = 0; i < dbFolder.length(); i++) {
			// get db folder id
			String dbFolderId = dbFolder.getJSONObject(i).getString("id");

			// find db's folder is in client's
			int j = 0;
			for (j = 0; j < folderData.length(); j++) {
				// get client folder id
				String clientFolderId = folderData.getJSONObject(i).getString(
						"id");
				if (clientFolderId.equals(dbFolderId))
					break;
			}

			// not found folder in client, delete it in db
			if (j == folderData.length())
				db.deleteFolder(dbFolderId);
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
				if (clientFolderId.equals(dbFolderId)
						&& clientFoldername.equals(dbFoldername))
					break;
			}

			// not found folder in db, add it in db
			if (j == dbFolder.length())
				db.addFolder(clientFolderId, clientFoldername, userId);
		}
	}

	// decide server or client has newer data by last_sync_time
	private boolean isClientNewer() throws JSONException {

		// get user's last sync time from server
		JSONObject dbSetting = new JSONObject(db.getSetting(userId));
		String dbSyncTime = dbSetting.getString("last_sync_time");
		Timestamp dbSyncTimeStamp = Timestamp.valueOf(dbSyncTime);

		// get user's last sync time from client
		Timestamp clientSyncTime = Timestamp.valueOf(settingData
				.getString("last_sync_time"));

		// compare who's data are newer
		// true mean clientSyncTime is after dbSyncTimeStamp
		if (dbSyncTimeStamp.compareTo(clientSyncTime) < 0)
			return true;
		else
			return false;
	}

	// extract json data to its json object
	private void readSyncJsonData(String syncJsonData) throws JSONException {
		syncData = new JSONObject(syncJsonData);
		userData = new JSONObject(syncData.get("user"));
		settingData = new JSONObject(syncData.get("setting"));
		folderData = new JSONArray(syncData.get("folder"));
		folderHasPackData = new JSONArray(syncData.get("folder_has_pack"));
		packData = new JSONArray(syncData.get("folder_has_pack"));
		versionHasNoteData = new JSONArray(syncData.get("version_has_note"));
		noteData = new JSONArray(syncData.get("note"));
		commentData = new JSONArray(syncData.get("comment"));
		fileData = new JSONArray(syncData.get("file"));
		versionData = new JSONArray(syncData.get("version"));
		userHasVersionData = new JSONArray(syncData.get("user_has_version"));

		userId = userData.getString("id");
	}

	public void test() {
		userId = "00157016";
		try {
			syncBaseOnServer();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(responseJson);
	}

}
