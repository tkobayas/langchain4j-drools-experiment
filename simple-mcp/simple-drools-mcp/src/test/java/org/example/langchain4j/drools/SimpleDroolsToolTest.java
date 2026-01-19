package org.example.langchain4j.drools;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class SimpleDroolsToolTest {

    @Inject
    SimpleDroolsTool droolsTool;

    @Test
    void testApprove_adultWithSmallAmount() {
        String result = droolsTool.approve("John", 45, 3000);
        assertThat(result).isEqualTo("true");
    }

    @Test
    void testApprove_edgeCase_exactlyEighteenAndFiveThousand() {
        String result = droolsTool.approve("Alice", 18, 5000);
        assertThat(result).isEqualTo("true");
    }

    @Test
    void testReject_tooYoung() {
        String result = droolsTool.approve("Jane", 16, 3000);
        assertThat(result).isEqualTo("false");
    }

    @Test
    void testReject_amountTooLarge() {
        String result = droolsTool.approve("Bob", 30, 8000);
        assertThat(result).isEqualTo("false");
    }

    @Test
    void testReject_bothConditionsFail() {
        String result = droolsTool.approve("Charlie", 16, 10000);
        assertThat(result).isEqualTo("false");
    }
}
