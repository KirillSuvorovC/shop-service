package org.web.shop_service.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.web.shop_service.role.Role;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, unique = true)
    private Long id;
    private String username;
    private String password;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

}
