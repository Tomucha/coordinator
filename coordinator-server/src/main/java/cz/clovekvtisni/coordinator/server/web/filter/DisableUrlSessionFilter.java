package cz.clovekvtisni.coordinator.server.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * See: http://randomcoder.com/articles/jsessionid-considered-harmful
 *
 * @author Tomas Zverina <zverina@m-atelier.cz>
 */
public class DisableUrlSessionFilter implements Filter {
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		request.setCharacterEncoding("UTF-8");
		
		if (!(request instanceof HttpServletRequest)) {
			chain.doFilter(request, response);
			return;
		}

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		if (httpRequest.isRequestedSessionIdFromURL()) {
			HttpSession session = httpRequest.getSession();
			if (session != null) session.invalidate();
		}
		HttpServletResponseWrapper wrappedResponse = new HttpServletResponseWrapper(httpResponse) {
			public String encodeRedirectUrl(String url) { return url; }
			public String encodeRedirectURL(String url) { return url; }
			public String encodeUrl(String url) { return url; }
			public String encodeURL(String url) { return url; }
		};
		chain.doFilter(request, wrappedResponse);
	}

	public void init(FilterConfig config) throws ServletException {}

	public void destroy() {}

}
