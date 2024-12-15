package vn.edu.uit.csbu.software_design.software_design_backend.jwt;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Extract the Authorization header
        String authHeader = request.getHeader("Authorization");

        // Ensure the filter is only applied to "/auth/" paths
        String requestURI = request.getRequestURI();
        if (!requestURI.contains("/auth/")) {
            filterChain.doFilter(request, response);
            return; // Skip filtering for other paths
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Missing or invalid token");
            return;
        }

        String token = authHeader.substring(7); // Extract token after "Bearer "

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
