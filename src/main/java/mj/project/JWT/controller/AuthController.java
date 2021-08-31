package mj.project.JWT.controller;

import lombok.RequiredArgsConstructor;
import mj.project.JWT.jwt.JwtTokenUtil;
import mj.project.JWT.jwt.PrincipalDetails;
import mj.project.JWT.jwt.RefreshToken;
import mj.project.JWT.model.UserMaster;
import mj.project.JWT.model.dto.AuthProvider;
import mj.project.JWT.model.dto.CMRespDto;
import mj.project.JWT.model.dto.SignUpRequestDto;
import mj.project.JWT.repository.UserRepository;
import mj.project.JWT.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    @Qualifier("AU-UserRepository")
    private UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthService authService;
    public static final long JWT_ACCESS_TOKEN_VALIDITY = 10* 60; //10분
    public static final long JWT_REFRESH_TOKEN_VALIDITY = 24 * 60 * 60 * 7; //일주일
    @PostMapping("/login")
    public CMRespDto<?> jwtCreate(@RequestBody Map<String, Object> data) {
        Optional<UserMaster> userMasterOptional =
                userRepository.findByProviderIdAndProvider((String) data.get("providerId"), AuthProvider.valueOf((String) data.get("provider")));
        System.out.println(userMasterOptional.get());
        if (userMasterOptional.isPresent()) {
            UserMaster userMaster = userMasterOptional.get();
            PrincipalDetails principal = PrincipalDetails.create(userMaster);

            String jwtToken = jwtTokenUtil.generateAccessToken(principal);
            String refreshToken = jwtTokenUtil.generateRefreshToken(userMaster.getEmail());

            RefreshToken refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setEmail(userMaster.getEmail());
            refreshTokenEntity.setRefreshToken(refreshToken);
            ValueOperations<String, Object> vop = redisTemplate.opsForValue();
            vop.set(userMaster.getEmail(), refreshTokenEntity,JWT_REFRESH_TOKEN_VALIDITY, TimeUnit.SECONDS);

            Map<String,String> info = new HashMap<>();
            info.put("jwtToken",jwtToken);
            info.put("email",userMaster.getEmail());
            info.put("refreshToken", refreshToken);

            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat sdf;
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            sdf.setTimeZone(TimeZone.getTimeZone("KST"));
            String text = sdf.format(date);


            return new CMRespDto<>(200, "jwt 반환", info);
        }
        return new CMRespDto<>(201, "회원 가입이 안된 유저",null);
    }

    @PostMapping("/signup")
    @Transactional
    public CMRespDto<?> signup(@RequestBody SignUpRequestDto signUpRequestDto){
        UserMaster userMaster = authService.singUp(signUpRequestDto);
        if (userMaster == null) {
            return new CMRespDto<> (201 , "회원가입 불가능","이메일이나 닉네임 중복 회원이 존재합니다.");
        }
        PrincipalDetails principal = PrincipalDetails.create(userMaster);
        String jwtToken = jwtTokenUtil.generateAccessToken(principal);
        return new CMRespDto<> (200 , "회원가입 완료",jwtToken);
    }

    @GetMapping("/user/delete")
    @Transactional
    public CMRespDto<?> deleteUser(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        String result = authService.deleteUser(principalDetails.getUserUuid());
        if (result == "True") {
            return new CMRespDto<>(200,"성공",result);
        }
        return new CMRespDto<>(400,"실패",result);
    }

}


