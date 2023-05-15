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

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.trae.backend.entity.TypeWork;

/**
 * Repository interface for managing {@link TypeWork} entities.
 *
 * @author Vladimir Olennikov
 */
@Repository
public interface TypeWorkRepository extends JpaRepository<TypeWork, Long> {
  @Query("select t from TypeWork t where t.isActive = true")
  List<TypeWork> findByIsActiveTrue();
  
  @Query("select t from TypeWork t where t.isActive = true and t.id <> ?1")
  List<TypeWork> findByIsActiveTrueAndIdNot(long id);
  
  boolean existsByNameIgnoreCase(String name);
  
  Optional<TypeWork> findByName(String name);
  
  @Transactional
  @Modifying
  @Query("update TypeWork t set t.name = ?1 where t.id = ?2")
  void updateNameById(String name, Long id);
  
  @Query("select tw.name from TypeWork tw where tw.id = ?1")
  String getTypeWorkNameById(long typeWorkId);
  
  @Transactional
  @Modifying
  @Query("update TypeWork t set t.isActive = ?1 where t.id = ?2")
  void updateIsActiveById(boolean isActive, Long id);
  
  @Query("select tw.isActive from TypeWork tw where tw.id = ?1")
  boolean getTypeWorkActiveById(long typeWorkId);
  
  @Query("select t from TypeWork t where t.isActive = ?1")
  Page<TypeWork> findByIsActive(boolean isActive, Pageable pageable);
}
