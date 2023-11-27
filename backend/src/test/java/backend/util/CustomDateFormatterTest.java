package backend.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CustomDateFormatterTest {

    @Test
    void testConvertToLocalDateFormat() {
        CustomDateFormatter customDateFormatter = new CustomDateFormatter();
        LocalDate inputDate = LocalDate.of(2023, 11, 27);
        String expectedFormattedDate = "27/11/2023";

        String formattedDate = customDateFormatter.convertToLocalDateFormat(inputDate);

        assertEquals(expectedFormattedDate, formattedDate);
    }

    @Test
    void testFormatLocalDateTimeNowAsString() {
        CustomDateFormatter customDateFormatter = new CustomDateFormatter();
        LocalDateTime mockLocalDateTime = LocalDateTime.of(2023, 11, 27, 12, 17);
        String expectedFormattedDate = "27 Nov 2023 12:17";

        assertEquals(expectedFormattedDate, customDateFormatter.formatLocalDateTimeNowAsString(mockLocalDateTime));

    }


}
