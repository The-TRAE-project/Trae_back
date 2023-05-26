package ru.trae.backend.dto.report;

import java.time.LocalDate;
import java.util.List;
import ru.trae.backend.dto.project.ProjectForReportDto;

/**
 * Data transfer object for reporting projects for a specific period.
 *
 * @author Vladimir Olennikov
 */
public record ReportProjectForPeriodDto(
    LocalDate startPeriod,
    LocalDate endPeriod,
    LocalDate dateOfReportFormation,
    List<ProjectForReportDto> projectForReportDtoList
) {
}
