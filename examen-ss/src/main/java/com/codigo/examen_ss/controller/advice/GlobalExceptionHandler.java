package com.codigo.examen_ss.controller.advice;

import com.codigo.examen_ss.aggregates.constants.Constants;
import com.codigo.examen_ss.aggregates.response.BaseResponse;
import com.codigo.examen_ss.controller.advice.personalizado.ApiException;
import com.codigo.examen_ss.controller.advice.personalizado.ResourceNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.util.Optional;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> controlandoException(Exception e) {
        BaseResponse response = new BaseResponse();
        response.setCode(Constants.ERROR_TRX_CODE);
        response.setMessage(Constants.ERROR_TRX_MESS + e.getMessage());
        response.setObjeto(Optional.empty());
        log.error("EXCEPTION CAPUTRADA EN:. . . controlandoException");
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<BaseResponse> controlandoIoException(IOException e) {
        BaseResponse response = new BaseResponse();
        response.setCode(Constants.ERROR_TRX_CODE);
        response.setMessage(Constants.ERROR_TRX_MESS + e.getMessage());
        response.setObjeto(Optional.empty());
        log.error("EXCEPTION CAPUTRADA EN:. . . controlandoIoException");
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<BaseResponse> controlandoApiException(ApiException e) {
        BaseResponse response = new BaseResponse();
        response.setCode(Constants.ERROR_TRX_API_EXCEPTION_CODE);
        response.setMessage(Constants.ERROR_TRX_API_EXCEPTION_MESS + e.getMessage());
        response.setObjeto(Optional.empty());
        log.error("EXCEPTION CAPUTRADA EN:. . . controlandoApiException");
        return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<BaseResponse> controlandoNullPointerException(NullPointerException e) {
        BaseResponse response = new BaseResponse();
        response.setCode(Constants.ERROR_NULLPOINTER_CODE);
        response.setMessage(Constants.ERROR_NULLPOINTER_MESS + e.getMessage());
        response.setObjeto(Optional.empty());
        log.error("EXCEPTION CAPUTRADA EN:. . . controlandoNullPointerException");
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseResponse> controlandoResourceNotFoundException(ResourceNotFoundException e) {
        BaseResponse response = new BaseResponse();
        response.setCode(Constants.ERROR_RESOURCENOTFOUND_CODE);
        response.setMessage(Constants.ERROR_RESOURCENOTFOUND_MESS + e.getMessage());
        response.setObjeto(Optional.empty());
        log.error("EXCEPTION CAPUTRADA EN:. . . controlandoResourceNotFoundException");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse> controlandoIllegalArgumentException(IllegalArgumentException e) {
        BaseResponse response = new BaseResponse();
        response.setCode(Constants.ERROR_ILLEGALARGUMENT_CODE);
        response.setMessage(Constants.ERROR_ILLEGALARGUMENT_MESS + e.getMessage());
        response.setObjeto(Optional.empty());
        log.error("EXCEPTION CAPUTRADA EN:. . . controlandoIllegalArgumentException");
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<BaseResponse> controlandoAuthenticationException(AuthenticationException e) {
        BaseResponse response = new BaseResponse();
        response.setCode(Constants.ERROR_AUTHENTICATION_CODE);
        response.setMessage(Constants.ERROR_AUTHENTICATION_MESS + e.getMessage());
        response.setObjeto(Optional.empty());
        log.error("EXCEPTION CAPUTRADA EN:. . . controlandoAuthenticationException");
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
