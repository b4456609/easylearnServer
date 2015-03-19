package ntou.easylearn.servlet;

import java.util.ArrayList;

import ntou.easylearn.model.BookmarkModel;
import ntou.easylearn.model.FolderArrayModel;
import ntou.easylearn.model.SettingModel;

import org.json.JSONArray;
import org.json.JSONObject;

public class SyncTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SyncManerger sm = new SyncManerger();
		sm.test();
		
		ArrayList<SettingModel> al = new ArrayList<SettingModel>();

		al.add(new SettingModel(true, true,"fweew","11565"));
		al.add(new SettingModel(true, true,"fweew","11565"));
		al.add(new SettingModel(true, true,"fweew","11565"));
		al.add(new SettingModel(true, true,"fefee","eeee"));

		System.out.println(al);
		JSONArray jb = new JSONArray(al);
		System.out.println(jb);
	}

}
