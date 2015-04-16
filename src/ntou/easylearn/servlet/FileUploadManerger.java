package ntou.easylearn.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Base64;

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
		String img = request.getParameter("file");
		String filename = request.getParameter("filename");

		int start = img.indexOf(',');
		img = img.substring(start + 1);

		decode(img, packId, filename);
	}

	private void decode(String img, String packId, String filename) {
		try {
			// Note preferred way of declaring an array variable
			byte[] data = Base64.getDecoder().decode(img);

			String path = "D:" + File.separator + "easylearn" + File.separator
					+ packId + File.separator + filename;
			// (use relative path for Unix systems)
			File f = new File(path);
			// (works for both Windows and Linux)
			f.getParentFile().mkdirs();
			f.createNewFile();

			OutputStream stream = new FileOutputStream(path);
			stream.write(data);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
