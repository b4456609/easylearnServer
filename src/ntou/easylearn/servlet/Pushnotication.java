package ntou.easylearn.servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;

import ntou.easylearn.model.DBManerger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class PackManerger
 */
@WebServlet("/push")
public class Pushnotication extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private DBManerger db;
	private  ArrayList<String> RegIDs ;
	private JSONObject users;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
	try{
		String clientOrigin = request.getHeader("origin");
		response.setHeader("Access-Control-Allow-Origin", clientOrigin);
		response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400");

        request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		String Users = request.getParameter("RegID");
		System.out.println(Users);
		System.out.println("What the hilllllllll");
		users = new JSONObject(Users);
		System.out.println("user:"+users);
		System.out.println("Object:"+users.get("User"));
		String temp = new String (users.get("User").toString());
		JSONArray array = new JSONArray(temp);
		
		db = new DBManerger();

		
		for(int i = 0;i < array.length(); i++){
			
			JSONObject Account = array.getJSONObject(i);
			JSONArray jsonArray = new JSONArray();   
			//取得物件內資料
			System.out.println("name:"+Account.getString("name"));
			RegIDs=db.findDevice(Account.getString("name"));

			System.out.println("RegIDs"+RegIDs);	
			for(String RegID:RegIDs){                     
	              jsonArray.put(RegID);//將資料表中的各Registration ID放入jsonArray中
	              System.out.println(RegID);
			}
			
		    HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		    CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
		    HttpPost httpPost = new HttpPost("https://gcm-http.googleapis.com/gcm/send");
		           //GCM服務的API Key                    
		           httpPost.addHeader("Authorization","key=AIzaSyB4hcjvpiAStqtjOqfVrAuDtIxm-NItaes");
		          //選擇使用JSON型式傳送，在Header中加上Content-Type為application/json的參數
		          httpPost.addHeader("Content-Type","application/json;");
		          httpPost.addHeader("charset", "UTF-8"); 
		          //建立JSON內容
		          JSONObject jsonObject = new JSONObject();   //要傳送的JSON物件
		          JSONObject data = new JSONObject();
		      //填好JSONObject的各項值
		          data.put("message","有人與你分享懶人包");
		          data.put("title","new message");
		          data.put("soundname","beep.wav");
				jsonObject.put("registration_ids",jsonArray);
				jsonObject.put("data",data);
				//jsonObject.put("notification",data);
				  
			    
			    System.out.println(jsonObject);
		      //建立StringEntity後使用Http Post送出JSON資料
		      httpPost.setEntity(new StringEntity(jsonObject.toString(),HTTP.UTF_8));
		      
		      
		      HttpResponse response_srv = closeableHttpClient.execute(httpPost); 
		      System.out.println(response_srv.toString());
		 /*     //取得回傳的JSON格式資料
		      String ss = EntityUtils.toString(response_srv.getEntity());
		      JSONObject jj = null;
				try {
					jj = new JSONObject(ss);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}         
		      System.out.println(jj);         
		         */
			}
			
			
		
		
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				System.out.println("ERROOOOOO");
				e.printStackTrace();
			}
			finally{
				
				
				
			}
	
			  }
		}
		
		
		
