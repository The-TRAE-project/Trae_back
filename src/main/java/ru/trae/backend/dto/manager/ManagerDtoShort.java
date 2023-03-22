/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.dto.manager;

/**
 * ManagerDtoShort is a data transfer object (DTO) class used to encapsulate data related
 * to a manager.
 *
 * @author Vladimir Olennikov
 */
public record ManagerDtoShort(
    long managerId,
    String lastName,
    String firstName
) {
}
