package mj.project.JWT.jwt;

import mj.project.JWT.model.UserMaster;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PrincipalDetails implements  UserDetails {

    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
    private String userUuid;
    private Collection<? extends GrantedAuthority> authorities;

    public PrincipalDetails(String email,  String userUuid,  Collection<? extends GrantedAuthority> authorities) {
        this.email = email;
        this.authorities = authorities;
        this.userUuid = userUuid;
    }

    public static PrincipalDetails create(UserMaster user) {
        List<GrantedAuthority> authorities = Collections.
                singletonList(new SimpleGrantedAuthority("ROLE_"+user.getRole()));

        return new PrincipalDetails(
                user.getEmail(),
                user.getUserUuid(),
                authorities
        );
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }


    public String getUserUuid() { return userUuid;}

}