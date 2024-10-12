package com.codigo.examen_ss.controller;

import com.codigo.examen_ss.aggregates.constants.Constants;
import com.codigo.examen_ss.aggregates.request.SignInRequest;
import com.codigo.examen_ss.aggregates.request.UserRequest;
import com.codigo.examen_ss.aggregates.response.BaseResponse;
import com.codigo.examen_ss.aggregates.response.SignInResponse;
import com.codigo.examen_ss.entity.UserEntity;
import com.codigo.examen_ss.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/examensbss_lin/authentication/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/users/signup")
    public ResponseEntity<BaseResponse<UserEntity>> crearUsuario(@RequestBody UserRequest userRequest) throws Exception {
        BaseResponse<UserEntity> response = authenticationService.crearUsuario(userRequest).getBody();
        if (response.getCode().equals(Constants.OK_DNI_CODE)) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/users/signin")
    public ResponseEntity<BaseResponse<SignInResponse>> signIn(@RequestBody SignInRequest signInRequest) throws Exception {
        BaseResponse<SignInResponse> response = authenticationService.signIn(signInRequest).getBody();
        if (response.getCode().equals(Constants.OK_DNI_CODE)) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/key/validateToken")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("validate") String validate) {
        return ResponseEntity.ok(authenticationService.validateToken(validate));
    }

}
