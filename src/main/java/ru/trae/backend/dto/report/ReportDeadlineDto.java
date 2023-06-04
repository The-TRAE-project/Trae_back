package ru.trae.backend.dto.report;

import java.util.List;
import lombok.Data;

/**
 * Data transfer object for reporting deadlines.
 *
 * @author Vladimir Olennikov
 */
@Data
public class ReportDeadlineDto {
  private long firstRespId;
  private String firstRespValue;
  private List<SecondResponseSubDto> secondRespValues;
}
