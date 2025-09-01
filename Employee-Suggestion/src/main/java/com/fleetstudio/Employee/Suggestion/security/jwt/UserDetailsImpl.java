package com.fleetstudio.Employee.Suggestion.security.jwt;


import com.fleetstudio.Employee.Suggestion.model.Employee;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    // Constructor used when creating a UserDetailsImpl from a User object
    public UserDetailsImpl(Collection<? extends GrantedAuthority> authorities, String email, Long id, String password, String username) {
        this.authorities = authorities;
        this.email = email;
        this.id = id;
        this.password = password;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    // Static method to build UserDetailsImpl from User object
    public static UserDetailsImpl build(Employee user) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getRole());
        return new UserDetailsImpl(
                Collections.singletonList(grantedAuthority),
                user.getEmail(),
                user.getId(),
                user.getPassword(),
                user.getName()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }


    @Override
    public String getUsername() {
        return email;
    }

    public UserDetailsImpl setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
        return this;
    }


    public UserDetailsImpl setEmail(String email) {
        this.email = email;
        return this;
    }



    public UserDetailsImpl setId(Long id) {
        this.id = id;
        return this;
    }

    public UserDetailsImpl setPassword(String password) {
        this.password = password;
        return this;
    }

    public UserDetailsImpl setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }


}
