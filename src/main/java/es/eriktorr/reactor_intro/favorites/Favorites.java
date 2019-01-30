package es.eriktorr.reactor_intro.favorites;

import reactor.core.publisher.Flux;

@FunctionalInterface
public interface Favorites {

    Flux<ProductId> favorites(String username);

}