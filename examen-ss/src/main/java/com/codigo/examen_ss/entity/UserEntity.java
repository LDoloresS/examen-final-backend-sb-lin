package com.codigo.examen_ss.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombre", nullable = false, length = 255)
    private String nombres;
    @Column(name = "apPaterno", nullable = false, length = 255)
    private String apPaterno;
    @Column(name = "apMaterno", nullable = false, length = 255)
    private String apMaterno;
    @Column(name = "tipoDoc", nullable = false, length = 2)
    private String tipoDoc;
    @Column(name = "numDoc", nullable = false, length = 8)
    private String numDoc;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    @Column(name = "isAccountNonExpired", nullable = false)
    private Boolean isAccountNonExpired;
    @Column(name = "isAccountNonLocked", nullable = false)
    private Boolean isAccountNonLocked;
    @Column(name = "isCredentialsNonExpired", nullable = false)
    private Boolean isCredentialsNonExpired;
    @Column(name = "isEnabled", nullable = false)
    private Boolean isEnabled;

    @Column(name = "usuaCrea", nullable = false, length = 255)
    private String usuaCrea;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "dateCrea", nullable = false)
    private Timestamp dateCrea;
    @Column(name = "usuaUpda", length = 255)
    private String usuaUpda;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "dateUpda")
    private Timestamp dateUpda;
    @Column(name = "usuaDele", length = 255)
    private String usuaDele;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "dateDele")
    private Timestamp dateDele;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_rol",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "rol_id", referencedColumnName = "id", nullable = false))
    private Set<Rol> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
