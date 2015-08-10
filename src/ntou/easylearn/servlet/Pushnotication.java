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
			
			System.out.println("name:"+Account.getString("name"));
			RegIDs=db.findDevice(Account.getString("name"));

			System.out.println("RegIDs"+RegIDs);	
			for(String RegID:RegIDs){                     
	              jsonArray.put(RegID);//register ID
	              System.out.println(RegID);
			}
			
		    HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		    CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
		    HttpPost httpPost = new HttpPost("https://gcm-http.googleapis.com/gcm/send");
		           //GCM API keys    
		           httpPost.addHeader("Authorization","key=AIzaSyB4hcjvpiAStqtjOqfVrAuDtIxm-NItaes");
		          //Content-Type application/json 
		          httpPost.addHeader("Content-Type","application/json;");
		          httpPost.addHeader("charset", "UTF-8"); 
		          // create json to put data
		          JSONObject jsonObject = new JSONObject();   //JSON
		          JSONObject data = new JSONObject();
		          
		          //Data to pass 
		          data.put("message","有人與你分享懶人包");
		          data.put("title","new message");
		          data.put("soundname","beep.wav");
		          data.put("packId",users.get("pack").toString());
		          data.put("userName",users.get("userName").toString());
		          
				jsonObject.put("registration_ids",jsonArray);
				jsonObject.put("collapse_key",users.get("pack").toString());
				jsonObject.put("data",data);
				//jsonObject.put("notification",data);
				  
			    
			    System.out.println(jsonObject);
		      //String Entity utf-8
		      httpPost.setEntity(new StringEntity(jsonObject.toString(),HTTP.UTF_8));
		      
		      
		      HttpResponse response_srv = closeableHttpClient.execute(httpPost); 
		      System.out.println(response_srv.toString());
		 /*    json response
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
		
		
		
