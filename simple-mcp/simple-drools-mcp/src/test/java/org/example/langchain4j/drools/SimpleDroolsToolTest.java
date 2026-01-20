package org.example.langchain4j.drools;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.example.langchain4j.drools.domain.LoanApplication;
import org.example.langchain4j.drools.domain.Person;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class SimpleDroolsToolTest {

    @Inject
    SimpleDroolsTool droolsTool;

    @Test
    void testApprove_adultWithSmallAmount() {
        Boolean result = droolsTool.approve(new LoanApplication(new Person("John", 45), 3000));
        assertThat(result).isTrue();
    }

    @Test
    void testApprove_edgeCase_exactlyEighteenAndFiveThousand() {
        Boolean result = droolsTool.approve(new LoanApplication(new Person("Alice", 18), 5000));
        assertThat(result).isTrue();
    }

    @Test
    void testReject_tooYoung() {
        Boolean result = droolsTool.approve(new LoanApplication(new Person("Jane", 16), 3000));
        assertThat(result).isFalse();
    }

    @Test
    void testReject_amountTooLarge() {
        Boolean result = droolsTool.approve(new LoanApplication(new Person("Bob", 30), 8000));
        assertThat(result).isFalse();
    }

    @Test
    void testReject_bothConditionsFail() {
        Boolean result = droolsTool.approve(new LoanApplication(new Person("Charlie", 16), 10000));
        assertThat(result).isFalse();
    }
}
