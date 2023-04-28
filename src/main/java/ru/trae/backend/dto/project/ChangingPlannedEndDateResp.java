/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.dto.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * A response object for changing the planned end date of a project.
 *
 * @param id             The ID of the project.
 * @param plannedEndDate The updated planned end date of the project.
 */
public record ChangingPlannedEndDateResp(
    @JsonProperty("projectId")
    long id,
    @JsonProperty("updatedPlannedEndDate")
    LocalDateTime plannedEndDate
) {
}
