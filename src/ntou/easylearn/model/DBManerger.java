package ntou.easylearn.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DBManerger {
	private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/easylearn?useUnicode=true&characterEncoding=Big5";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "root";

	private Connection dbConnection = null;
	private PreparedStatement pStat = null;
	private String selectSQL = null;
	private ResultSet rs = null;

	public DBManerger() {
		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException:@@" + e.toString());
		}

		try {
			dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER,
					DB_PASSWORD);
		} catch (SQLException e) {
			System.out.println("SQLException:" + e.toString());
		}

	}

	private void closeDatabaseConnection() {
		// close statement
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (pStat != null) {
				pStat.close();
				pStat = null;
			}
		} catch (SQLException e) {
			System.out.println("Close Exception :" + e.toString());
		}
	}

	/**
	 * add user by name and id
	 */
	public String addUser(String id, String name) {
		// create current time stamp
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

		try {

			// insert sql
			String insertdbSQL = "INSERT INTO `easylearn`.`user`(`id`,`name`,`create_time`)VALUES(?,?,?)";
			pStat = dbConnection.prepareStatement(insertdbSQL);
			pStat.setString(1, id);
			pStat.setString(2, name);
			pStat.setString(3, currentTimestamp.toString());
			pStat.executeUpdate();

		} catch (SQLException e) {
			System.out
					.println("[DBManerger addUser] Exception:" + e.toString());
			return "[DBManerger addUser] Fail";
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger addUser] success";
	}

	/**
	 * find user using id
	 */
	public String findUser(String id) {
		try {
			selectSQL = "SELECT * FROM easylearn.user WHERE user.id=?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, id);
			rs = pStat.executeQuery();

			// return userid if found
			while (rs.next()) {
				return rs.getString("id");
			}
		} catch (SQLException e) {
			System.out.println("[DBManerger findUser] Exception :"
					+ e.toString());
			return "[DBManerger findUser] failed";
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger findUser]user not find";
	}

	/**
	 * add user's setting. insert only userID. Other value has default value in
	 * db.
	 */
	public String addSetting(String userId) {
		try {
			String insertdbSQL = "INSERT INTO `easylearn`.`setting`(`user_id`)VALUES(?)";
			pStat = dbConnection.prepareStatement(insertdbSQL);
			pStat.setString(1, userId);
			pStat.executeUpdate();
		} catch (SQLException e) {
			System.out.println("[DBManerger addSetting] Exception:"
					+ e.toString());
			return "[DBManerger addSetting] Fail";
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger addSetting] success";
	}

	/**
	 * get user's setting by user's id
	 */
	public JSONObject getSetting(String userId) {
		// create json object
		JSONObject obj = new JSONObject();

		try {
			selectSQL = "SELECT `setting`.`wifi_sync`, `setting`.`mobile_network_sync`, `setting`.`last_sync_time`, `setting`.`version`"
					+ " FROM `easylearn`.`setting`" + "WHERE `user_id`=?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, userId);
			rs = pStat.executeQuery();
			JSONArray jsonArray = ResultSetConverter.convert(rs);
			if (jsonArray.length() != 0)
				obj = jsonArray.getJSONObject(0);
		} catch (SQLException e) {
			System.out.println("[DBManerger getSetting] Exception :"
					+ e.toString());
		} catch (JSONException e) {
			System.out.print("[DBManerger getSetting] Exception :");
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
		return obj;
	}

	/**
     * 
     */
	public String updateSetting(boolean wifi_sync, boolean mobile_network_sync,
			String last_sync_time,int version, String user_id) {
		try {
			String updateSQL = "UPDATE setting "
					+ "SET wifi_sync = ?, mobile_network_sync = ?, last_sync_time = ?, version = ? "
					+ "WHERE user_id =?";
			pStat = dbConnection.prepareStatement(updateSQL);
			pStat.setBoolean(1, wifi_sync);
			pStat.setBoolean(2, mobile_network_sync);
			pStat.setString(3, last_sync_time);
			pStat.setInt(4, version);
			pStat.setString(5, user_id);
			pStat.executeUpdate();
			return ("[DBManerger updateSetting] Success");
		}
		/*--------------------- failed to inserting data to database  ---------------------*/
		catch (SQLException e) {
			System.out.println("[DBManerger updateSetting] :" + e.toString());
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger updateSetting] Fail";
	}

	public String syncTime(String last_sync_time, String user_id) {
		try {
			String updateSQL = "UPDATE setting " + "SET last_sync_time = ?"
					+ "WHERE user_id =?";
			pStat = dbConnection.prepareStatement(updateSQL);
			pStat.setString(1, last_sync_time);
			pStat.setString(2, user_id);
			pStat.executeUpdate();
			return ("[DBManerger syncTime] Success");
		}
		/*--------------------- failed to inserting data to database  ---------------------*/
		catch (SQLException e) {
			System.out.println("[DBManerger syncTime] :" + e.toString());
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger syncTime] Fail";
	}

	public String updatePack(String id, String name, String description,
			long createTime, String tags, boolean is_public) {
		try {
			String updateSQL = "UPDATE pack "
					+ "SET name = ?, description = ?, create_time = ?, tags=?, is_public=? "
					+ "WHERE `id`=?;";
			pStat = dbConnection.prepareStatement(updateSQL);
			pStat.setString(1, name);
			pStat.setString(2, description);
			pStat.setTimestamp(3, new Timestamp(createTime));
			pStat.setString(4, tags);
			pStat.setBoolean(5, is_public);
			pStat.setString(6, id);
			pStat.executeUpdate();
			return ("[DBManerger updatePack] Success");
		}
		/*--------------------- failed to inserting data to database  ---------------------*/
		catch (SQLException e) {
			System.out.println("[DBManerger updatePack] :" + e.toString());
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger updatePack] Fail";
	}

	public String updateVersion(String id, String content, long create_time,
			String pack_id, boolean is_public, int version) {
		try {
			String updateSQL = "UPDATE version "
					+ "SET content = ?, create_time = ?, pack_id=?, is_public=?, version=? "
					+ "WHERE id =?";
			pStat = dbConnection.prepareStatement(updateSQL);
			pStat.setString(1, content);
			pStat.setTimestamp(2, new Timestamp(create_time));
			pStat.setString(3, pack_id);
			pStat.setBoolean(4, is_public);
			pStat.setInt(5, version);
			pStat.setString(6, id);
			pStat.executeUpdate();
			return ("[DBManerger updateVersion] Success");
		}
		/*--------------------- failed to inserting data to database  ---------------------*/
		catch (SQLException e) {
			System.out.println("[DBManerger updateVersion] :" + e.toString());
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger updateVersion] Fail";
	}

	/**
	 * add folder
	 */
	public String addFolder(String id, String name, String userId) {
		try {
			String insertdbSQL = "INSERT INTO `easylearn`.`folder`(`id`,`name`,`user_id`)VALUES(?,?,?)";
			pStat = dbConnection.prepareStatement(insertdbSQL);
			pStat.setString(1, id);
			pStat.setString(2, name);
			pStat.setString(3, userId);
			pStat.executeUpdate();

		} catch (SQLException e) {
			System.out.println("[DBManerger addFolder] Exception:"
					+ e.toString());
			return "[DBManerger addFolder] Fail";
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger addFolder] success";

	}

	/**
     * 
     */
	public String updateFolder(String id, String name, String user_id) {
		try {
			String updateSQL = "UPDATE folder " + "SET name = ?"
					+ "WHERE user_id =? AND  id = ?";
			pStat = dbConnection.prepareStatement(updateSQL);
			pStat.setString(1, name);
			pStat.setString(2, user_id);
			pStat.setString(3, id);
			pStat.executeUpdate();
			return ("[DBManerger updateFolder] Success");
		}
		/*--------------------- failed to inserting data to database  ---------------------*/
		catch (SQLException e) {
			System.out.println("[DBManerger updateFolder] :" + e.toString());
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger updateFolder] Fail";
	}

	public String updateBookmark(String id, String name, int position,
			String user_id, String version_id, String version_pack_id) {
		JSONArray storeJsonArray = new JSONArray();

		try {
			selectSQL = "UPDATE `bookmark` SET  `name`=?, `position`=?, `user_id`=?, `version_id`=?, `version_pack_id`=?"
					+ "WHERE `id`=?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, name);
			pStat.setInt(2, position);
			pStat.setString(3, user_id);
			pStat.setString(4, version_id);
			pStat.setString(5, version_pack_id);
			pStat.setString(6, id);
			rs = pStat.executeQuery();

			return ("[DBManerger updateBookmark] Success");
		} catch (SQLException e) {
			System.out.println("[DBManerger updateBookmark] Exception :"
					+ e.toString());
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger updateFolder] Fail";
	}

	/**
     * 
     */
	public String deleteFolder(String folderId) {
		try {
			String DELETE_SQL = "DELETE FROM `folder` " + "WHERE id= ?";
			pStat = dbConnection.prepareStatement(DELETE_SQL);
			pStat.setString(1, folderId);
			pStat.executeUpdate();
			return ("[DBManerger deleteFolder] Success");
		}
		/*--------------------- failed to inserting data to database  ---------------------*/
		catch (SQLException e) {
			System.out.println("[DBManerger deleteFolder] :" + e.toString());
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger deleteFolder] Fail";
	}
	
	public String deleteUserFolder(String userId) {
		try {
			String DELETE_SQL = "DELETE FROM `easylearn`.`folder` " + "WHERE user_id= ?";
			pStat = dbConnection.prepareStatement(DELETE_SQL);
			pStat.setString(1, userId);
			pStat.executeUpdate();
			return ("[DBManerger deleteFolder] Success");
		}
		/*--------------------- failed to inserting data to database  ---------------------*/
		catch (SQLException e) {
			System.out.println("[DBManerger deleteUserFolder] :" + e.toString());
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger deleteUserFolder] Fail";
	}

	/**
	 * get user's folder return json array
	 */
	public JSONArray getFolder(String userId) {

		JSONArray jsonArray = new JSONArray();

		try {
			selectSQL = "SELECT `folder`.`id`,`folder`.`name`"
					+ "FROM `easylearn`.`folder`" + "WHERE `user_id`=?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, userId);
			rs = pStat.executeQuery();

			jsonArray = ResultSetConverter.convert(rs);

		} catch (SQLException e) {
			System.out.println("[DBManerger getFolder] Exception :"
					+ e.toString());
		} catch (JSONException e) {
			System.out.print("[DBManerger getFolder] Exception :");
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
		return jsonArray;
	}

	public JSONArray getPackIDArray(String userId, String folderId) {

		JSONArray jsonArray = new JSONArray();

		try {
			selectSQL = "SELECT `pack_id` "
					+ "FROM `easylearn`.`folder_has_pack`"
					+ "WHERE `folder_user_id`=? and `folder_id`=?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, userId);
			pStat.setString(2, folderId);
			rs = pStat.executeQuery();

			while (rs.next()) {
				jsonArray.put(rs.getString("pack_id"));
			}

		} catch (SQLException e) {
			System.out.println("[DBManerger getPackIDArray] Exception :"
					+ e.toString());
		} finally {
			closeDatabaseConnection();
		}
		return jsonArray;
	}
	
	public JSONArray getAllPackIDArray() {

		JSONArray jsonArray = new JSONArray();

		try {
			selectSQL = "SELECT  `id` "
					+ "FROM `easylearn`.`pack`";
			pStat = dbConnection.prepareStatement(selectSQL);
			rs = pStat.executeQuery();

			while (rs.next()) {
				jsonArray.put(rs.getString("id"));
			}

		} catch (SQLException e) {
			System.out.println("[DBManerger getPackIDArray] Exception :"
					+ e.toString());
		} finally {
			closeDatabaseConnection();
		}
		return jsonArray;
	}

	public JSONArray getPackIDArray(String userId) {

		JSONArray jsonArray = new JSONArray();

		try {
			selectSQL = "SELECT  `pack_id` "
					+ "FROM `easylearn`.`folder_has_pack`"
					+ "WHERE `folder_user_id`=?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, userId);
			rs = pStat.executeQuery();

			jsonArray = ResultSetConverter.convert(rs);

		} catch (SQLException e) {
			System.out.println("[DBManerger getPackIDArray] Exception :"
					+ e.toString());
		} catch (JSONException e) {
			System.out.print("[DBManerger getPackIDArray] Exception :");
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
		return jsonArray;
	}

	public String getUserHasVersion(String UserId) {
		JSONArray storeJsonArray = new JSONArray();

		try {
			selectSQL = "SELECT * FROM `easylearn`.`user_has_version`"
					+ "WHERE `user_id`=?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, UserId);
			rs = pStat.executeQuery();

			while (rs.next()) {
				// create json object
				JSONObject obj = new JSONObject();

				obj.put("user_id", rs.getString("user_id"));
				obj.put("version_id", rs.getString("version_id"));
				obj.put("version_pack_id", rs.getString("version_pack_id"));

				storeJsonArray.put(obj);
			}
		} catch (SQLException e) {
			System.out.println("[DBManerger getUserHasVersion] Exception :"
					+ e.toString());
		} catch (JSONException e) {
			System.out.print("[DBManerger getUserHasVersion] Exception :");
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
		return storeJsonArray.toString();
	}

	public String deleteUserHasVersion(String userId) {

		try {
			selectSQL = "DELETE FROM `user_has_version` "
					+ "WHERE `user_id`= ?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, userId);
			pStat.executeUpdate();
			return ("[DBManerger deleteUserHasVersion] Success");

		} catch (SQLException e) {
			System.out.println("[DBManerger deleteUserHasVersion] Exception :"
					+ e.toString());
		} finally {
			closeDatabaseConnection();
		}
		return ("[DBManerger deleteUserHasVersion] Fail");
	}

	public String getFolderHasPack(String user_id) {
		JSONArray storeJsonArray = new JSONArray();

		try {
			selectSQL = "SELECT `folder_id`, `folder_user_id`, `pack_id` FROM `easylearn`.`folder_has_pack`"
					+ "WHERE `folder_user_id`=?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, user_id);
			rs = pStat.executeQuery();

			while (rs.next()) {
				// create json object
				JSONObject obj = new JSONObject();

				obj.put("folder_id", rs.getString("folder_id"));
				obj.put("folder_user_id", rs.getString("folder_user_id"));
				obj.put("pack_id", rs.getString("pack_id"));

				storeJsonArray.put(obj);
			}
		} catch (SQLException e) {
			System.out.println("[DBManerger getFolderHasPack] Exception :"
					+ e.toString());
		} catch (JSONException e) {
			System.out.print("[DBManerger getFolderHasPack] Exception :");
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
		return storeJsonArray.toString();
	}

	/**
	 * get notes by version id
	 */
	public String getVersionHasNote(String versionId) {
		JSONArray storeJsonArray = new JSONArray();

		try {
			selectSQL = "SELECT `version_id`, `version_pack_id`, `note_id` FROM `easylearn`.`version_has_note`"
					+ "WHERE `version_id`=?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, versionId);
			rs = pStat.executeQuery();

			while (rs.next()) {
				// create json object
				JSONObject obj = new JSONObject();

				obj.put("version_id", rs.getString("version_id"));
				obj.put("version_pack_id", rs.getString("version_pack_id"));
				obj.put("note_id", rs.getString("note_id"));

				storeJsonArray.put(obj);
			}
		} catch (SQLException e) {
			System.out.println("[DBManerger getFolderHasPack] Exception :"
					+ e.toString());
		} catch (JSONException e) {
			System.out.print("[DBManerger getFolderHasPack] Exception :");
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
		return storeJsonArray.toString();
	}

	public String getVersionHasNote(String versionId, String note_id) {
		JSONArray storeJsonArray = new JSONArray();

		try {
			selectSQL = "SELECT `version_id`, `version_pack_id`, `note_id`, `position`, `position_length` FROM `easylearn`.`version_has_note`"
					+ "WHERE `version_id`=? and `note_id`=?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, versionId);
			pStat.setString(1, note_id);
			rs = pStat.executeQuery();

			while (rs.next()) {
				// create json object
				JSONObject obj = new JSONObject();

				obj.put("version_id", rs.getString("version_id"));
				obj.put("version_pack_id", rs.getString("version_pack_id"));
				obj.put("note_id", rs.getString("note_id"));
				obj.put("position", rs.getString("position"));
				obj.put("position_length", rs.getString("position_length"));

				storeJsonArray.put(obj);
			}
		} catch (SQLException e) {
			System.out.println("[DBManerger getFolderHasPack] Exception :"
					+ e.toString());
		} catch (JSONException e) {
			System.out.print("[DBManerger getFolderHasPack] Exception :");
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
		return storeJsonArray.toString();
	}

	/**
     * 
     */
	public String addFolderHasPack(String folder_id, String pack_id,
			String folder_user_id) {
		try {
			String insertdbSQL = "INSERT INTO `easylearn`.`folder_has_pack` (`folder_id`, `pack_id`, `folder_user_id`) VALUES (?, ?, ?)";
			pStat = dbConnection.prepareStatement(insertdbSQL);
			pStat.setString(1, folder_id);
			pStat.setString(2, pack_id);
			pStat.setString(3, folder_user_id);
			pStat.executeUpdate();

		} catch (SQLException e) {
			System.out.println("[DBManerger addFolderHasPack] Exception:"
					+ e.toString());
			return "[DBManerger addFolderHasPack] Fail";
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger addFolderHasPack] success";
	}

	/**
     * 
     */
	public String deleteFolderHasPack(String folder_id, String user_id) {
		try {
			String DELETE_SQL = "DELETE FROM `folder_has_pack` "
					+ "WHERE `folder_id`= ? and `folder_user_id`=?";
			pStat = dbConnection.prepareStatement(DELETE_SQL);
			pStat.setString(1, folder_id);
			pStat.setString(2, user_id);
			pStat.executeUpdate();
			return ("[DBManerger deleteFolderHasPack] Success");
		} catch (SQLException e) {
			System.out.println("[DBManerger deleteFolderHasPack] :"
					+ e.toString());
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger deleteFolderHasPack] Fail";
	}

	/**
     * 
     */
	public JSONObject getVersion(String versionId) {
		JSONObject obj = new JSONObject();

		try {
			selectSQL = "SELECT `id`, `content`, `create_time`, `pack_id`, `is_public`, `creator_user_id`, `version`, `creator_user_name` FROM `easylearn`.`get_version` "
					+ "WHERE `id`=?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, versionId);
			rs = pStat.executeQuery();

			JSONArray temp = ResultSetConverter.convert(rs);
			if (temp.length() != 0)
				obj = temp.getJSONObject(0);

		} catch (SQLException e) {
			System.out.println("[DBManerger getVersion] Exception :"
					+ e.toString());
		} catch (JSONException e) {
			System.out.print("[DBManerger getVersion] Exception :");
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
		System.out.println("VERSION:"+obj);
		return obj;
	}

	/**
     * 
     */
	public String addVersion(String id, String content, long create_time,
			String pack_id, boolean is_public, String creator_user_id, int version) {
		try {
			String insertdbSQL = "INSERT INTO `easylearn`.`version` (`id`, `content`, `create_time`, `pack_id`, `is_public`, `creator_user_id`, `version`) VALUES (?, ?, ?, ?, ?, ?, ?)";
			pStat = dbConnection.prepareStatement(insertdbSQL);
			pStat.setString(1, id);
			pStat.setString(2, content);
			pStat.setTimestamp(3, new Timestamp(create_time));
			pStat.setString(4, pack_id);
			pStat.setBoolean(5, is_public);
			pStat.setString(6, creator_user_id);
			pStat.setInt(7, version);
			pStat.executeUpdate();

		} catch (SQLException e) {
			System.out.println("[DBManerger addVersion] Exception:"
					+ e.toString());
			return "[DBManerger addVersion] Fail";
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger addVersion] success";
	}

	/**
	 * add file
	 */
	public String addFile(String filename, String version_id,
			String version_pack_id) {
		try {
			String insertdbSQL = "INSERT INTO `easylearn`.`file` (`filename`, `version_id`, `version_pack_id`) VALUES (?, ?, ?);";
			pStat = dbConnection.prepareStatement(insertdbSQL);
			pStat.setString(1, filename);
			pStat.setString(2, version_id);
			pStat.setString(3, version_pack_id);
			pStat.executeUpdate();

		} catch (SQLException e) {
			System.out
					.println("[DBManerger addFile] Exception:" + e.toString());
			return "[DBManerger addFile] Fail";
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger addFile] success";
	}

	/**
	 * get file with version id
	 */
	public JSONArray getFile(String version_id) {
		JSONArray jsonArray = new JSONArray();

		try {
			selectSQL = "SELECT `filename` FROM `easylearn`.`file` WHERE `version_id`=?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, version_id);
			rs = pStat.executeQuery();

			while(rs.next()){
				jsonArray.put(rs.getString("filename"));
			}
		} catch (SQLException e) {
			System.out.println("[DBManerger getFile] Exception :"
					+ e.toString());
		} finally {
			closeDatabaseConnection();
		}
		return jsonArray;
	}

	public JSONObject getFile(String version_id, String filename) {
		JSONObject obj = new JSONObject();

		try {
			selectSQL = "SELECT `filename` FROM `easylearn`.`file` WHERE `version_id`=? and `filename`=?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, version_id);
			pStat.setString(2, filename);
			rs = pStat.executeQuery();

			JSONArray jsonArray = ResultSetConverter.convert(rs);
			if (jsonArray.length() != 0)
				obj = jsonArray.getJSONObject(0);

		} catch (SQLException e) {
			System.out.println("[DBManerger getFile] Exception :"
					+ e.toString());
		} catch (JSONException e) {
			System.out.print("[DBManerger getFile] Exception :");
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
		return obj;
	}

	/**
	 * getNote by id
	 */
	public JSONArray getNotes(String versionId) {
		JSONArray jsonArray = new JSONArray();

		try {
			selectSQL = "SELECT`note`.`id`, `content`, `note`.`create_time`, `user_id` "
					+ "FROM `easylearn`.`note`"
					+ "INNER JOIN `easylearn`.`version_has_note` ON `note`.`id`=`version_has_note`.`note_id`"
					+ "INNER JOIN `easylearn`.`user` ON `note`.`user_id`=`user`.`id`"
					+ "WHERE `version_id`=?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, versionId);
			rs = pStat.executeQuery();

			jsonArray = ResultSetConverter.convert(rs);
		} catch (SQLException e) {
			System.out.println("[DBManerger getNotes] Exception :"
					+ e.toString());
		} catch (JSONException e) {
			System.out.print("[DBManerger getNotes] Exception :");
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
		return jsonArray;
	}

	public JSONArray getNote(String noteId) {
		JSONArray jsonArray = new JSONArray();

		try {
			selectSQL = "SELECT`note`.`id`, `content`, `note`.`create_time`, `user_id` "
					+ "FROM `easylearn`.`note`"
					+ "INNER JOIN `easylearn`.`version_has_note` ON `note`.`id`=`version_has_note`.`note_id`"
					+ "INNER JOIN `easylearn`.`user` ON `note`.`user_id`=`user`.`id`"
					+ "WHERE `note`.`id`=?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, noteId);
			rs = pStat.executeQuery();

			jsonArray = ResultSetConverter.convert(rs);
		} catch (SQLException e) {
			System.out.println("[DBManerger getNote] Exception :"
					+ e.toString());
		} catch (JSONException e) {
			System.out.print("[DBManerger getNote] Exception :");
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
		return jsonArray;
	}

	/**
     * 
     */
	public String addNote(String id, String content, long create_time,
			String user_id) {
		try {
			String insertdbSQL = "INSERT INTO `easylearn`.`note` (`id`,  `content`, `create_time`, `user_id`) VALUES (?, ?, ?, ?);";
			pStat = dbConnection.prepareStatement(insertdbSQL);
			pStat.setString(1, id);
			pStat.setString(2, content);
			pStat.setTimestamp(3, new Timestamp(create_time));
			pStat.setString(4, user_id);
			pStat.executeUpdate();

		} catch (SQLException e) {
			System.out
					.println("[DBManerger addNote] Exception:" + e.toString());
			return "[DBManerger addNote] Fail";
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger addNote] success";
	}

	/**
     * 
     */
	public String addComment(String id, String content, long create_time,
			String note_id, String user_id) {

		try {
			String insertdbSQL = "INSERT INTO `easylearn`.`comment` (`id`, `content`, `create_time`, `note_id`, `user_id`) VALUES (?, ?, ?, ?, ?)";
			pStat = dbConnection.prepareStatement(insertdbSQL);
			pStat.setString(1, id);
			pStat.setString(2, content);
			pStat.setTimestamp(3, new Timestamp(create_time));
			pStat.setString(4, note_id);
			pStat.setString(5, user_id);
			pStat.executeUpdate();

		} catch (SQLException e) {
			System.out.println("[DBManerger addComment] Exception:"
					+ e.toString());
			return "[DBManerger addComment] Fail";
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger addComment] success";
	}

	public String addUserHasVersion(String UserId, String version_id,
			String version_pack_id) {
		
		try {
			String insertdbSQL = "INSERT INTO `easylearn`.`user_has_version` (`user_id`, `version_id`, `version_pack_id`) VALUES (?, ?, ?)";
			pStat = dbConnection.prepareStatement(insertdbSQL);
			pStat.setString(1, UserId);
			pStat.setString(2, version_id);
			pStat.setString(3, version_pack_id);
			pStat.executeUpdate();

		} catch (SQLException e) {
			System.out.println("[DBManerger addUserHasVersion] Exception:"
					+ e.toString());
			return "[DBManerger addUserHasVersion] Fail";
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger addUserHasVersion] success";
	}

	/**
     * 
     */
	public String addVersionHasNote(String version_id, String version_pack_id,
			String note_id) {
		try {
			String insertdbSQL = "INSERT INTO `easylearn`.`version_has_note` (`version_id`, `version_pack_id`, `note_id`) VALUES (?, ?, ?);";
			pStat = dbConnection.prepareStatement(insertdbSQL);
			pStat.setString(1, version_id);
			pStat.setString(2, version_pack_id);
			pStat.setString(3, note_id);
			pStat.executeUpdate();

		} catch (SQLException e) {
			System.out.println("[DBManerger addVersionHasNote] Exception:"
					+ e.toString());
			return "[DBManerger addVersionHasNote] Fail";
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger addVersionHasNote] success";
	}

	/**
     * 
     */
	public String deleteVersionHasNote(String id) {
		try {
			selectSQL = "DELETE FROM `version_has_note` " + "WHERE `id`= ?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, id);
			pStat.executeUpdate();
			return ("[DBManerger deleteVersionHasNote] Success");

		} catch (SQLException e) {
			System.out.println("[DBManerger deleteVersionHasNote] Exception :"
					+ e.toString());
		} finally {
			closeDatabaseConnection();
		}
		return ("[DBManerger deleteVersionHasNote] Fail");
	}

	public String deleteFile(String name, String versionId) {
		try {
			selectSQL = "DELETE FROM `file` "
					+ "WHERE `filename`= ? and `version_id`=?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, name);
			pStat.setString(2, versionId);
			pStat.executeUpdate();
			return ("[DBManerger deleteFile] Success");

		} catch (SQLException e) {
			System.out.println("[DBManerger deleteFile] Exception :"
					+ e.toString());
		} finally {
			closeDatabaseConnection();
		}
		return ("[DBManerger deleteFile] Fail");
	}

	/**
	 * get comment with note_id
	 */
	public JSONArray getComments(String note_id) {

		JSONArray jsonArray = new JSONArray();

		try {
			selectSQL = "SELECT *"
					+ "FROM `easylearn`.`comment_with_name`"
					+ "WHERE `note_id`=?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, note_id);
			rs = pStat.executeQuery();

			jsonArray = ResultSetConverter.convert(rs);

		} catch (SQLException e) {
			System.out.println("[DBManerger getComment] Exception :"
					+ e.toString());
		} catch (JSONException e) {
			System.out.print("[DBManerger getComment] Exception :");
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
		return jsonArray;
	}

	public JSONArray getCommentsAfterTime(String note_id, long time) {

		JSONArray jsonArray = new JSONArray();

		try {
			selectSQL = "SELECT *"
					+ "FROM `easylearn`.`comment_with_name` "
					+ "WHERE `note_id`=?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, note_id);
			rs = pStat.executeQuery();

			jsonArray = ResultSetConverter.convert(rs);
			
			System.out.println(jsonArray);
			int i;
			for(i = 0; i < jsonArray.length(); i++){
				System.out.println(jsonArray.getJSONObject(i).getLong("create_time"));
				System.out.println(jsonArray.getJSONObject(i).getLong("create_time") -  time);
				if(jsonArray.getJSONObject(i).getLong("create_time") <= time){
					jsonArray.remove(i);
					i--;
				}
			}

		} catch (SQLException e) {
			System.out.println("[DBManerger getComment] Exception :"
					+ e.toString());
		} catch (JSONException e) {
			System.out.print("[DBManerger getComment] Exception :");
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
		return jsonArray;
	}

	public JSONObject getComment(String comment_id) {

		JSONObject obj = new JSONObject();

		try {
			selectSQL = "SELECT *"
					+ "FROM `easylearn`.`comment_with_name` " + "WHERE `id`=?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, comment_id);
			rs = pStat.executeQuery();
			JSONArray temp = ResultSetConverter.convert(rs);
			if (temp.length() != 0)
				obj = temp.getJSONObject(0);

		} catch (SQLException e) {
			System.out.println("[DBManerger getComment] Exception :"
					+ e.toString());
		} catch (JSONException e) {
			System.out.print("[DBManerger getComment] Exception :");
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
		return obj;
	}

	/**
	 * add pack tags is json array data
	 */
	public String addPack(String id, String name, String description,
			long createTime, String tags, boolean is_public,
			String creator_user_id, String cover_filename) {
		try {
			String insertdbSQL = "INSERT INTO `easylearn`.`pack` (`id`, `name`, `description`, `create_time`, `tags`, `is_public`, `creator_user_id`, `cover_filename`) VALUES (?, ?,?, ?, ?, ?, ?, ?)";
			pStat = dbConnection.prepareStatement(insertdbSQL);
			pStat.setString(1, id);
			pStat.setString(2, name);
			pStat.setString(3, description);
			pStat.setTimestamp(4, new Timestamp(createTime));
			pStat.setString(5, tags);
			pStat.setBoolean(6, is_public);
			pStat.setString(7, creator_user_id);
			pStat.setString(8, cover_filename);
			pStat.executeUpdate();

		} catch (SQLException e) {
			System.out
					.println("[DBManerger addPack] Exception:" + e.toString());
			return "[DBManerger addPack] Fail";
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger addPack] success";
	}

	/**
	 * get pack by its id
	 */
	public JSONObject getPack(String packId) {
		JSONObject obj = new JSONObject();

		try {
			selectSQL = "SELECT `pack`.`id`, `pack`.`name`, `description`, `pack`.`create_time`, `tags`, `is_public`, `creator_user_id`, `cover_filename`, `user`.`name` AS `creator_user_name`"
					+ " FROM `easylearn`.`pack` INNER JOIN `easylearn`.`user` ON `pack`.`creator_user_id` = `user`.`id`" + " WHERE `pack`.`id`=? ";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, packId);
			rs = pStat.executeQuery();

			JSONArray jsonArray = ResultSetConverter.convert(rs);
			if (jsonArray.length() != 0)
				obj = jsonArray.getJSONObject(0);
		} catch (SQLException e) {
			System.out.println("[DBManerger getPack] Exception :"
					+ e.toString());
		} catch (JSONException e) {
			System.out.print("[DBManerger getPack] Exception :");
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
		return obj;
	}
	
	public JSONArray getPacksVersion(String packId) {
		JSONArray jsonArray = new JSONArray();

		try {
			selectSQL = "SELECT `id`, `content`,`create_time`, `is_public`, `creator_user_id`, `version`, `creator_user_name` "
					+ "FROM `easylearn`.`get_packs_version` "
					+ "WHERE `pack_id` = ?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, packId);
			System.out.println(selectSQL);
			rs = pStat.executeQuery();

			jsonArray = ResultSetConverter.convert(rs);
		} catch (SQLException e) {
			System.out.println("[DBManerger getPacksVersion()] Exception :"
					+ e.toString());
		} catch (JSONException e) {
			System.out.print("[DBManerger getPacksVersion()] Exception :");
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
		System.out.println(jsonArray);
		return jsonArray;
	}

	public JSONArray getBookmark(String user_id, String versionid) {
		JSONArray jsonArray = new JSONArray();

		try {
			selectSQL = "SELECT `id`, `name`, `position`"
					+ "FROM `easylearn`.`bookmark` "
					+ "WHERE `user_id`=? AND `version_id`=?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, user_id);
			pStat.setString(2, versionid);
			rs = pStat.executeQuery();

			jsonArray = ResultSetConverter.convert(rs);

		} catch (SQLException e) {
			System.out.println("[DBManerger getBookmark] Exception :"
					+ e.toString());
		} catch (JSONException e) {
			System.out.print("[DBManerger getBookmark] Exception :");
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
		return jsonArray;
	}

	public String addBookmark(String id, String name, int position,
			String user_id, String version_id, String version_pack_id) {
		try {
			String insertdbSQL = "INSERT INTO `easylearn`.`bookmark` (`id`, `name`, `position`, `user_id`, `version_id`, `version_pack_id`) VALUES (?, ?, ?, ?, ?, ?);";
			pStat = dbConnection.prepareStatement(insertdbSQL);
			pStat.setString(1, id);
			pStat.setString(2, name);
			pStat.setInt(3, position);
			pStat.setString(4, user_id);
			pStat.setString(5, version_id);
			pStat.setString(6, version_pack_id);
			pStat.executeUpdate();

		} catch (SQLException e) {
			System.out.println("[DBManerger addBookmark] Exception:"
					+ e.toString());
			return "[DBManerger addBookmark] Fail";
		} finally {
			closeDatabaseConnection();
		}
		return "[DBManerger addBookmark] success";
	}

	public String deleteBookmark(String userId) {
		try {
			selectSQL = "DELETE FROM `bookmark` " + "WHERE `user_id`= ?";
			pStat = dbConnection.prepareStatement(selectSQL);
			pStat.setString(1, userId);
			pStat.executeUpdate();
			return ("[DBManerger deleteBookmark] Success");

		} catch (SQLException e) {
			System.out.println("[DBManerger deleteBookmark] Exception :"
					+ e.toString());
		} finally {
			closeDatabaseConnection();
		}
		return ("[DBManerger deleteBookmark] Fail");
	}
	
	//search pack name
	public JSONArray search(String keyword) {
		JSONArray jsonArray = null;

		try {
			selectSQL = "SELECT `pack`.`id`, `pack`.`name`, `description`, `pack`.`create_time`, `tags`, `is_public`, `creator_user_id`, `cover_filename`, `user`.`name` AS `creator_user_name`"
					+ " FROM `easylearn`.`pack` INNER JOIN `easylearn`.`user` ON `pack`.`creator_user_id` = `user`.`id`" + " WHERE `pack`.`name` LIKE '%"+keyword+"%'";
			pStat = dbConnection.prepareStatement(selectSQL);
			//pStat.setString(1, keyword);
			rs = pStat.executeQuery();

			jsonArray = ResultSetConverter.convert(rs);

		} catch (SQLException e) {
			System.out.println("[DBManerger search] Exception :"
					+ e.toString());
		} catch (JSONException e) {
			System.out.print("[DBManerger search] Exception :");
			e.printStackTrace();
		} finally {
			closeDatabaseConnection();
		}
		return jsonArray;
	}
}
