package backend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static backend.constants.OtherConst.CUSTOM_DATE_FORMAT_PRESENT;
import static backend.constants.OtherConst.CUSTOM_DATE_PATTERN;

@Component
@RequiredArgsConstructor
public class CustomDateFormatter {


    /**
     * Converts a LocalDate object to a string formatted with the pattern "dd/MM/yyyy".
     *
     * @param localDateTime The LocalDate object to be formatted.
     * @return A string representation of the LocalDate in the specified format.
     */
    public String convertToLocalDateFormat(LocalDate localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(CUSTOM_DATE_PATTERN));
    }

    /**
     * Formats a LocalDateTime object as a string using a custom date-time format.
     *
     * @param localDateTime The LocalDateTime object to be formatted.
     * @return A string representation of the LocalDateTime in the specified format.
     */
    public String formatLocalDateTimeNowAsString(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CUSTOM_DATE_FORMAT_PRESENT);
        return localDateTime.format(formatter);
    }
}