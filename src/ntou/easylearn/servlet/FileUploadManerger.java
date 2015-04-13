package ntou.easylearn.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import sun.misc.BASE64Decoder;

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
		String filename = request.getParameter("filename");
		String imageString = request.getParameter("file");
		
		System.out.println("get upload img" + packId + versionId + filename);
		//System.out.println(imageString);
		
		byte[] btDataFile = new sun.misc.BASE64Decoder().decodeBuffer(imageString);
		File of = new File("D:\\"+filename);
		FileOutputStream osf = new FileOutputStream(of);
		osf.write(btDataFile);
		osf.flush();
    }
// //ImageIO.write(newImg, "png", new File("D:\\easylearn\\file\\" + packId + "\\" + versionId
//		//		+ "\\" + filename));
//		//System.out.println(newImg);
//		ImageIO.write(newImg, "jpg", new File("D:\\"+filename));
}
