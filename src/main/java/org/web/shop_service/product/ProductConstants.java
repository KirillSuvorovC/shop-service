package org.web.shop_service.product;

public class ProductConstants {
    public static final int MIN_SIZE_NAME = 3;
    public static final int MAX_SIZE_NAME = 150;
    public static final String INVALID_NOT_BLANK_NAME = "name should be not empty";
    public static final String INVALID_SIZE_NAME =
            "name should be "
            + MIN_SIZE_NAME + '-' + MAX_SIZE_NAME
            + " characters";
    public static final int MIN_PRICE = 0;
    public static final int MAX_PRICE = 1_000_000_000;
    public static final int PRECISION_PRICE = 10;
    public static final int FRACTION_PRICE = 2;
    public static final String INVALID_PRICE =
            "the price must be "
            + MIN_PRICE + '-' + MAX_PRICE;
}
