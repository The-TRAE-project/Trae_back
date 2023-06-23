/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.exceptionhandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.auth0.jwt.exceptions.JWTVerificationException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import ru.trae.backend.controller.AuthController;
import ru.trae.backend.dto.Credentials;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.exceptionhandler.exception.CustomJwtVerificationException;
import ru.trae.backend.exceptionhandler.exception.EmployeeException;
import ru.trae.backend.exceptionhandler.exception.LoginCredentialException;
import ru.trae.backend.exceptionhandler.exception.ManagerException;
import ru.trae.backend.exceptionhandler.exception.OperationException;
import ru.trae.backend.exceptionhandler.exception.PayloadPieceException;
import ru.trae.backend.exceptionhandler.exception.ProjectException;
import ru.trae.backend.exceptionhandler.exception.ReportException;
import ru.trae.backend.exceptionhandler.exception.TypeWorkException;
import ru.trae.backend.exceptionhandler.exception.WorkingShiftException;

class RestResponseEntityExceptionHandlerTest {
  @Mock
  private WebRequest webRequest;
  
  @org.junit.jupiter.api.BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }
  
  @Test
  void handleException_ConstraintViolationException_ReturnsResponseEntityWithCorrectValues() {
    //given
    ConstraintViolation<?> constraintViolation = mock(ConstraintViolation.class);
    when(constraintViolation.getMessage()).thenReturn("Error message");
    
    Set<ConstraintViolation<?>> constraintViolations = new HashSet<>();
    constraintViolations.add(constraintViolation);
    
    ConstraintViolationException exception = new ConstraintViolationException("Error", constraintViolations);
    RestResponseEntityExceptionHandler handler = new RestResponseEntityExceptionHandler();
    
    //when
    ResponseEntity<Response> responseEntity = handler.handleValidException(exception);
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals("Error message", Objects.requireNonNull(responseEntity.getBody()).getError());
    assertEquals(LocalDateTime.now().toString(), responseEntity.getBody().getTimestamp());
  }
  
  @Test
  void handleException_EmployeeException_ReturnsResponseEntityWithCorrectValues() {
    //given
    EmployeeException exception = new EmployeeException(HttpStatus.BAD_REQUEST, "Error");
    RestResponseEntityExceptionHandler handler = new RestResponseEntityExceptionHandler();
    
    //when
    ResponseEntity<Response> responseEntity = handler.handleException(exception);
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals("Error", Objects.requireNonNull(responseEntity.getBody()).getError());
    assertTimestampWithinRange(responseEntity.getBody().getTimestamp());
  }
  
  @Test
  void handleException_MangerException_ReturnsResponseEntityWithCorrectValues() {
    //given
    ManagerException exception = new ManagerException(HttpStatus.BAD_REQUEST, "Error");
    RestResponseEntityExceptionHandler handler = new RestResponseEntityExceptionHandler();
    
    //when
    ResponseEntity<Response> responseEntity = handler.handleException(exception);
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals("Error", Objects.requireNonNull(responseEntity.getBody()).getError());
    assertTimestampWithinRange(responseEntity.getBody().getTimestamp());
  }
  
  @Test
  void handleException_WorkingShiftException_ReturnsResponseEntityWithCorrectValues() {
    //given
    WorkingShiftException exception = new WorkingShiftException(HttpStatus.BAD_REQUEST, "Error");
    RestResponseEntityExceptionHandler handler = new RestResponseEntityExceptionHandler();
    
    //when
    ResponseEntity<Response> responseEntity = handler.handleException(exception);
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals("Error", Objects.requireNonNull(responseEntity.getBody()).getError());
    assertTimestampWithinRange(responseEntity.getBody().getTimestamp());
  }
  
  @Test
  void handleException_ProjectException_ReturnsResponseEntityWithCorrectValues() {
    //given
    ProjectException exception = new ProjectException(HttpStatus.BAD_REQUEST, "Error");
    RestResponseEntityExceptionHandler handler = new RestResponseEntityExceptionHandler();
    
    //when
    ResponseEntity<Response> responseEntity = handler.handleException(exception);
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals("Error", Objects.requireNonNull(responseEntity.getBody()).getError());
    assertTimestampWithinRange(responseEntity.getBody().getTimestamp());
  }
  
  @Test
  void handleException_OperationException_ReturnsResponseEntityWithCorrectValues() {
    //given
    OperationException exception = new OperationException(HttpStatus.BAD_REQUEST, "Error");
    RestResponseEntityExceptionHandler handler = new RestResponseEntityExceptionHandler();
    
    //when
    ResponseEntity<Response> responseEntity = handler.handleException(exception);
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals("Error", Objects.requireNonNull(responseEntity.getBody()).getError());
    assertTimestampWithinRange(responseEntity.getBody().getTimestamp());
  }
  
  @Test
  void handleException_ReportException_ReturnsResponseEntityWithCorrectValues() {
    //given
    ReportException exception = new ReportException(HttpStatus.BAD_REQUEST, "Error");
    RestResponseEntityExceptionHandler handler = new RestResponseEntityExceptionHandler();
    
    //when
    ResponseEntity<Response> responseEntity = handler.handleException(exception);
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals("Error", Objects.requireNonNull(responseEntity.getBody()).getError());
    assertTimestampWithinRange(responseEntity.getBody().getTimestamp());
  }
  
  @Test
  void handleException_TypeWorkException_ReturnsResponseEntityWithCorrectValues() {
    //given
    TypeWorkException exception = new TypeWorkException(HttpStatus.BAD_REQUEST, "Error");
    RestResponseEntityExceptionHandler handler = new RestResponseEntityExceptionHandler();
    
    //when
    ResponseEntity<Response> responseEntity = handler.handleException(exception);
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals("Error", Objects.requireNonNull(responseEntity.getBody()).getError());
    assertTimestampWithinRange(responseEntity.getBody().getTimestamp());
  }
  
  @Test
  void handleException_PayloadException_ReturnsResponseEntityWithCorrectValues() {
    //given
    PayloadPieceException exception = new PayloadPieceException(HttpStatus.BAD_REQUEST, "Error");
    RestResponseEntityExceptionHandler handler = new RestResponseEntityExceptionHandler();
    
    //when
    ResponseEntity<Response> responseEntity = handler.handleException(exception);
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals("Error", Objects.requireNonNull(responseEntity.getBody()).getError());
    assertTimestampWithinRange(responseEntity.getBody().getTimestamp());
  }
  
  @Test
  void handleException_LoginCredentialException_ReturnsResponseEntityWithCorrectValues() {
    //given
    LoginCredentialException exception = new LoginCredentialException(HttpStatus.BAD_REQUEST, "Error");
    RestResponseEntityExceptionHandler handler = new RestResponseEntityExceptionHandler();
    
    //when
    ResponseEntity<Response> responseEntity = handler.handleException(exception);
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals("Error", Objects.requireNonNull(responseEntity.getBody()).getError());
    assertTimestampWithinRange(responseEntity.getBody().getTimestamp());
  }
  
  @Test
  void handleException_JWTVerificationException_ReturnsResponseEntityWithCorrectValues() {
    //given
    JWTVerificationException exception = new JWTVerificationException("Error");
    RestResponseEntityExceptionHandler handler = new RestResponseEntityExceptionHandler();
    
    //when
    ResponseEntity<Response> responseEntity = handler.handleJwtException(exception);
    
    //then
    assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    assertEquals("Error", Objects.requireNonNull(responseEntity.getBody()).getError());
    assertTimestampWithinRange(responseEntity.getBody().getTimestamp());
  }
  
  private void assertTimestampWithinRange(String timestamp) {
    LocalDateTime expectedTimestamp = LocalDateTime.now();
    LocalDateTime actualTimestamp = LocalDateTime.parse(timestamp);
    long diffInSeconds = ChronoUnit.SECONDS.between(expectedTimestamp, actualTimestamp);
    assertEquals(0, diffInSeconds, "Timestamp difference exceeds allowable range");
  }
  
  @Test
  void handleException_PropertyReferenceException_ReturnsResponseEntityWithCorrectValues() {
    //given
    String errorMessage = "Custom error message";
    RestResponseEntityExceptionHandler handler = new RestResponseEntityExceptionHandler();
    PropertyPath propertyPath = PropertyPath.from("period", Operation.class);
    PropertyReferenceException exception = new PropertyReferenceException(errorMessage,
        ClassTypeInformation.from(Operation.class), List.of(propertyPath));
    
    //when
    ResponseEntity<Response> responseEntity = handler.handleException(exception);
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertTimestampWithinRange(Objects.requireNonNull(responseEntity.getBody()).getTimestamp());
  }
  
  @Test
  void handleException_AuthenticationException_ReturnsResponseEntityWithCorrectValues() {
    //given
    String errorMessage = "Authentication failed";
    RestResponseEntityExceptionHandler handler = new RestResponseEntityExceptionHandler();
    AuthenticationException exception = new CustomAuthenticationException(errorMessage);
    
    //when
    ResponseEntity<Response> responseEntity = handler.handleException(exception);
    
    //then
    assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    assertEquals(errorMessage, Objects.requireNonNull(responseEntity.getBody()).getError());
    assertTimestampWithinRange(responseEntity.getBody().getTimestamp());
  }
  
  // Custom subclass of AuthenticationException for testing
  private static class CustomAuthenticationException extends AuthenticationException {
    public CustomAuthenticationException(String message) {
      super(message);
    }
  }
  
  @Test
  void handleMethodArgumentNotValid_ReturnsResponseEntityWithCorrectValues() throws NoSuchMethodException {
    //given
    RestResponseEntityExceptionHandler handler = new RestResponseEntityExceptionHandler();
    MethodArgumentNotValidException exception = createMethodArgumentNotValidException();
    
    //when
    ResponseEntity<Object> responseEntity = handler.handleMethodArgumentNotValid(
        exception, null, HttpStatus.BAD_REQUEST, webRequest);
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
  }
  
  private MethodArgumentNotValidException createMethodArgumentNotValidException() throws NoSuchMethodException {
    FieldError fieldError1 = new FieldError("objectName", "fieldName1", "Error message 1");
    FieldError fieldError2 = new FieldError("objectName", "fieldName2", "Error message 2");
    BindingResult bindingResult = new BindException(Collections.emptyList(), "objectName");
    bindingResult.addError(fieldError1);
    bindingResult.addError(fieldError2);
    return new MethodArgumentNotValidException(
        new MethodParameter(AuthController.class.getMethod("login", Credentials.class), 0),
        bindingResult);
  }
  
  @Test
  void handleHttpMessageNotReadable_ReturnsResponseEntityWithCorrectValues() {
    //given
    RestResponseEntityExceptionHandler handler = new RestResponseEntityExceptionHandler();
    String errorMessage = "Invalid request";
    Throwable cause = mock(Throwable.class);
    when(cause.getMessage()).thenReturn("Error\nError");
    HttpMessageNotReadableException exception = new HttpMessageNotReadableException(errorMessage, cause);
    
    //when
    ResponseEntity<Object> responseEntity = handler.handleHttpMessageNotReadable(
        exception, new HttpHeaders(), HttpStatus.BAD_REQUEST, null);
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    assertNotNull(responseEntity.getBody());
  }
  
  @Test
  void handleException_CustomJwtVerificationException_ReturnsResponseEntityWithCorrectValues() {
    //given
    RestResponseEntityExceptionHandler handler = new RestResponseEntityExceptionHandler();
    CustomJwtVerificationException exception = new CustomJwtVerificationException(HttpStatus.UNAUTHORIZED, "JWT verification failed");
    
    //when
    ResponseEntity<Response> responseEntity = handler.handleException(exception);
    
    //then
    assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    assertEquals("JWT verification failed", responseEntity.getBody().getError());
    assertNotNull(responseEntity.getBody().getTimestamp());
  }
  
}
