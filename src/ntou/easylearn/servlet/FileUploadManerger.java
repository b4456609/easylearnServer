package ntou.easylearn.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Servlet implementation class FileUploadManerger
 */
@WebServlet("/upload")
public class FileUploadManerger extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileUploadManerger() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String packId = request.getParameter("pack_id");
		String versionId = request.getParameter("version_id");
		Part filePart = request.getPart("file");
		String fileName = filePart.getSubmittedFileName();
		InputStream fileContent = filePart.getInputStream();

		File file = new File("D:/easylearn/file/" + packId + "/" + versionId
				+ "/", fileName);

		Files.copy(fileContent, file.toPath()); // How to obtain part is answered in
											// http://stackoverflow.com/a/2424824

	}

}
