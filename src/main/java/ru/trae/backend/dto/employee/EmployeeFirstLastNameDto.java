/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.dto.employee;

/**
 * Represents a data transfer object for an employee's first and last name.
 *
 * @author Vladimir Olennikov
 */
public record EmployeeFirstLastNameDto(
    String firstName,
    String lastName
) {
}
