package servlet.global;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

public class BaseDao {
	private static String driver = "com.mysql.cj.jdbc.Driver";
	private static String url = "jdbc:mysql://47.115.31.88:3306/compuOrg?useSSL=false&serverTimezone=GMT";
	private static String user = "root";
	private static String password = "root";
	
	 static {
         try {
             Class.forName(driver);
         } catch (ClassNotFoundException e) {
             e.printStackTrace();
         }
     }
	 
	public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);    
    }
	
   public static void closeAll(Connection conn,Statement stmt,ResultSet rs) throws SQLException {
        if(rs!=null) {
            rs.close();
        }
        if(stmt!=null) {
            stmt.close();
        }
        if(conn!=null) {
            conn.close();
        }
    }
   public String readRequest(HttpServletRequest request) throws IOException {
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
		return str;
   }
   public int executeSQL(String preparedSql, Object[] param) throws ClassNotFoundException {
       Connection conn = null;
       PreparedStatement pstmt = null;
       /* ����SQL,ִ��SQL */
       try {
           conn = getConnection(); // �õ����ݿ�����
           pstmt = conn.prepareStatement(preparedSql); // �õ�PreparedStatement����
           if (param != null) {
               for (int i = 0; i < param.length; i++) {
                   pstmt.setObject(i + 1, param[i]); // ΪԤ����sql���ò���
               }
           }
       ResultSet num = pstmt.executeQuery(); // ִ��SQL���
       } catch (SQLException e) {
           e.printStackTrace(); // ����SQLException�쳣
       } finally {
           try {
               BaseDao.closeAll(conn, pstmt, null);
           } catch (SQLException e) {    
               e.printStackTrace();
           }
       }
       return 0;
   }
}
