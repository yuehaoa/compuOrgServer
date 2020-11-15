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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import servlet.global.BaseDao;


/**
 * Servlet implementation class GenerateExam
 */
@WebServlet("/api/exammanage/generateExam")
public class GenerateExam extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private BaseDao dao  = new BaseDao();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GenerateExam() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
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
		ResultSet rs = null;
		JSONObject jsonobj = new JSONObject();
		try {
			/*ServletInputStream is = request.getInputStream();
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
			if(!jsonObj.has("count")) {
				jsonObj.put("count", 5);
			}
			String flag = jsonObj.getString("flag");
			int count = jsonObj.getInt("count");
			//Class.forName("com.mysql.cj.jdbc.Driver");
			conn = dao.getConnection();//DriverManager.getConnection("jdbc:mysql://47.115.31.88:3306/compuorg?useSSL=false&serverTimezone=GMT","root","root");
			stmt = conn.createStatement();
			String sql = "select * from exam where flag = ? order by rand() limit "+count;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, flag);
			rs = ps.executeQuery();
			JSONArray array = new JSONArray();
			while(rs.next()) {
				JSONObject temp = new JSONObject();
				String examID = rs.getString("examID");
				String title = rs.getString("title");
				temp.put("examID",examID);
				temp.put("title",title);
				array.add(temp);
			}
			jsonobj.put("data",array);
			jsonobj.put("success",true);
		}
		catch(Exception e) {
			jsonobj.put("success",false);
			jsonobj.put("msg",e.getMessage());
		}
		out = response.getWriter();
		out.println(jsonobj);
		try {
			dao.closeAll(conn, stmt, rs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
