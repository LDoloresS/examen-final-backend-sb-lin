package com.codigo.examen_ss.service;

import com.codigo.examen_ss.aggregates.request.SignInRequest;
import com.codigo.examen_ss.aggregates.request.UserRequest;
import com.codigo.examen_ss.aggregates.response.BaseResponse;
import com.codigo.examen_ss.aggregates.response.SignInResponse;
import com.codigo.examen_ss.entity.UserEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AuthenticationService {
    ResponseEntity<BaseResponse<UserEntity>> crearUsuario(UserRequest userRequest) throws Exception;

    ResponseEntity<BaseResponse<List<UserEntity>>> listarUsuarios();

    ResponseEntity<BaseResponse<List<UserEntity>>> listarUsuariosAll();

    ResponseEntity<BaseResponse<UserEntity>> buscarUsuarioDni(String dni) throws Exception;

    ResponseEntity<BaseResponse<UserEntity>> actualizarUsuario(Long id, UserRequest userRequest);

    ResponseEntity<BaseResponse<UserEntity>> eliminarUsuario(Long id);

    ResponseEntity<BaseResponse<SignInResponse>> signIn(SignInRequest signInRequest) throws Exception;

    boolean validateToken(String token);
}
