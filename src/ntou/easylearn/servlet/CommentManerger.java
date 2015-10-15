package ntou.easylearn.servlet;

import java.io.IOException;

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
 * Servlet implementation class CommentHandler
 */
@WebServlet("/comment")
public class CommentManerger extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DBManerger db;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CommentManerger() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String clientOrigin = request.getHeader("origin");
		response.setHeader("Access-Control-Allow-Origin", clientOrigin);
		response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400");
		// prepare db
		db = new DBManerger();
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		String note_id = request.getParameter("note_id");
//        String lastest_create_time = request.getParameter("lastest_create_time");
//        System.out.println(note_id);
//        System.out.println(lastest_create_time);
//        long time = Long.valueOf(lastest_create_time).longValue();
        
        JSONArray comments = db.getComments(note_id);
        
        
        System.out.println(comments);
        
        response.setContentType("application/json");
		response.getWriter().write(comments.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String clientOrigin = request.getHeader("origin");
		response.setHeader("Access-Control-Allow-Origin", clientOrigin);
		response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400");
		// prepare db
		db = new DBManerger();
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		// get data from request
		String noteId = request.getParameter("noteId");
		String newComment = request.getParameter("newComment");
		
		System.out.println(noteId);
        System.out.println(newComment);

		try {
			JSONObject newCommentObj = new JSONObject(newComment);
			db.addComment(newCommentObj.getString("id"),
					newCommentObj.getString("content"),
					newCommentObj.getLong("create_time"), noteId,
					newCommentObj.getString("user_id"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
