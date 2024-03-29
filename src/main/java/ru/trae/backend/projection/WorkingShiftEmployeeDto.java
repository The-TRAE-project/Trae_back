/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.projection;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;

/**
 * Projection interface representing the employee's working shift hours for a specific shift.
 *
 * @author Vladimir Olennikov
 */
public interface WorkingShiftEmployeeDto {
  @Value("#{target.employee_id}")
  long getEmployeeId();
  
  @Value("#{target.auto_closing_shift}")
  Boolean isAutoClosed();
  
  @Value("#{target.shift_date}")
  LocalDate getShiftDate();
  
  @Value("#{target.part_of_shift}")
  Float getPartOfShift();
}
