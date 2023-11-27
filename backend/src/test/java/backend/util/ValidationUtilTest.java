package backend.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ValidationUtilTest {

    @Test
    void testIsValidWithValidEntity() {
        ValidationUtil validationUtil = new ValidationUtil();
        SampleEntity validEntity = new SampleEntity("John", "Doe", "john.doe@example.com");

        boolean isValid = validationUtil.isValid(validEntity);

        assertTrue(isValid);
    }


    private static class SampleEntity {
        private final String firstName;
        private final String lastName;
        private final String email;

        public SampleEntity(String firstName, String lastName, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }

    }
}
