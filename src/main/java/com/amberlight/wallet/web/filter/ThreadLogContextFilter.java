package com.amberlight.wallet.web.filter;


import com.amberlight.wallet.util.HttpUtil;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Filter for tagging each request with data related.
 */
public class ThreadLogContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        ThreadContext.put("RID", requestId);
        ThreadContext.put("IP", request.getRemoteAddr());
        ThreadContext.put("HOST", request.getServerName() + ":" + request.getServerPort());
        response.addHeader(HttpUtil.HEADER_X_REQUEST_ID, requestId);
        filterChain.doFilter(request, response);
        ThreadContext.clearAll();
    }

}
