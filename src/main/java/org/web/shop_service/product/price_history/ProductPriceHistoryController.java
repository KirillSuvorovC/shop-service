package org.web.shop_service.product.price_history;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web.shop_service.annotations.PublicEndPoint;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/products")
public class ProductPriceHistoryController {
    private final ProductPriceHistoryService service;

    @GetMapping("/price_history")
    public ResponseEntity<ProductsPriceHistoryDTO> getPriceChanges(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getPriceChanges(pageable));
    }
}
