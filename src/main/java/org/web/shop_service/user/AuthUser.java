package org.web.shop_service.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public class AuthUser implements UserDetails {
    private final Long id;
    private final String username;
    private final List<? extends GrantedAuthority> authorities;

    public AuthUser(Long id, String username,
                    List<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.authorities = authorities;
    }

    public AuthUser(User user) {
        this(user.getId(), user.getUsername(),
                List.of(new SimpleGrantedAuthority(user.getRole().getName())));
    }

    public Long getId() {
        return id;
    }

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return "";
    }
}
