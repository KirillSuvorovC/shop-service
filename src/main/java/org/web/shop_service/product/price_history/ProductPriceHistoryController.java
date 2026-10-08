package org.web.shop_service.product.price_history;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.web.shop_service.openapi.RuPageableAsQueryParam;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/products")
@Tag(name = "Product-price-histories")
public class ProductPriceHistoryController {
    private final ProductPriceHistoryService service;

    @GetMapping("/price_history")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить историю изменений цен на продукты")
    @RuPageableAsQueryParam
    public ProductsPriceHistoryDTO getPriceChanges(
            @PageableDefault(size = 20)
            @Parameter(hidden = true)
            Pageable pageable
    ) {
        return service.getPriceChanges(pageable);
    }
}
