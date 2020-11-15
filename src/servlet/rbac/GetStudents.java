package servlet.rbac;

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
 * Servlet implementation class GetStudents
 */
@WebServlet("/api/usermanage/getStudents")
public class GetStudents extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private BaseDao dao = new BaseDao();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetStudents() {
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
		// doGet(request, response);
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		Connection conn = null;
		ResultSet rs = null;
		try {
			//Class.forName("com.mysql.cj.jdbc.Driver");
			conn=dao.getConnection();//DriverManager.getConnection("jdbc:mysql://47.115.31.88:3306/compuOrg?useSSL=false&serverTimezone=GMT","root","root");
			Statement stmt = conn.createStatement();
			JSONObject result = new JSONObject();
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
				boolean finished  = false;
				if(jsonObj.has("finished"))  finished = true;
				String sql = "select * from role where roleName = 'student'";
				String sql2 = "select student.* from user_role, student, user_exp where roleId = ? "
						+ "and student.studentId = user_role.userId and user_role.userId = user_exp.userId and user_exp.finish = 1";
				String sql3 = "select student.*, user_exp.* from user_role, student, user_exp where roleId = ? "
						+ "and student.studentId = user_role.userId and user_role.userId = user_exp.userId and user_exp.finish = 0";
				rs = stmt.executeQuery(sql);
				JSONArray data = new JSONArray();
				if(rs.next()) {
					String roleId = rs.getString("roleId");
					PreparedStatement ps2 = null;
					if(!finished)
					ps2 = conn.prepareStatement(sql2);
					else ps2 = conn.prepareStatement(sql3);
					ps2.setString(1, roleId);
					ResultSet rs2 = ps2.executeQuery();
					while(rs2.next()) {
						String userId = rs2.getString("studentId");
						String userName = rs2.getString("name");
						String className = rs2.getString("classname");
						String grade = rs2.getString("grade");
						double score = rs2.getDouble("score");
						JSONObject temp = new JSONObject();
						temp.put("studentId", userId);
						temp.put("name", userName);
						temp.put("classname", className);
						temp.put("grade", grade);
						temp.put("score", score);
						data.add(temp);
					}
					rs2.close();
				}
				result.put("success", true);
				result.put("students", data);
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
