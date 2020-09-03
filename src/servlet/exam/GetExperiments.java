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

/**
 * Servlet implementation class GetExperiments
 */
@WebServlet("/api/exammanage/getExperiments")
public class GetExperiments extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetExperiments() {
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
		JSONObject jsonobj = new JSONObject();
		HttpSession session = request.getSession();
		try {
			ServletInputStream is = request.getInputStream();
			int nRead = 1;
			int nTotalRead = 0;
			byte[] bytes = new byte[10240];
			while (nRead > 0) {
				nRead = is.read(bytes, nTotalRead, bytes.length - nTotalRead);
				if (nRead > 0)
					nTotalRead = nTotalRead + nRead;
			}
			String str = new String(bytes, 0, nTotalRead, "utf-8");
			JSONObject jsonObj = JSONObject.fromObject(str);
			String studentId = (String) session.getAttribute("userId");
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://47.115.31.88:3306/compuorg?useSSL=false&serverTimezone=GMT","root","root");
			Statement stmt = conn.createStatement();
			boolean finish = false;
			if(jsonObj.has("finished")) finish = true;
			String sql = "select experiment.* from user_exp, experiment where user_exp.userId = ? and user_exp.expID = experiment.experID and finish = 1";
			String sql2 = "select experiment.*, score from user_exp, experiment where user_exp.userId = ? and user_exp.expID = experiment.experID and finish = 0";
			PreparedStatement ps = null;//conn.prepareStatement(sql);
			if(!finish) ps = conn.prepareStatement(sql);
			else ps = conn.prepareStatement(sql2);
			ps.setString(1, studentId);
			ResultSet rs = ps.executeQuery();
			JSONArray array = new JSONArray();
			while(rs.next()) {
				JSONObject temp = new JSONObject();
				String examID = rs.getString("experID");
				String title = rs.getString("name");
				if(finish) {
					double score = rs.getDouble("score");
					temp.put("score", score);
				}
				temp.put("experID",examID);
				temp.put("name",title);
				array.add(temp);
			}
			jsonobj.put("data",array);
			jsonobj.put("studentId",studentId);
			jsonobj.put("success",true);
			rs.close();
			stmt.close();
		}
		catch(Exception e) {
			jsonobj.put("success",false);
			jsonobj.put("msg",e.getMessage());
		}
		out = response.getWriter();
		out.println(jsonobj);
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
