package io.github.company;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CORSFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpResp = (HttpServletResponse) response;
        httpResp.addHeader("Access-Control-Allow-Origin", "*");
        httpResp.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        HttpServletRequest httpReq = (HttpServletRequest) request;
        if (httpReq.getMethod().equals("OPTIONS")) {
            httpResp.setStatus(HttpServletResponse.SC_ACCEPTED);
            return;
        }

        chain.doFilter(httpReq, response);
    }

}
