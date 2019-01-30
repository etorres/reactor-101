package es.eriktorr.reactor_intro.favorites;

import reactor.core.publisher.Flux;

@FunctionalInterface
interface CachedPromotions {

    Flux<ProductId> activePromotions();

}