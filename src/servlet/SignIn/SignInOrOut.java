package servlet;

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
			System.out.println(str);
			JSONObject jsonObj = JSONObject.fromObject(str);
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/compuorg?useSSL=false&serverTimezone=GMT","root","mysql2020**");
			Statement stmt = conn.createStatement();
			String mySno = jsonObj.getString("myStudentNo");
			String signInOrOut = jsonObj.getString("myColumn"); // "SignInTime" for signIn and "SignOutTime" for signOut
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        String dateTime = df.format(new Date());
	        
			String sql = "UPDATE SignIn SET " + signInOrOut + " = '" +  dateTime + "' WHERE Sno = '" + mySno + "'";
			System.out.println(sql);
			PreparedStatement ps = conn.prepareStatement(sql);
			try {
				int rowCount = ps.executeUpdate();
				JSONObject jsonobj = new JSONObject();
				if(rowCount>0){
					jsonobj.put("success", true);
					jsonobj.put("msg","签到或签退成功");
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
