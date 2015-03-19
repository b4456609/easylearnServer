package ntou.easylearn.model;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class DBManergerTest {

	String userId = "00157016";
	String userName = "bernie";
	String folderId = "folderId";
	String packId = "packId";
	String versionId = "versionId";
	String noteId = "noteId";
	String commentId = "commentId";
	String filename = "filename";

	// TODO Auto-generated method stub
	private DBManerger db = new DBManerger();

	// create current time stamp
	Calendar calendar = Calendar.getInstance();
	Date now = calendar.getTime();
	Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

	public static void main(String[] args) {

		DBManergerTest test = new DBManergerTest();

		//test.testDelete();
		test.testAdd();
		//test.testGet();
	}

	private void testAdd(){
		System.out.println(db.addUser(userId, userName));
		System.out.println(db.addSetting(userId));
		System.out.println(db.addFolder(folderId, "newFolderName", userId));		
		System.out.println(db.addPack(packId, "firstPackName", "description", currentTimestamp.toString(), "tagsJsonArray", false, userId));
		System.out.println(db.addFolderHasPack(folderId, packId, userId));
		System.out.println(db.addVersion(versionId, "vvery_long_content", currentTimestamp.toString(), packId, false, userId));
		System.out.println(db.addUserHasVersion(userId, versionId, packId));
		System.out.println(db.addNote(noteId, 1, "content",  currentTimestamp.toString(), userId));
		System.out.println(db.addVersionHasNote(versionId, packId, noteId, 123, 1));
		System.out.println(db.addComment(commentId, "commentContent", currentTimestamp.toString(), noteId, userId));
		System.out.println(db.addFile(filename, versionId, packId));
		System.out.println(db.addBookmark("ff", "name", 123, userId, versionId, packId));

	}
	
	private void testGet(){
		System.out.println(db.getSetting(userId));
		System.out.println(db.getFolder(userId));
		System.out.println(db.getFolderHasPack(userId));
		System.out.println(db.getNote(noteId));
		System.out.println(db.getPack(packId));
		System.out.println(db.getVersion(versionId));
		System.out.println(db.getVersionHasNote(versionId));
		System.out.println(db.getComment(commentId));
		System.out.println(db.getFile(versionId));
		

	}
	
	private void testDelete(){
		System.out.println(db.deleteFolder(folderId));

	}
}
