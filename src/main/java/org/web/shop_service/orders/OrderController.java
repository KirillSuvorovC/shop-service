package org.web.shop_service.orders;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.web.shop_service.openapi.IfMatchHeader;
import org.web.shop_service.openapi.OpenAPIConfig;
import org.web.shop_service.openapi.RuPageableAsQueryParam;
import org.web.shop_service.user.AuthUser;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders",
        description = """
        Только аутентифицированные пользователи имеют доступ
        к эндпоинтам, иначе вернут 401
        """)
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Создать заказ",
            security = @SecurityRequirement(name = OpenAPIConfig.BEARER_AUTH),
            responses = {
            @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Продукт не найден",
                    content = @Content)
            }
    )
    public OrderDTO create(@Valid @RequestBody OrderCreateRequest request,
                           @AuthenticationPrincipal AuthUser user) {
        return orderService.create(request, user);
    }

    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Обновить статус заказа",
            security = @SecurityRequirement(name = OpenAPIConfig.BEARER_AUTH),
            responses = {
            @ApiResponse(responseCode = "403", description = "Пользователь - не админ",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Заказ не найден",
                    content = @Content)
            }
    )
    @IfMatchHeader
    public OrderDTO updateStatus(@PathVariable Long id,
                                 @Valid @RequestBody
                                 OrderUpdateStatusRequest request) {
        return orderService.updateStatus(id, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Получить заказы",
            description = "Возвращает заказы у текущего пользователя",
            security = @SecurityRequirement(name = OpenAPIConfig.BEARER_AUTH),
            responses = {
            @ApiResponse(responseCode = "404", description = "Заказ не найден",
                    content = @Content)
            }
    )
    @RuPageableAsQueryParam
    public OrdersDTO findAllByUserId(@AuthenticationPrincipal AuthUser user,
                 @PageableDefault(size = 20) @Parameter(hidden = true) Pageable pageable) {
        return orderService.findAllByUserId(user, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Получить конкретный заказ по ID",
            security = @SecurityRequirement(name = OpenAPIConfig.BEARER_AUTH),
            responses = {
            @ApiResponse(responseCode = "403", description = "Пользователь - не админ",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Заказ не найден",
                    content = @Content)
            }
    )
    public OrderDTO findById(@PathVariable Long id) {
        return orderService.findById(id);
    }
}
