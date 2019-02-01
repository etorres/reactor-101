package es.eriktorr.reactor_intro.favorites;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

class RecommendationService {

    private final Favorites favorites;
    private final Products products;
    private final TopSales topSales;
    private final CachedPromotions cachedPromotions;

    RecommendationService(Favorites favorites, Products products, TopSales topSales, CachedPromotions cachedPromotions) {
        this.favorites = favorites;
        this.products = products;
        this.topSales = topSales;
        this.cachedPromotions = cachedPromotions;
    }

    Flux<Product> topFiveProductsFor(String username) {
        return favorites.favorites(username)
                .timeout(Duration.ofSeconds(1L))
                .onErrorResume(TimeoutException.class, e -> cachedPromotions.activePromotions())
                .map(products::detailsFor)
                .switchIfEmpty(topSales.topSealingProducts())
                .take(5L);
    }

}