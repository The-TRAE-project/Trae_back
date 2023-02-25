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
  private void workShiftingDayHandler() {
    workingShiftService.createWorkingShift();
  }

  /**
   * This cron job is used to end a work shifting day.
   */
  @Scheduled(cron = "${scheduler.end-day}")
  private void workShiftingDayEndHandler() {
    workingShiftService.closeWorkingShift();
  }

  /**
   * This method is used to create a working shift during initialization, if one doesn't already
   * exist.
   */
  @PostConstruct
  private void createWorkingShiftAfterInit() {
    if (!workingShiftService.existsActiveWorkingShift()) {
      workingShiftService.createWorkingShift();
    }
  }
}
