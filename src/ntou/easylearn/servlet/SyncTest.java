package ntou.easylearn.servlet;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

public class SyncTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SyncManerger sm = new SyncManerger();
		JSONObject syncData = sm.test();

		Iterator packIter = syncData.keys();

		// add and update folderHasPack in db
		while (packIter.hasNext()) {
			System.out.println(packIter.next());

		}
	}

}
