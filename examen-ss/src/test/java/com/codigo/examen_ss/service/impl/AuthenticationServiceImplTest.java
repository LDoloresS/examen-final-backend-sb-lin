package com.codigo.examen_ss.service.impl;

import com.codigo.examen_ss.aggregates.constants.Constants;
import com.codigo.examen_ss.aggregates.request.UserRequest;
import com.codigo.examen_ss.aggregates.response.BaseResponse;
import com.codigo.examen_ss.aggregates.response.ReniecResponse;
import com.codigo.examen_ss.client.ReniecClient;
import com.codigo.examen_ss.entity.Rol;
import com.codigo.examen_ss.entity.UserEntity;
import com.codigo.examen_ss.repository.RolRepository;
import com.codigo.examen_ss.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class AuthenticationServiceImplTest {
    //Simular la DB y Objetos
    @Mock
    private UserRepository userRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private ReniecClient reniecClient;
    @InjectMocks
    private AuthenticationServiceImpl usuarioService;


    //Se ejecuta antes de cada prueba
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearUsuarioNuevo() throws Exception {
        //ARRANGE
        UserRequest request = new UserRequest();
        request.setNumDoc("12345678");
        request.setEmail("ejemplo@test.com");
        request.setPassword("123456abc");
        UserEntity userEntity = new UserEntity();
        Rol rol = new Rol(1L, "ADMIN");
        request.setRol(rol.getNombre());
        ReniecResponse responseReniec = new ReniecResponse();
        responseReniec.setNumeroDocumento(request.getNumDoc());

        //CUANDO EJECUTES... debes devolver...
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(rolRepository.findByNombre("ADMIN")).thenReturn(Optional.of(rol));
        when(reniecClient.getPersonaReniec(anyString(), anyString())).thenReturn(responseReniec);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        //ACT ->EJECUTAR EL MÉTODO A PROBAR
        ResponseEntity<BaseResponse<UserEntity>> response = usuarioService.crearUsuario(request);

        //ASSERT
        assertEquals(Constants.OK_DNI_CODE, response.getBody().getCode(),
                "El Código de respuesta debe ser igual al 2001");
        assertEquals(Constants.OK_DNI_MESS, response.getBody().getMessage(),
                "El Mensaje de respuesta debe ser igual MSJ_OK de Constants");
        assertTrue(response.getBody().getObjeto().isPresent());
        assertSame(userEntity, response.getBody().getObjeto().get());
    }

}