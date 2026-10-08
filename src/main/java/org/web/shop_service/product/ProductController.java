package org.web.shop_service.product;

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
import org.springframework.web.bind.annotation.*;
import org.web.shop_service.openapi.IfMatchHeader;
import org.web.shop_service.openapi.OpenAPIConfig;
import org.web.shop_service.openapi.RuPageableAsQueryParam;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = """
        GET-эндпоинты - доступны всем.
        POST, PUT, PATCH, DELETE эндпоинты - только для админа,
        возвращают:
        401 - если не аутентифицирован,
        403 - если аутентифицирован, но не админ.
        """)
public class ProductController {
    private final ProductService service;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Создать продукт",
            security = @SecurityRequirement(name = OpenAPIConfig.BEARER_AUTH)
    )
    @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content)
    public ProductDTO createProduct(@Valid @RequestBody
                                          ProductCreateRequest request) {
        return service.createProduct(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Полностью обновить продукт",
            security = @SecurityRequirement(name = OpenAPIConfig.BEARER_AUTH)
    )
    @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content)
    @IfMatchHeader
    public ProductDTO updateProduct(@PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {
        return service.updateProduct(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Частично обновить продукт",
            security = @SecurityRequirement(name = OpenAPIConfig.BEARER_AUTH)
    )
    @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content)
    @IfMatchHeader
    public ProductDTO patchProduct(@PathVariable Long id,
            @Valid @RequestBody ProductPatchRequest request) {
        return service.patchProduct(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удалить продукт",
            security = @SecurityRequirement(name = OpenAPIConfig.BEARER_AUTH)
    )
    @ApiResponse(responseCode = "204", description = "Успешно удален", content = @Content)
    public void deleteProduct(@PathVariable Long id) {
        service.deleteProduct(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить конкретный продукт по ID")
    @ApiResponse(responseCode = "404", description = "Продукт не найден", content = @Content)
    public ProductDTO getProductById(@PathVariable Long id) {
        return service.getProductById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить продукты")
    @RuPageableAsQueryParam
    public ProductsDTO getProducts(@PageableDefault(size = 20)
                                   @Parameter(hidden = true) Pageable pageable) {
        return service.getProducts(pageable);
    }
}
