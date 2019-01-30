package es.eriktorr.reactor_intro.favorites;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@DisplayName("Product recommendation service")
@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @DisplayName("Suggest top five favorites to user")
    @Test void
    top_five_favorites_for_user() {
        assert false;
    }

    @DisplayName("Suggest top five sealing products to user with no favorites")
    @Test void
    top_sales_for_user_with_no_favorites() {
        assert false;
    }

    @DisplayName("Suggest products within 1 second or send cached response")
    @Test void
    answer_within_one_second_or_send_cached_response() {
        assert false;
    }

}