package org.web.shop_service.role;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository repository;
    @Override
    public Role getRoleByName(String name) {
        return repository.findByName(name)
                .orElseThrow(EntityNotFoundException::new);
    }
}
