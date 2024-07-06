package com.uca.project.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.*;

@Data
@Entity
@NoArgsConstructor
@Table
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID code;
    private String username;
    private String email;
    private String dui;
    private String pictureurl;
    private boolean active;

    @OneToMany(mappedBy = "user")
    private List<Invitation> invitations;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Token> tokens;

    @ManyToMany(mappedBy = "users")
    private List<Home> homes;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="permissions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @JsonManagedReference  //la ocupo para la llamada listar por rol no me cree bucle
    private List<Role> roles;

    @OneToOne(mappedBy = "user")
    private Entry entry;

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private QR qr;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return this.active;
    }

}


