package servlet.global;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;

/**
 * Servlet Filter implementation class loginFilter
 */
@WebFilter(filterName = "loginFilter",urlPatterns = "/*")
public class loginFilter implements Filter {

    public loginFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code here
		// pass the request along the filter chain
		HttpServletRequest req = (HttpServletRequest)request;
		HttpSession session = req.getSession();
		response.setContentType("text/json; charset=utf-8");
		
		String uri = req.getRequestURI();
		
		if(session.getAttribute("login") != null && (boolean)session.getAttribute("login") == true) {
			chain.doFilter(request, response);
			return;
		}
		String whiteList[] = {
				"/login",
				"/regist"
		};
		for(String item:whiteList) {
			if(uri.contains(item)) {
				chain.doFilter(request, response);
				return;
			}
		}
		/* 提示未登录 */
		PrintWriter out = response.getWriter();
		JsonObject jsonobj = new JsonObject();
		jsonobj.addProperty("success", false);
		jsonobj.addProperty("msg", "用户未登录");
		jsonobj.addProperty("errorcode", 403);
		out = response.getWriter();
		out.println(jsonobj);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
	}

}
