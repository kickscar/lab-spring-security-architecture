package filter;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FilterEx01 implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);

        Cookie cookie = new Cookie("FilterEx01", "Works");
        cookie.setPath(((HttpServletRequest)request).getContextPath());
        cookie.setMaxAge(60);

        ((HttpServletResponse)response).addCookie(cookie);
    }
}
