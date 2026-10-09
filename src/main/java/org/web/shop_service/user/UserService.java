package org.web.shop_service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.web.shop_service.role.Role;
import org.web.shop_service.role.RoleService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
// TODO: добавить индекс для колонки таблицы users.username
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    /**
     * @throws UsernameNotFoundException если пользователь не найден
     * @return загруженный {@link UserDetails}
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final var user = getUserByUsername(username);
        final var authorities = List.of(new SimpleGrantedAuthority(user.getRole().getName()));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * @throws UsernameNotFoundException если пользователь не найден
     * @return найденный {@link User}
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "user not found with username " + username));
    }

    /**
     * @throws EntityNotFoundException если пользователь не найден
     * @return найденный {@link User}
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "user not found with id " + id));
    }

    public UserDetails createUser(UserRegisterRequest request) {
        final var newUser = new User();
        newUser.setUsername(request.username());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(roleService.getRoleByName(Role.ROLE_USER));
        userRepository.save(newUser);
        final var authorities = List.of(new SimpleGrantedAuthority(newUser.getRole().getName()));
        return new org.springframework.security.core.userdetails.User(
                newUser.getUsername(), newUser.getPassword(), authorities);
    }
}
