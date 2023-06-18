/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class PageSettingsTest {
  
  @InjectMocks
  private PageSettings pageSettings;
  
  @Test
  void testBuildSortDefaultDirection() {
    //given
    pageSettings.setDirection("invalid");
    pageSettings.setKey("id");
    
    Sort expectedSort = Sort.by("id").descending();
    Sort actualSort = pageSettings.buildSort();
    
    //then
    assertEquals(expectedSort, actualSort);
  }
  
  @Test
  void testBuildSortDefaultKey() {
    //given
    pageSettings.setDirection("asc");
    
    Sort expectedSort = Sort.by("id").ascending();
    Sort actualSort = pageSettings.buildSort();
    
    //then
    assertEquals(expectedSort, actualSort);
  }
  
  @Test
  void testBuildManagerOrEmpSortDefaultDirection() {
    //given
    pageSettings.setDirection("invalid");
    
    Sort expectedSort = Sort.by("lastName").descending().and(Sort.by("firstName").descending());
    Sort actualSort = pageSettings.buildManagerOrEmpSort();
    
    //then
    assertEquals(expectedSort, actualSort);
  }
  
  @Test
  void testBuildManagerOrEmpSortDefaultKey() {
    //given
    pageSettings.setDirection("asc");
    
    Sort expectedSort = Sort.by("lastName").ascending().and(Sort.by("firstName").ascending());
    Sort actualSort = pageSettings.buildManagerOrEmpSort();
    
    //then
    assertEquals(expectedSort, actualSort);
  }
  
  @Test
  void testBuildTypeWorkSortDefaultDirection() {
    //given
    pageSettings.setDirection("invalid");
    
    Sort expectedSort = Sort.by("name").descending().and(Sort.by("id").descending());
    Sort actualSort = pageSettings.buildTypeWorkSort();
    
    //then
    assertEquals(expectedSort, actualSort);
  }
  
  @Test
  void testBuildTypeWorkSortDefaultKey() {
    //given
    pageSettings.setDirection("asc");
    
    Sort expectedSort = Sort.by("name").ascending().and(Sort.by("id").ascending());
    Sort actualSort = pageSettings.buildTypeWorkSort();
    
    //then
    assertEquals(expectedSort, actualSort);
  }
  
  @Test
  void testBuildProjectSortDefaultDirection() {
    //given
    pageSettings.setDirection("invalid");
    
    Sort expectedSort = Sort.by("endDateInContract").descending().and(Sort.by("id").descending());
    Sort actualSort = pageSettings.buildProjectSort();
    
    //then
    assertEquals(expectedSort, actualSort);
  }
  
  @Test
  void testBuildProjectSortDefaultKey() {
    //given
    pageSettings.setDirection("asc");
    
    Sort expectedSort = Sort.by("endDateInContract").ascending().and(Sort.by("id").ascending());
    Sort actualSort = pageSettings.buildProjectSort();
    
    //then
    assertEquals(expectedSort, actualSort);
  }
  
  @Test
  void testEquals() {
    //given
    PageSettings anotherPageSettings = new PageSettings();
    
    //then
    assertEquals(anotherPageSettings, pageSettings);
    assertEquals(pageSettings, pageSettings);
  }
  
  @Test
  void testNotEqualsId() {
    //given
    PageSettings anotherPageSettings = new PageSettings();
    anotherPageSettings.setKey("no_id");
    
    //then
    assertNotEquals(anotherPageSettings, pageSettings);
  }
  
  @Test
  void testNotEqualsDirection() {
    //given
    PageSettings anotherPageSettings = new PageSettings();
    anotherPageSettings.setDirection("desc");
    
    //then
    assertNotEquals(anotherPageSettings, pageSettings);
  }
  
  @Test
  void testNotEquals_Null() {
    //given
    PageSettings anotherPageSettings = null;
    
    //then
    assertFalse(pageSettings.equals(anotherPageSettings));
  }
  
  @Test
  void testNotEquals_anotherClass() {
    //given
    String anotherClassObject = "";
    
    //then
    assertFalse(pageSettings.equals(anotherClassObject));
  }
  
  @Test
  void testHashCode() {
    //given
    int hash = pageSettings.hashCode();
    
    //then
    assertNotEquals(0, pageSettings.hashCode());
    assertEquals(hash, pageSettings.hashCode());
  }
  
  @Test
  void testToString() {
    //then
    assertNotNull(pageSettings.toString());
  }
}
