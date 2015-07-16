package ntou.easylearn.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntou.easylearn.model.DBManerger;

import org.json.JSONArray;

/**
 * Servlet implementation class SearchManerger
 */
@WebServlet("/search")
public class SearchManerger extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchManerger() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String clientOrigin = request.getHeader("origin");
		response.setHeader("Access-Control-Allow-Origin", clientOrigin);
		response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		String searchText = request.getParameter("search_text");
        System.out.println("search:"+searchText);
        

    	DBManerger db = new DBManerger();
    	
    	JSONArray search = db.search(searchText);
        
        System.out.println(search);
        
        response.setContentType("application/json");
        if(search != null)
        	response.getWriter().write(search.toString());
	}

}
