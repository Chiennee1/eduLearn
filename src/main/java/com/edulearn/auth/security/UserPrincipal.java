package com.edulearn.auth.security;

import com.edulearn.auth.entity.Role;
import com.edulearn.auth.entity.User;
import com.edulearn.auth.entity.UserStatus;
import com.edulearn.common.Constants;
import java.util.Collection;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Builder
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final UserStatus status;
    private final Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal from(User user) {
        List<SimpleGrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(Role::getName)
                .map(Enum::name)
                .map(roleName -> new SimpleGrantedAuthority(Constants.ROLE_PREFIX + roleName))
                .toList();

        return UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPasswordHash())
                .status(user.getStatus())
                .authorities(authorities)
                .build();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.BANNED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }
}
