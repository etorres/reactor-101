package es.eriktorr.reactor_intro.favorites;

import lombok.Value;

import static es.eriktorr.reactor_intro.favorites.ProductId.productIdFrom;

@Value
class Product {

    private final ProductId id;
    private final String description;

    static final Product INVALID_PRODUCT = new Product(productIdFrom(-1), "Invalid product");

}