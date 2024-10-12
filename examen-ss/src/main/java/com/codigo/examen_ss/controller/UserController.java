package com.codigo.examen_ss.controller;

import com.codigo.examen_ss.aggregates.constants.Constants;
import com.codigo.examen_ss.aggregates.request.UserRequest;
import com.codigo.examen_ss.aggregates.response.BaseResponse;
import com.codigo.examen_ss.entity.UserEntity;
import com.codigo.examen_ss.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/examensbss_lin/user/v1")
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationService authenticationService;

    @GetMapping("users/listar")
    public ResponseEntity<BaseResponse<List<UserEntity>>> listarUsuarios() {
        BaseResponse<List<UserEntity>> response = authenticationService.listarUsuarios().getBody();
        if (response.getCode().equals(Constants.OK_DNI_CODE)) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("users/{dni}")
    public ResponseEntity<BaseResponse<UserEntity>> buscarUsuarioDni(@PathVariable("dni") String dni) throws Exception {
        BaseResponse<UserEntity> response = authenticationService.buscarUsuarioDni(dni).getBody();
        if (response.getCode().equals(Constants.OK_DNI_CODE)) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("users/actualizar/{id}")
    public ResponseEntity<BaseResponse<UserEntity>> actualizarUsuario(@PathVariable("id") Long id, @RequestBody UserRequest userRequest) {
        BaseResponse<UserEntity> response = authenticationService.actualizarUsuario(id, userRequest).getBody();
        if (response.getCode().equals(Constants.OK_DNI_CODE)) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
