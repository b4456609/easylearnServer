package ntou.easylearn.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class FileDownloadManerger
 */
@WebServlet("/download")
public class FileDownloadManerger extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileDownloadManerger() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String packId = request.getParameter("pack_id");
		String versionId = request.getParameter("version_id");
		String filename = request.getParameter("filename");
		
		String img64Base = encode(packId, versionId, filename);
		
		response.setContentType("text/plain");
		response.getWriter().write(img64Base);
	}
	
	public String encode( String packId, String versionId, String filename) {
		byte[] bytes = null;
		try {
			String path = "D:" + File.separator + "easylearn" + File.separator +  packId + File.separator + versionId + File.separator
					+ filename;

			File file = new File(path);
			InputStream is = new FileInputStream(file);
			 
		    long length = file.length();
		    if (length > Integer.MAX_VALUE) {
		        // File is too large
		    }
		    bytes = new byte[(int)length];
		    
		    is.read(bytes);
		    
		    String imgBase64 = Base64.getEncoder().encodeToString(bytes);
		    return imgBase64;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bytes.toString();
	}

}
