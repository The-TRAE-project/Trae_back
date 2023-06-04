package ru.trae.backend.dto.report;

import java.util.Set;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import ru.trae.backend.util.ReportParameter;

/**
 * Represents a request for report of deadlines.
 *
 * @author Vladimir Olennikov
 */
public record DeadlinesReq(
    @NotNull(message = "Name of first parameter is NULL")
    @Size(min = 6, max = 11, message = "Length name of first parameter is wrong")
    ReportParameter firstParameter,
    @NotNull(message = "Value of first parameter: parameter is NULL")
    @Min(value = 1, message = "The first parameter cannot be less than 1")
    @Max(value = Integer.MAX_VALUE, message =
        "The first parameter cannot be more than " + Integer.MAX_VALUE)
    long valueOfFirstParameter,
    @NotNull(message = "Name of second parameter is NULL")
    @Size(min = 6, max = 11, message = "Length name of second parameter is wrong")
    ReportParameter secondParameter,
    @NotNull(message = "Values of second parameter: set values is NULL")
    Set<Long> valuesOfSecondParameter,
    @NotNull(message = "Name of third parameter is NULL")
    @Size(min = 6, max = 11, message = "Length name of third parameter is wrong")
    ReportParameter thirdParameter,
    @NotNull(message = "Values of third parameter: set values is NULL")
    Set<Long> valuesOfThirdParameter
) {
}
