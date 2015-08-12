package ntou.easylearn.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntou.easylearn.model.DBManerger;

/**
 * Servlet implementation class DeviceManerger
 */
@WebServlet("/device")
public class DeviceManerger extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeviceManerger() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String clientOrigin = request.getHeader("origin");
		response.setHeader("Access-Control-Allow-Origin", clientOrigin);
		response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
				
		String userId = request.getParameter("user_id");
		String userDeviceId = request.getParameter("device_id");
		System.out.println("[DeviceManerger]" + userId + userDeviceId);
		
		DBManerger db = new DBManerger();
		String recordUserId = db.getUserByDiveceId(userDeviceId);
		if(recordUserId.equals("")){
			db.addDevice(userId, userDeviceId);
		}
		else if(recordUserId != userId){
			db.updateDiveceId(userId, userDeviceId);
		}
	}

}
