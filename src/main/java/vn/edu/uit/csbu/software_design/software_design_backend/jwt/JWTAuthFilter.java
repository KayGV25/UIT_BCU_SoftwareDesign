package vn.edu.uit.csbu.software_design.software_design_backend.jwt;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The `JWTAuthFilter` class in Java is a component that filters requests to ensure they have a valid
 * JWT token for paths starting with "/auth/".
 */
@Component
public class JWTAuthFilter extends OncePerRequestFilter {
    
    /**
     * This Java function is a filter that checks for a valid Bearer token in the Authorization header
     * for requests to "/auth/" paths.
     * 
     * @param request The `request` parameter in the `doFilterInternal` method represents the HTTP
     * request that the filter is processing. It contains information about the request such as
     * headers, parameters, and the request URI. In the provided code snippet, the filter checks the
     * Authorization header in the request to validate a JWT token
     * @param response The `response` parameter in the `doFilterInternal` method represents the HTTP
     * response that will be sent back to the client. It allows you to set response headers, status
     * codes, and write content back to the client. In the provided code snippet, the response is used
     * to handle authentication and authorization
     * @param filterChain The `filterChain` parameter in the `doFilterInternal` method is an object
     * that represents a chain of filters to be applied to a request for a resource. When a request is
     * made to a servlet, the servlet container passes the request and response objects through the
     * filter chain before reaching the servlet.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Extract the Authorization header
        String authHeader = request.getHeader("Authorization");
        String token="";
        if(authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7); // Extract token after "Bearer "
        }

        // Ensure the filter is only applied to "/auth/" paths
        String requestURI = request.getRequestURI();
        if (!requestURI.contains("/auth/")) {
            if(!token.isBlank() && !JWTUtil.validateToken(token)){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: Invalid token");
                return;
            }
            filterChain.doFilter(request, response);
            return; // Skip filtering for other paths
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Missing or invalid token");
            return;
        }


        // Validate the token
        if (!JWTUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid token");
            return;
        }

        // // Extract username and log it (optional)
        // String Name = JWTUtil.extractName(token);
        // System.out.println("Authenticated user: " + Name);

        // Proceed with the next filter or controller
        filterChain.doFilter(request, response);
    }
}
