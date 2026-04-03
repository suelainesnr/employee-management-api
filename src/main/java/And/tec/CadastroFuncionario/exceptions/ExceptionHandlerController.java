package And.tec.CadastroFuncionario.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



@RestControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(FuncionarioException.class)


    public ResponseEntity<ErroResponse> handleFuncionarioException(FuncionarioException ex) {
        return ResponseEntity.badRequest().body(
            ErroResponse.builder()
                .message(ex.getMessage())
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(java.time.LocalDateTime.now())
                .build()
        );
    }
    @ExceptionHandler(FuncionarioNotFoundException.class)
    public ResponseEntity<ErroResponse> handleFuncionarioNotFoundException(FuncionarioNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErroResponse.builder()
                        .message(ex.getMessage())
                        .errorCode(HttpStatus.NOT_FOUND.value())
                        .timestamp(java.time.LocalDateTime.now())
                        .build()
        );
    }
        @ExceptionHandler(CpfjaCadastradoException.class)
        public ResponseEntity<ErroResponse> handleCpfjaCadastradoException(CpfjaCadastradoException ex) {
            return ResponseEntity.badRequest().body(
                    ErroResponse.builder()
                            .message(ex.getMessage())
                            .errorCode(HttpStatus.BAD_REQUEST.value())
                            .timestamp(java.time.LocalDateTime.now())
                            .build()
            );
        }
        @ExceptionHandler(SalarioNegativoException.class)
        public ResponseEntity<ErroResponse> handleSalarioNegativoException(SalarioNegativoException ex) {
            return ResponseEntity.badRequest().body(
                    ErroResponse.builder()
                            .message(ex.getMessage())
                            .errorCode(HttpStatus.BAD_REQUEST.value())
                            .timestamp(java.time.LocalDateTime.now())
                            .build()
            );
        }
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErroResponse> handleGenericException(Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ErroResponse.builder()
                            .message("Ocorreu um erro inesperado: " + ex.getMessage())
                            .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .timestamp(java.time.LocalDateTime.now())
                            .build()
            );
        }
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ErroResponse> handleRuntimeException(RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ErroResponse.builder()
                            .message("Ocorreu um erro inesperado: " + ex.getMessage())
                            .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .timestamp(java.time.LocalDateTime.now())
                            .build()
            );
        }
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErroResponse> handleValidationException(MethodArgumentNotValidException ex) {
            String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                    .orElse("Erro de validação");
            return ResponseEntity.badRequest().body(
                    ErroResponse.builder()
                            .message(errorMessage)
                            .errorCode(HttpStatus.BAD_REQUEST.value())
                            .timestamp(java.time.LocalDateTime.now())
                            .build()
            );
        }
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErroResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(
                    ErroResponse.builder()
                            .message(ex.getMessage())
                            .errorCode(HttpStatus.BAD_REQUEST.value())
                            .timestamp(java.time.LocalDateTime.now())
                            .build()
            );
        }
        @ExceptionHandler(NegocioException.class)
        public ResponseEntity<ErroResponse> handleNegocioException(NegocioException ex) {
            return ResponseEntity.badRequest().body(
                    ErroResponse.builder()
                            .message(ex.getMessage())
                            .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .timestamp(java.time.LocalDateTime.now())
                            .build()
            );
        }
        @ExceptionHandler(DependenteNotFoundException.class)
        public ResponseEntity<ErroResponse> handleDependenteNotFoundException(DependenteNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ErroResponse.builder()
                            .message(ex.getMessage())
                            .errorCode(HttpStatus.NOT_FOUND.value())
                            .timestamp(java.time.LocalDateTime.now())
                            .build()
            );
        }



}
