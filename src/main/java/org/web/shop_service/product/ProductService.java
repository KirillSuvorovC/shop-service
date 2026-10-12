package org.web.shop_service.product;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ValidationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ProductService {
    /**
     * Сохраняет новый продукт в БД.
     * @throws EntityNotFoundException если продукт не найден.
     * @param request данные для изменения продукта
     * @return созданный продукт в виде {@link ProductDTO}
     */
    ProductDTO createProduct(ProductCreateRequest request);
    /**
     * Полностью изменяет существующий продукт в БД.
     * @throws EntityNotFoundException если продукт не найден.
     * @throws OptimisticLockException если версии не совпадают
     * @throws OptimisticLockingFailureException если версии не совпадают
     * @param id ID продукта
     * @param request данные для изменения продукта
     * @return измененный продукт в виде {@link ProductDTO}
     */
    ProductDTO updateProduct(Long id, ProductUpdateRequest request);
    /**
     * Частично изменяет существующий продукт в БД.
     * Обновляются только те поля из {@code request},
     * которые не null.
     * @throws EntityNotFoundException если продукт не найден
     * @throws ValidationException если поле запроса некорректное
     * @throws OptimisticLockException если версии не совпадают
     * @throws OptimisticLockingFailureException если версии не совпадают
     * @param id ID продукта
     * @param request данные для изменения продукта
     * @return измененный продукт в виде {@link ProductDTO}
     */
    ProductDTO patchProduct(Long id, ProductPatchRequest request);
    /**
     * @throws EntityNotFoundException если продукт не найден
     * @param id идентификатор заказа
     * @return найденный продукт в виде {@link ProductDTO}
     */
    ProductDTO getProductById(Long id);
    ProductsDTO getProducts(Pageable pageable);
    /**
     * Вместо физического удаления архивизирует продукт,
     * чтобы сохранить связи между таблицами.
     * @param id идентификатор заказа
     */
    void deleteProduct(Long id);
    /**
     * @throws EntityNotFoundException если продукт не найден
     * @param id идентификатор заказа
     * @return найденная сущность {@link Product}
     */
    Product getProductEntityById(Long id);
    /**
     * Используется для проверки существующих продуктов через
     * ключ Long - ID продукта
     * @param ids IDs необходимых продуктов
     * @return продукты в виде {@link Map}, где ключ - ID продукта,
     * а значение - сам продукт
     */
    Map<Long, Product> getProductEntitiesAsMap(Iterable<Long> ids);
}
