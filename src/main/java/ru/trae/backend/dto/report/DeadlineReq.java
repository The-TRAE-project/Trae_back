package ru.trae.backend.dto.report;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import ru.trae.backend.util.ReportParameter;

/**
 * Represents a request for report of deadline.
 *
 * @author Vladimir Olennikov
 */
public record DeadlineReq(
    @NotNull(message = "Name of first parameter is NULL")
    ReportParameter firstParameter,
    @NotNull(message = "Value of first parameter: parameter is NULL")
    @Min(value = 1, message = "The first parameter cannot be less than 1")
    @Max(value = Integer.MAX_VALUE, message =
        "The first parameter cannot be more than " + Integer.MAX_VALUE)
    long valueOfFirstParameter,
    @NotNull(message = "Name of second parameter is NULL")
    ReportParameter secondParameter,
    @NotNull(message = "Values of second parameter: set values is NULL")
    @NotEmpty(message = "Values of second parameter: set values is empty")
    Set<Long> valuesOfSecondParameter,
    @NotNull(message = "Name of third parameter is NULL")
    ReportParameter thirdParameter,
    @NotNull(message = "Values of third parameter: set values is NULL")
    @NotEmpty(message = "Values of third parameter: set values is empty")
    Set<Long> valuesOfThirdParameter
) {
}
