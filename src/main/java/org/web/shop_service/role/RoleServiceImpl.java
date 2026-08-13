package org.web.shop_service.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.web.shop_service.utils.exceptions.EntityNotFoundException;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository repository;
    @Override
    public Role getRoleByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException(Role.class));
    }
}
