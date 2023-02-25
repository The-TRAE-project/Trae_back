/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.trae.backend.entity.task.Order;

/**
 * JPA repository for the {@link Order} entity.
 *
 * @author Vladimir Olennikov
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
