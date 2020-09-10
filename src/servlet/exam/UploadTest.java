package servlet.exam;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import servlet.global.BaseDao;

/**
 * Servlet implementation class UploadTest
 */
@WebServlet("/api/exammanage/uploadTest")
public class UploadTest extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private BaseDao dao  =new BaseDao();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadTest() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		Connection conn = null;
		Statement stmt = null;
		HttpSession session  = request.getSession();
		JSONObject result = new JSONObject();
		ResultSet rs = null;
		//HttpSession session = request.getSession();
		try {
			//Class.forName("com.mysql.cj.jdbc.Driver");
			conn=dao.getConnection();//DriverManager.getConnection("jdbc:mysql://47.115.31.88:3306/compuOrg?useSSL=false&serverTimezone=GMT","root","root");
			stmt = conn.createStatement();
			//ServletInputStream is;
			try {
				/*is = request.getInputStream();
				int nRead = 1;
				int nTotalRead = 0;
				byte[] bytes = new byte[10240];
				while (nRead > 0) {
					nRead = is.read(bytes, nTotalRead, bytes.length - nTotalRead);
					if (nRead > 0)
						nTotalRead = nTotalRead + nRead;
				}*/
				String str = dao.readRequest(request);//new String(bytes, 0, nTotalRead, "utf-8");
				JSONObject jsonObj = JSONObject.fromObject(str);
				String expID = jsonObj.getString("expID");
				String userId = (String) session.getAttribute("userId");
				String sql = "select roleName from role, user_role where userId = ? and role.roleId = user_role.roleId";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, userId);
				boolean isTeacher = false;
				rs = ps.executeQuery();
				while(rs.next()) {
					String roleName = rs.getString("roleName");
					if(roleName.compareTo("teacher")==0) {
						isTeacher = true;
						break;
					}
				}
				if(!isTeacher) {
					JSONArray exams = jsonObj.getJSONArray("exams");
					String sql2 = "insert into exp_exa_user(userId, examID, expID,answer) values(?,?,?,?)";
					String sql3 = "insert user_exp(userId, expID, score,finish) values(?,?,?,?)";
					for(int i=0;i<exams.size();i++) {
						String answer = exams.getJSONObject(i).getString("answer");
						String examID = exams.getJSONObject(i).getString("examID");
						PreparedStatement ps2 = conn.prepareStatement(sql2);
						ps2.setString(1, userId);
						ps2.setString(2,examID);
						ps2.setString(3, expID);
						ps2.setString(4,answer);
						ps2.executeUpdate();					
					}
					PreparedStatement ps3 = conn.prepareStatement(sql3);
					ps3.setString(1, userId);
					ps3.setString(2, expID);
					ps3.setDouble(3, 0);
					ps3.setInt(4, 1);
					ps3.executeUpdate();
				}
				else {
					String studentId = jsonObj.getString("studentId");
					double score = jsonObj.getDouble("totalScore");
					String sql3 = "update user_exp set score = ?, finish = ? where userId = ? and expID = ?";
					PreparedStatement ps3 = conn.prepareStatement(sql3);
					ps3.setString(3, studentId);
					ps3.setString(4,expID);
					ps3.setInt(2, 0);
					ps3.setDouble(1, score);
					ps3.executeUpdate();
					JSONArray exams = jsonObj.getJSONArray("exams");
					String sql4 = "update exp_exa_user set score = ?, comment = ? where userId = ? and expID = ? and examID = ?";
					for(int i=0;i<exams.size();i++) {
						String comment = "";
						String score2 = "";
						if(exams.getJSONObject(i).has("comment"))
						comment = exams.getJSONObject(i).getString("comment");
						else comment = "";
						if(exams.getJSONObject(i).has("score"))
						score2 = exams.getJSONObject(i).getString("score");
						else score2 = "";
						String examID = exams.getJSONObject(i).getString("examID");
						PreparedStatement ps4 = conn.prepareStatement(sql4);
						ps4.setString(1, score2);
						ps4.setString(2,comment);
						ps4.setString(3,studentId);
						ps4.setString(4, expID);
						ps4.setString(5,examID);
						int rowCount = ps4.executeUpdate();
					}
				}
				result.put("success",true);
				result.put("msg", "upload successful");
				
			}
			catch(Exception e) {
				result.put("success",false);
				result.put("msg", e.getMessage());
				e.printStackTrace();
			}
			out = response.getWriter();
			out.println(result);
			dao.closeAll(conn, stmt, rs);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
