/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.scheduler;

import java.time.LocalTime;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.trae.backend.service.WorkingShiftService;

/**
 * This is a configuration class used to enable scheduling of tasks related to work shifting.
 * It also defines two cron jobs, one to start a new work shifting day, and another to end the day.
 * A working shift is also created during initialization, if one doesn't already exist.
 *
 * @author Vladimir Olennikov
 */
@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
@RequiredArgsConstructor
public class WorkShiftingScheduler {
  private final WorkingShiftService workingShiftService;

  /**
   * This cron job is used to start a new work shifting day.
   */
  @Scheduled(cron = "${scheduler.start-day}")
  protected void workShiftingDayHandler() {
    if (!workingShiftService.existsActiveWorkingShift()) {
      workingShiftService.closeWorkingShift();
    }

    workingShiftService.createWorkingShift();
  }

  /**
   * This cron job is used to end a work shifting day.
   */
  @Scheduled(cron = "${scheduler.end-day}")
  protected void workShiftingDayEndHandler() {
    workingShiftService.closeWorkingShift();
  }

  /**
   * This method is used to create a working shift during initialization, if one doesn't already
   * exist.
   */
  @PostConstruct
  protected void createWorkingShiftAfterInit() {
    LocalTime start = LocalTime.of(7, 0, 0);
    LocalTime end = LocalTime.of(23, 0, 0);

    //проверка, что не закрытая сменя открыта сегодня, если нет, то смена завершается
    if (workingShiftService.existsByIsEndedFalseAndStartShiftNotCurrentDate()) {
      workingShiftService.closeWorkingShift();
    }
    //проверка, нет открытой смены между 7 утра и 23 вечера, если нет, то сменя открывается
    if (!workingShiftService.existsActiveWorkingShift()
        && LocalTime.now().isBefore(end) && LocalTime.now().isAfter(start)) {
      workingShiftService.createWorkingShift();
    }
  }
}
