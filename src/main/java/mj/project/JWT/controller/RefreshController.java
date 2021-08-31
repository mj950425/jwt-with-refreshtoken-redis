package mj.project.JWT.controller;


import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import mj.project.JWT.jwt.JwtTokenUtil;
import mj.project.JWT.jwt.PrincipalDetails;
import mj.project.JWT.jwt.PrincipalDetailsService;
import mj.project.JWT.jwt.RefreshToken;
import mj.project.JWT.model.UserMaster;
import mj.project.JWT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
public class RefreshController {
    @Autowired
    @Qualifier("AU-UserRepository")
    private UserRepository userRepository;
    @Autowired
    PrincipalDetailsService principalDetailsService;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @PostMapping(path="/newuser/refresh")

    public Map<String, Object> requestForNewAccessToken(@RequestBody Map<String, String> m) {
        String accessToken = null;
        String refreshToken = null;
        String refreshTokenFromDb = null;
        String email = null;
        Map<String, Object> map = new HashMap<>();
        try {
            accessToken = m.get("accessToken");
            refreshToken = m.get("refreshToken");
            try {
                email = jwtTokenUtil.getEmailFromToken(accessToken);
            } catch (IllegalArgumentException e) {

            } catch (ExpiredJwtException e) { //expire됐을 때
                email = e.getClaims().getSubject();
                log.info("email from expired access token: " + email);
            }
            if (refreshToken != null) { //refresh를 같이 보냈으면.
                try {
                    ValueOperations<String, Object> vop = redisTemplate.opsForValue();
                    RefreshToken result = (RefreshToken) vop.get(email);
                    refreshTokenFromDb = result.getRefreshToken();
                    log.info("rtfrom db: " + refreshTokenFromDb);
                } catch (IllegalArgumentException e) {
                    log.warn("illegal argument!!");
                }
                //둘이 일치하고 만료도 안됐으면 재발급 해주기.
                if (refreshToken.equals(refreshTokenFromDb) && !jwtTokenUtil.isTokenExpired(refreshToken)) {
                    Optional<UserMaster> oUser = userRepository.findByEmail(email);
                    UserMaster user = oUser.get();
                    PrincipalDetails principal = PrincipalDetails.create(user);
                    String newToken =  jwtTokenUtil.generateAccessToken(principal);
                    map.put("success", true);
                    map.put("accessToken", newToken);
                } else {
                    map.put("success", false);
                    map.put("msg", "refresh token is expired.");
                }
            } else { //refresh token이 없으면
                map.put("success", false);
                map.put("msg", "your refresh token does not exist.");
            }
        } catch (Exception e) {
            throw e;
        }
        log.info("m: " + m);
        return map;
    }
}
