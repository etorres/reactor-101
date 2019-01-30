package es.eriktorr.reactor_intro.favorites;

import lombok.Value;

@Value
class ProductId {

    private final int value;

    static ProductId productIdFrom(int productId) {
        return new ProductId(productId);
    }

}