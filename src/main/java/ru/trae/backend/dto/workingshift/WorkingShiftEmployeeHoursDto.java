/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.dto.workingshift;

import java.time.LocalDate;

/**
 * Data record representing the employee's working shift hours for a specific shift.
 *
 * @author Vladimir Olennikov
 */
public record WorkingShiftEmployeeHoursDto(
    long employeeId,
    LocalDate shiftDate,
    Float percentOnShift
) {
}
