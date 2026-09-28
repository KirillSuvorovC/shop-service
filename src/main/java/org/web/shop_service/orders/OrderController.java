package org.web.shop_service.orders;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.web.shop_service.user.CurrentUser;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public OrderDTO create(@Valid @RequestBody OrderCreateRequest request,
                           @AuthenticationPrincipal CurrentUser user) {
        return orderService.create(request, user);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    public OrdersDTO findAllByUserId(@AuthenticationPrincipal CurrentUser user,
                                     @PageableDefault(size = 20) Pageable pageable) {
        return orderService.findAllByUserId(user, pageable);
    }

    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public OrderDTO updateStatus(@PathVariable Long id,
                                 @Valid @RequestBody
                                 OrderUpdateStatusRequest request) {
        return orderService.updateStatus(id, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public OrderDTO findById(@PathVariable Long id) {
        return orderService.findById(id);
    }
}
