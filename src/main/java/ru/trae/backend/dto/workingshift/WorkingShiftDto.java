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

import java.time.LocalDateTime;
import java.util.List;
import ru.trae.backend.dto.TimeControlDto;

/**
 * WorkingShiftDto record is a data class that holds information about working shift.
 * It contains startShift, endShift, isEnded and list of timeControls.
 *
 * @author Vladimir Olennikov
 */
public record WorkingShiftDto(
    LocalDateTime startShift,
    LocalDateTime endShift,
    boolean isEnded,
    List<TimeControlDto> timeControls
) {
}
