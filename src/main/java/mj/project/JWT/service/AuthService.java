package mj.project.JWT.service;

import mj.project.JWT.model.UserMaster;
import mj.project.JWT.model.dto.SignUpRequestDto;
import mj.project.JWT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    @Qualifier("AU-UserRepository")
    private UserRepository userRepository;

    @Transactional
    public UserMaster singUp(SignUpRequestDto signUpRequestDto) {
            if (!emailDuplicateCheck(signUpRequestDto.getEmail())) {
                UserMaster userMaster = UserMaster.builder()
                        .email(signUpRequestDto.getEmail())
                        .role(signUpRequestDto.getRole())
                        .userUuid(UUID.randomUUID().toString())
                        .provider(signUpRequestDto.getProvider())
                        .providerId(signUpRequestDto.getProviderId())
                        .birthday(signUpRequestDto.getBirthday())
                        .build();
                userRepository.save(userMaster);
                return userMaster;
            }
        return null;
    }

    @Transactional(readOnly = true)
    public boolean emailDuplicateCheck(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public String deleteUser(String userUuid) {
        Optional<UserMaster> userMasterOptional = userRepository.findByUserUuid(userUuid);
        userRepository.delete(userMasterOptional.get());
        return "True";
    }
}
