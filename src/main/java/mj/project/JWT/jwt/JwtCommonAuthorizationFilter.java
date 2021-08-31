package mj.project.JWT.jwt;



import io.jsonwebtoken.ExpiredJwtException;
import mj.project.JWT.model.UserMaster;
import mj.project.JWT.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class JwtCommonAuthorizationFilter extends BasicAuthenticationFilter {
    private JwtTokenUtil jwtTokenUtil;

    private UserRepository userRepository;


    public JwtCommonAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, JwtTokenUtil jwtTokenUtil) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Read the Authorization header, where the JWT token should be
        String header = request.getHeader("Authorization");

        // If header does not contain BEARER or is null delegate to Spring impl and exit
        if (header == null || !header.startsWith("Bearer")) {

            logger.info("header가 null이 아니거나 Bearer type이 아닙니다"+header);
            chain.doFilter(request, response);
            return;
        }
        // If header is present, try grab user principal from database and perform authorization
        Authentication authentication = getUsernamePasswordAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Continue filter execution
        chain.doFilter(request, response);
    }

    private Authentication getUsernamePasswordAuthentication(HttpServletRequest request) {
        String email = null;
        String token = request.getHeader("Authorization")
                .replace("Bearer ",""); //Bear 다음에 한칸 뛰어야한다
        if (token != null) {
            try {
                Map<String, Object> parseInfo = jwtTokenUtil.getUserParseInfo(token);
                email = (String)parseInfo.get("email");
            } catch (ExpiredJwtException e) {
            }
            if (email != null) {
                Optional<UserMaster> userMasterOptional = userRepository.findByEmail(email);
                UserMaster userMaster = userMasterOptional.get();
                PrincipalDetails principalDetails = PrincipalDetails.create(userMaster);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
                return authentication;
            } else {
                logger.info("token maybe expired: email is null.");
            }
        }
        return null;
    }
}