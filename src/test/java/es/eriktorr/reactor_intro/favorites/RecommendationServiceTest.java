package es.eriktorr.reactor_intro.favorites;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Tag("unit")
@DisplayName("Product recommendation service")
@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    private static final String JANE_DOE = "Jane Doe";

    private static final ProductId PRODUCT_ID_1 = new ProductId(1);
    private static final Product PRODUCT_1 = new Product(PRODUCT_ID_1, "t-shirt");

    private static final ProductId PRODUCT_ID_2 = new ProductId(2);
    private static final Product PRODUCT_2 = new Product(PRODUCT_ID_2, "jeans");

    private static final ProductId PRODUCT_ID_3 = new ProductId(3);
    private static final Product PRODUCT_3 = new Product(PRODUCT_ID_3, "belt");

    private static final ProductId PRODUCT_ID_4 = new ProductId(4);
    private static final Product PRODUCT_4 = new Product(PRODUCT_ID_4, "shoes");

    private static final ProductId PRODUCT_ID_5 = new ProductId(5);
    private static final Product PRODUCT_5 = new Product(PRODUCT_ID_5, "jacket");

    private static final ProductId PRODUCT_ID_6 = new ProductId(6);
    private static final ProductId PRODUCT_ID_7 = new ProductId(7);

    @Mock
    private Favorites favorites;

    @Mock
    private Products products;

    @Mock
    private TopSales topSales;

    @Mock
    private CachedPromotions cachedPromotions;

    @DisplayName("Suggest top five favorites to user")
    @Test void
    top_five_favorites_for_user() {
        given(favorites.favorites(JANE_DOE)).willReturn(Flux.just(
                PRODUCT_ID_1, PRODUCT_ID_2, PRODUCT_ID_3, PRODUCT_ID_4, PRODUCT_ID_5, PRODUCT_ID_6, PRODUCT_ID_7
        ));
        given(topSales.topSealingProducts()).willReturn(Flux.empty());
        given(products.detailsFor(any(ProductId.class))).will((InvocationOnMock invocation) -> {
            if (invocation.getArgument(0).equals(PRODUCT_ID_1)) return PRODUCT_1;
            if (invocation.getArgument(0).equals(PRODUCT_ID_2)) return PRODUCT_2;
            if (invocation.getArgument(0).equals(PRODUCT_ID_3)) return PRODUCT_3;
            if (invocation.getArgument(0).equals(PRODUCT_ID_4)) return PRODUCT_4;
            if (invocation.getArgument(0).equals(PRODUCT_ID_5)) return PRODUCT_5;
            return null;
        });

        val recommendationService = new RecommendationService(favorites, products, topSales, cachedPromotions);

        StepVerifier.create(recommendationService.topFiveProductsFor(JANE_DOE))
                .expectNext(PRODUCT_1)
                .expectNext(PRODUCT_2)
                .expectNext(PRODUCT_3)
                .expectNext(PRODUCT_4)
                .expectNext(PRODUCT_5)
                .verifyComplete();
    }

    @DisplayName("Suggest top five sealing products to user with no favorites")
    @Test void
    top_sales_for_user_with_no_favorites() {
        given(favorites.favorites(JANE_DOE)).willReturn(Flux.empty());
        given(topSales.topSealingProducts()).willReturn(Flux.just(PRODUCT_2, PRODUCT_1, PRODUCT_4));

        val recommendationService = new RecommendationService(favorites, products, topSales, cachedPromotions);

        StepVerifier.create(recommendationService.topFiveProductsFor(JANE_DOE))
                .expectNext(PRODUCT_2)
                .expectNext(PRODUCT_1)
                .expectNext(PRODUCT_4)
                .verifyComplete();
    }

    @DisplayName("Suggest products within 1 second or send cached response")
    @Test void
    answer_within_one_second_or_send_cached_response() {
        given(favorites.favorites(JANE_DOE)).willReturn(Flux.never());
        given(products.detailsFor(any(ProductId.class))).willReturn(PRODUCT_3, PRODUCT_1, PRODUCT_5, PRODUCT_2);
        given(topSales.topSealingProducts()).willReturn(Flux.empty());
        given(cachedPromotions.activePromotions()).willReturn(Flux.just(PRODUCT_ID_3, PRODUCT_ID_1, PRODUCT_ID_5, PRODUCT_ID_2));

        val recommendationService = new RecommendationService(favorites, products, topSales, cachedPromotions);

        StepVerifier.create(recommendationService.topFiveProductsFor(JANE_DOE))
                .thenAwait(Duration.ofSeconds(2L))
                .expectNext(PRODUCT_3)
                .expectNext(PRODUCT_1)
                .expectNext(PRODUCT_5)
                .expectNext(PRODUCT_2)
                .verifyComplete();
    }

}