package servlet.SignIn;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;
/**
 * servlet implementation class SignIn
 */
@WebServlet("/api/usermanage/SignInOrOut")
public class SignInOrOut extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SignInOrOut() {
        super();
        // TODO Auto-generated constructor stub
       
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
    	response.setHeader("Allow", "POST");
    	response.sendError(405);
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
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn=DriverManager.getConnection("jdbc:mysql://47.115.31.88:3306/compuOrg?useSSL=false&serverTimezone=GMT","root","root");
			Statement stmt = conn.createStatement();
			String studentId = (String) session.getAttribute("userId");
			String signInOrOut = jsonObj.getString("signState"); // "SignInTime" for signIn and "SignOutTime" for signOut
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        String dateTime = df.format(new Date());
	        String sql = "insert into user_sign(studentId, signInTime, signOutTime) values(?,?,?)";
	        String sql2 = "update user_sign set signOutTime = ? where studentId = ?";
	        PreparedStatement ps = null;
	        if(signInOrOut.compareTo("signInTime")==0) {
	        	ps = conn.prepareStatement(sql);
	        	ps.setString(1, studentId);
	        	ps.setString(2, dateTime);
	        	ps.setString(3, "");
	        }
	        else if(signInOrOut.compareTo("signOutTime")==0) {
	        	ps = conn.prepareStatement(sql2);
	        	ps.setString(2, studentId);
	        	ps.setString(1, dateTime);
	        }								
			try {
				int rowCount = ps.executeUpdate();
				JSONObject jsonobj = new JSONObject();
				if(rowCount>0){
					jsonobj.put("success", true);
					jsonobj.put("msg","sign successfully");
				}
				out = response.getWriter();
				out.println(jsonobj);
				stmt.close();
				conn.close();
			}
			catch(Exception e) {
				JSONObject jsonobj = new JSONObject();
				jsonobj.put("success", false);
				jsonobj.put("msg", e.getMessage());
				out = response.getWriter();
				out.println(jsonobj);
				stmt.close();
				conn.close();
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
