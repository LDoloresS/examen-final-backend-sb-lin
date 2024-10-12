package com.codigo.examen_ss.controller;

import com.codigo.examen_ss.aggregates.constants.Constants;
import com.codigo.examen_ss.aggregates.response.BaseResponse;
import com.codigo.examen_ss.entity.UserEntity;
import com.codigo.examen_ss.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/examensbss_lin/admin/v1")
@RequiredArgsConstructor
public class AdminController {
    private final AuthenticationService authenticationService;

    @GetMapping("/users/listar")
    public ResponseEntity<BaseResponse<List<UserEntity>>> listarUsuariosAll() {
        BaseResponse<List<UserEntity>> response = authenticationService.listarUsuariosAll().getBody();
        if (response.getCode().equals(Constants.OK_DNI_CODE)) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/users/eliminar/{id}")
    public ResponseEntity<BaseResponse<UserEntity>> eliminarUsuario(@PathVariable("id") Long id) {
        BaseResponse<UserEntity> response = authenticationService.eliminarUsuario(id).getBody();
        if (response.getCode().equals(Constants.OK_DNI_CODE)) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
