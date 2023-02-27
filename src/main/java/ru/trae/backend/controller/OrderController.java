/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.order.NewOrderDto;
import ru.trae.backend.dto.order.OrderDto;
import ru.trae.backend.service.OrderService;

/**
 * Controller class for handling order related API requests.
 *
 * @author Vladimir Olennikov
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

  private final OrderService orderService;

  @PostMapping("/new")
  public ResponseEntity<OrderDto> orderPersist(@RequestBody NewOrderDto dto) {
    OrderDto orderDto = orderService.convertFromOrder(orderService.receiveNewOrder(dto));
    return ResponseEntity.ok(orderDto);
  }

  @GetMapping("/{orderId}")
  public ResponseEntity<OrderDto> order(@PathVariable long orderId) {
    return ResponseEntity.ok(orderService.convertFromOrderById(orderId));
  }

  @GetMapping("/orders")
  public ResponseEntity<List<OrderDto>> orders() {
    return ResponseEntity.ok(orderService.getAllOrder());
  }
}
