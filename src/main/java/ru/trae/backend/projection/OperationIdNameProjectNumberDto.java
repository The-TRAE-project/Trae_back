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

import org.springframework.beans.factory.annotation.Value;

/**
 * Projection interface representing the short operation DTO.
 *
 * @author Vladimir Olennikov
 */
public interface OperationIdNameProjectNumberDto {
  @Value("#{target.id}")
  long getProjectId();
  
  @Value("#{target.name}")
  String getName();
  
  @Value("#{target.project_number}")
  Integer getProjectNumber();
}
