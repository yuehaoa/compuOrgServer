package servlet.rbac;


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
 * Servlet implementation class login
 */
@WebServlet("/api/usermanage/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private BaseDao dao = new BaseDao();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		Connection conn = null;
		HttpSession session = request.getSession();
		//HttpSession session = request.getSession();
		try {
			//Class.forName("com.mysql.cj.jdbc.Driver");
			//conn=DriverManager.getConnection("jdbc:mysql://47.115.31.88:3306/compuOrg?useSSL=false&serverTimezone=GMT","root","root");
			conn = dao.getConnection();
			Statement stmt = conn.createStatement();
			// ServletInputStream is;
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
				String userName = jsonObj.getString("userName");
				String password = jsonObj.getString("password");
				String sql = "select * from user where userName=? and password=?";
				String sql2 = "select * from role, user_role where userId = ? and user_role.roleId = role.roleId";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, userName);
				ps.setString(2, password);
				ResultSet rs = ps.executeQuery();
				JSONObject jsonobj = new JSONObject();
				JSONArray array = new JSONArray();
				if(rs.next()){
					jsonobj.put("success",true);
					String userId = rs.getString("userId");
					PreparedStatement ps2 = conn.prepareStatement(sql2);
					ps2.setString(1, userId);
					ResultSet rs2 = ps2.executeQuery();
					while(rs2.next()) {
						String roleName = rs2.getString("roleName");
						array.add(roleName);
					}
					String sessionId = session.getId();
					session.setAttribute("userId", userId);
					session.setAttribute("login", true);
					jsonobj.put("sessionId",sessionId);
					jsonobj.put("roles", array);
					
				}
				else {
					jsonobj.put("success",false);
					jsonobj.put("msg", "用户名或密码错误");
				}
				out = response.getWriter();
				out.println(jsonobj);
				dao.closeAll(conn, stmt, rs);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
	}

}

