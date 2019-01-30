package es.eriktorr.reactor_intro.favorites;

import reactor.core.publisher.Flux;

class RecommendationService {

    Flux<Product> topFiveProductsFor(String username) {
        throw new IllegalStateException("feed me!");
    }

}