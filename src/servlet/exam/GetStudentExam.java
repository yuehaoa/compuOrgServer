package servlet.exam;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import servlet.global.BaseDao;

/**
 * Servlet implementation class GetStudentExam
 */
@WebServlet("/api/exammanage/getStudentExam")
public class GetStudentExam extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private BaseDao dao = new BaseDao();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetStudentExam() {
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
		//doGet(request, response);
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		Connection conn = null;
		ResultSet rs = null;
		try {
			//Class.forName("com.mysql.cj.jdbc.Driver");
			conn=dao.getConnection();//DriverManager.getConnection("jdbc:mysql://47.115.31.88:3306/compuOrg?useSSL=false&serverTimezone=GMT","root","root");
			Statement stmt = conn.createStatement();
			//ServletInputStream is;
			JSONObject result = new JSONObject();
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
				String studentId = jsonObj.getString("studentId");
				String expID = jsonObj.getString("expID");
				String sql = "select * from exp_exa_user where userId = ? and expID = ?";
				String sql2 = "select title from exam where examID = ?";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, studentId);
				ps.setString(2, expID);
				rs = ps.executeQuery();
				JSONArray data = new JSONArray();
				while(rs.next()) {
					String examID = rs.getString("examID");
					String answer = rs.getString("answer");
					String score = rs.getString("score");
					String comment = rs.getString("comment");
					PreparedStatement ps2 = conn.prepareStatement(sql2);
					ps2.setString(1,examID);
					ResultSet rs2 = ps2.executeQuery();
					String title = "";
					if(rs2.next()) {
						title += rs2.getString("title");
					}
					JSONObject temp = new JSONObject();
					temp.put("title",title);
					temp.put("examID", examID);
					temp.put("answer", answer);
					temp.put("score", score);
					temp.put("comment", comment);
					data.add(temp);
				}
				result.put("success", true);
				result.put("data", data);
			}
			catch(Exception e) {
				result.put("success", false);
				result.put("msg",e.getMessage());
			}
			out = response.getWriter();
			out.println(result);
			dao.closeAll(conn, stmt, rs);
		}
		catch(Exception e) {
			
		}
	}

}
