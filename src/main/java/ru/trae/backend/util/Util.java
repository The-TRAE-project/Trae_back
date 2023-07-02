/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.util;

import java.security.SecureRandom;
import java.util.Date;
import java.util.function.Predicate;
import ru.trae.backend.dto.operation.OperationDto;
import ru.trae.backend.dto.operation.OperationForReportDto;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;

/**
 * This is a utility class containing static methods for generating random integers,
 * getting the period for a first operation, date sorting, and priority sorting.
 *
 * @author Vladimir Olennikov
 */
public class Util {
  Util() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Generates a random integer between min and max.
   *
   * @param min The minimum number that can be generated
   * @param max The maximum number that can be generated
   * @return A random integer
   */
  public static int generateRandomInteger(int min, int max) {
    SecureRandom random = new SecureRandom();
    random.setSeed(new Date().getTime());
    return random.nextInt((max - min) + 1) + min;
  }

  /**
   * Gets the period for operation.
   *
   * @param period The total period
   * @param size   The size of the operation
   * @return The period for the first operation
   */
  public static int calculateOperationPeriod(int period, int size) {
    return (int) Math.floor(((double) period / (double) size));
  }

  /**
   * Compares two projects based on their planned end dates.
   *
   * @param p1 The first project
   * @param p2 The second project
   * @return An integer representing the comparison result
   */
  public static int endDateInContractSorting(Project p1, Project p2) {
    if (p1.getEndDateInContract().isAfter(p2.getEndDateInContract())) {
      return 1;
    } else if (p1.getEndDateInContract().isEqual(p2.getEndDateInContract())) {
      return 0;
    } else {
      return -1;
    }
  }

  /**
   * Compares two operations based on their priorities.
   *
   * @param o1 The first operation
   * @param o2 The second operation
   * @return An integer representing the comparison result
   */
  public static int prioritySorting(Operation o1, Operation o2) {
    return Integer.compare(o1.getPriority(), o2.getPriority());
  }

  /**
   * Compares two operationDtos based on their priorities.
   *
   * @param o1 The first operationDto
   * @param o2 The second operationDto
   * @return An integer representing the comparison result
   */
  public static int prioritySorting(OperationDto o1, OperationDto o2) {
    return Integer.compare(o1.priority(), o2.priority());
  }
  
  /**
   * Compares two OperationForReportDtos based on their priorities.
   *
   * @param o1 The first OperationForReportDto
   * @param o2 The second OperationForReportDto
   * @return An integer representing the comparison result
   */
  public static int prioritySorting(OperationForReportDto o1, OperationForReportDto o2) {
    return Integer.compare(o1.priority(), o2.priority());
  }

  /**
   * Returns a predicate that returns true if the Operation is either ready to accept or in work.
   *
   * @return Predicate {@link Operation} predicate
   */
  public static Predicate<Operation> opIsAcceptanceOrInWork() {
    Predicate<Operation> predicate1 = Operation::isReadyToAcceptance;
    Predicate<Operation> predicate2 = Operation::isInWork;

    return predicate1.or(predicate2);
  }
}
