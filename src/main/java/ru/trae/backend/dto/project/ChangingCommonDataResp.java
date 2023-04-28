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

/**
 * Response class for changing common data about project.
 *
 * @param id       The id of the project.
 * @param number   The number of the project.
 * @param name     The name of the project.
 * @param customer The customer of the project.
 * @param comment  The commentary.
 */
public record ChangingCommonDataResp(
    @JsonProperty("projectId")
    long id,
    @JsonProperty("projectNumber")
    int number,
    @JsonProperty("projectName")
    String name,
    String customer,
    @JsonProperty("commentary")
    String comment
) {
}
