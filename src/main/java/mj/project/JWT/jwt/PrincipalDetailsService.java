package mj.project.JWT.jwt;


import mj.project.JWT.model.UserMaster;
import mj.project.JWT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PrincipalDetailsService implements UserDetailsService{

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        System.out.println("PrincipalDetailsService의 loadUserByUsername탐");
        Optional<UserMaster> oUser = userRepository.findByEmail(email);

        UserMaster user = oUser
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email : " + email)
                );

        return PrincipalDetails.create(user);
    }
}
