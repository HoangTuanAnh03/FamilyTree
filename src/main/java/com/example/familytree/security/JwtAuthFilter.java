package com.example.familytree.security;

import com.example.familytree.entities.KeyTokenEntity;
import com.example.familytree.entities.UserAccountEntity;
import com.example.familytree.repositories.KeyRepository;
import com.example.familytree.repositories.UserAccountRepository;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private JwtService jwtService;

    private UserInfoUserDetailsService userDetailsService;
    private KeyRepository keyRepository;
    private UserAccountRepository userAccountRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;


        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            Base64.Decoder decoder = Base64.getUrlDecoder();
            String[] chunks = token.split("\\.");
            String payload = new String(decoder.decode(chunks[1]));
            JsonObject jsonObject = new JsonParser().parse(payload).getAsJsonObject();

            username = jsonObject.get("sub").getAsString();
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UserAccountEntity user = userAccountRepository.findFirstByUserEmail(username);
            KeyTokenEntity keyByUser = keyRepository.findFirstByUserId(user.getUserId());
            if (jwtService.validateToken(token,keyByUser.getPublicKey(), userDetails) ) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
