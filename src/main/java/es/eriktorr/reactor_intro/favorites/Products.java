package es.eriktorr.reactor_intro.favorites;

@FunctionalInterface
interface Products {

    Product detailsFor(ProductId productId);

}