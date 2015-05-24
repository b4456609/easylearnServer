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
 * Servlet implementation class PackManerger
 */
@WebServlet("/pack")
public class PackManerger extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PackManerger() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String packId = request.getParameter("pack_id");
		String userId = request.getParameter("user_id");

		DBManerger db = new DBManerger();

		// get pack by packId
		JSONObject pack = db.getPack(packId);

		// get pack's version jsonArray by userId , version_pack_id in
		// user_has_version and version
		JSONArray version = db.getPacksVersion(packId);
		try {
			for (int j = 0; j < version.length(); j++) {
				JSONObject versionItem = version.getJSONObject(j);
				versionItem.put("modified", false);
				String versionId = versionItem.getString("id");

				// get bookmark jsonArray by version and userid in bookmark
				// add bookmark jsonArray to Version
				versionItem.put("bookmark", db.getBookmark(userId, versionId));

				// get file jsonArray by version_id
				// add file jsonArray to Version
				versionItem.put("file", db.getFile(versionId));

				// get notes jsonArray by version_id from version_has_note and
				// note
				JSONArray notes = db.getNotes(versionId);
				for (int k = 0; k < notes.length(); k++) {
					// get comment jsonArray by noteid in comment
					String noteId = notes.getJSONObject(k).getString("id");
					notes.getJSONObject(k).put("comment",
							db.getComments(noteId));
					System.out.println(noteId + "   " + db.getComments(noteId));
				}
				// System.out.println(notes.length());
				// put notes in version
				versionItem.put("note", notes);

			}
			// put version in pack
			pack.put("version", version);
			// remove pack id
			pack.remove("id");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			response.setContentType("application/json");
			response.getWriter().write(pack.toString());
			System.out.println(pack.toString());
		}
	}
}
