package com.codigo.examen_ss.service.impl;

import com.codigo.examen_ss.aggregates.DTO.UserRedisDTO;
import com.codigo.examen_ss.aggregates.constants.Constants;
import com.codigo.examen_ss.aggregates.request.SignInRequest;
import com.codigo.examen_ss.aggregates.request.UserRequest;
import com.codigo.examen_ss.aggregates.response.BaseResponse;
import com.codigo.examen_ss.aggregates.response.ReniecResponse;
import com.codigo.examen_ss.aggregates.response.SignInResponse;
import com.codigo.examen_ss.client.ReniecClient;
import com.codigo.examen_ss.entity.Rol;
import com.codigo.examen_ss.entity.Role;
import com.codigo.examen_ss.entity.UserEntity;
import com.codigo.examen_ss.redis.RedisService;
import com.codigo.examen_ss.repository.RolRepository;
import com.codigo.examen_ss.repository.UserRepository;
import com.codigo.examen_ss.service.AuthenticationService;
import com.codigo.examen_ss.service.JwtService;
import com.codigo.examen_ss.service.UserService;
import com.codigo.examen_ss.util.Utils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final ReniecClient reniecClient;
    private final RedisService redisService;
    private final UserService userService;
    private final RolRepository rolRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationServiceImpl(UserRepository userRepository, ReniecClient reniecClient,
                                     RedisService redisService, UserService userService, RolRepository rolRepository,
                                     AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.reniecClient = reniecClient;
        this.redisService = redisService;
        this.userService = userService;
        this.rolRepository = rolRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Value("${token.api}")
    private String tokenApi;

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<UserEntity>> crearUsuario(UserRequest userRequest) throws Exception {
        BaseResponse<UserEntity> baseResponse = new BaseResponse<>();
        try {
            boolean existEmail = userRepository.existsByEmail(userRequest.getEmail());
            Rol rol = getRoles(Role.valueOf(userRequest.getRol()));
            if (existEmail || Objects.isNull(rol) || rol.equals("")) {
                return null;
            }
            UserEntity userEntity = getUsuarioEntity(userRequest);
            if (Objects.nonNull(userEntity)) {
                userEntity.setRoles(Collections.singleton(rol));
                userEntity.setPassword(new BCryptPasswordEncoder().encode(userRequest.getPassword()));
                userEntity.setIsEnabled(Constants.STATUS_ACTIVE);

                baseResponse.setCode(Constants.OK_DNI_CODE);
                baseResponse.setMessage(Constants.OK_DNI_MESS);
                baseResponse.setObjeto(Optional.of(userRepository.save(userEntity)));
            }
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            baseResponse.setCode(Constants.ERROR_DNI_CODE);
            baseResponse.setMessage(Constants.ERROR_DNI_MESS + " -> " + e.getMessage());
            baseResponse.setObjeto(Optional.empty());
            return ResponseEntity.ok(baseResponse);
        }
    }

    @Override
    public ResponseEntity<BaseResponse<List<UserEntity>>> listarUsuarios() {
        BaseResponse<List<UserEntity>> baseResponse = new BaseResponse<>();
        List<UserEntity> userEntityList = userRepository.findByIsEnabled(Constants.STATUS_ACTIVE);
        if (Objects.nonNull(userEntityList)) {
            baseResponse.setCode(Constants.OK_DNI_CODE);
            baseResponse.setMessage(Constants.OK_DNI_MESS);
            baseResponse.setObjeto(Optional.of(userEntityList));
        } else {
            baseResponse.setCode(Constants.ERROR_CODE_LIST_EMPTY);
            baseResponse.setMessage(Constants.ERROR_MESS_LIST_EMPTY);
            baseResponse.setObjeto(Optional.empty());
        }
        return ResponseEntity.ok(baseResponse);
    }

    @Override
    public ResponseEntity<BaseResponse<List<UserEntity>>> listarUsuariosAll() {
        BaseResponse<List<UserEntity>> baseResponse = new BaseResponse<>();
        List<UserEntity> userEntityList = userRepository.findAll();
        if (Objects.nonNull(userEntityList)) {
            baseResponse.setCode(Constants.OK_DNI_CODE);
            baseResponse.setMessage(Constants.OK_DNI_MESS);
            baseResponse.setObjeto(Optional.of(userEntityList));
        } else {
            baseResponse.setCode(Constants.ERROR_CODE_LIST_EMPTY);
            baseResponse.setMessage(Constants.ERROR_MESS_LIST_EMPTY);
            baseResponse.setObjeto(Optional.empty());
        }
        return ResponseEntity.ok(baseResponse);
    }

    @Override
    public ResponseEntity<BaseResponse<UserEntity>> buscarUsuarioDni(String dni) throws Exception {
        BaseResponse<UserEntity> baseResponse = new BaseResponse<>();
        try {
            Optional<UserEntity> usuarioBuscar = executionBuscarUsuarioDni(dni);
            if (usuarioBuscar.isPresent()) {
                baseResponse.setCode(Constants.OK_DNI_CODE);
                baseResponse.setMessage(Constants.OK_DNI_MESS);
                baseResponse.setObjeto(usuarioBuscar);
            } else {
                baseResponse.setCode(Constants.ERROR_DNI_CODE);
                baseResponse.setMessage(Constants.ERROR_DNI_MESS);
                baseResponse.setObjeto(Optional.empty());
            }
            return ResponseEntity.ok(baseResponse);
        } catch (Exception e) {
            baseResponse.setCode(Constants.ERROR_DNI_CODE);
            baseResponse.setMessage(Constants.ERROR_DNI_MESS + " -> " + e.getMessage());
            baseResponse.setObjeto(Optional.empty());
            return ResponseEntity.ok(baseResponse);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<UserEntity>> actualizarUsuario(Long id, UserRequest userRequest) {
        BaseResponse<UserEntity> baseResponse = new BaseResponse<>();
        Optional<UserEntity> usuarioExistente = userRepository.findById(id);
        Rol rol = getRoles(Role.valueOf(userRequest.getRol()));
        if (usuarioExistente.isEmpty() || Objects.isNull(rol) || rol.equals("")) {
            return null;
        }
        UserEntity usuarioActualizar = getUsuarioEntityUpdate(userRequest, usuarioExistente.get());
        if (Objects.nonNull(usuarioActualizar)) {
            usuarioActualizar.setRoles(Collections.singleton(rol));
            usuarioActualizar.setPassword(new BCryptPasswordEncoder().encode(userRequest.getPassword()));
            usuarioActualizar.setIsEnabled(Constants.STATUS_ACTIVE);
            baseResponse.setCode(Constants.OK_DNI_CODE);
            baseResponse.setMessage(Constants.OK_DNI_MESS);

            baseResponse.setObjeto(Optional.of(userRepository.save(usuarioActualizar)));
        } else {
            baseResponse.setCode(Constants.ERROR_CODE_UPD);
            baseResponse.setMessage(Constants.ERROR_MESS_UPD);
            baseResponse.setObjeto(Optional.empty());
        }
        return ResponseEntity.ok(baseResponse);
    }

    @Override
    public ResponseEntity<BaseResponse<UserEntity>> eliminarUsuario(Long id) {
        BaseResponse<UserEntity> baseResponse = new BaseResponse<>();
        if (userRepository.existsById(id)) {
            UserEntity usuarioRecuperado = userRepository.findById(id).orElse(null);
            usuarioRecuperado.setIsEnabled(Constants.STATUS_INACTIVE);
            usuarioRecuperado.setUsuaDele(Constants.USU_CREA);
            usuarioRecuperado.setDateDele(new Timestamp(System.currentTimeMillis()));
            baseResponse.setCode(Constants.OK_DNI_CODE);
            baseResponse.setMessage(Constants.OK_DNI_MESS);
            baseResponse.setObjeto(Optional.of(userRepository.save(usuarioRecuperado)));
        } else {
            baseResponse.setCode(Constants.ERROR_CODE_DEL);
            baseResponse.setMessage(Constants.ERROR_MESS_DEL);
            baseResponse.setObjeto(Optional.empty());
        }
        return ResponseEntity.ok(baseResponse);
    }

    @Override
    public ResponseEntity<BaseResponse<SignInResponse>> signIn(SignInRequest signInRequest) throws Exception {
        BaseResponse<SignInResponse> baseResponse = new BaseResponse<>();
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.getEmail(), signInRequest.getPassword()));
        if (auth.isAuthenticated()) {
            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(signInRequest.getEmail());
            var token = jwtService.generateToken(userDetails);
            SignInResponse response = new SignInResponse();
            response.setToken(token);
            baseResponse.setCode(Constants.OK_DNI_CODE);
            baseResponse.setMessage(Constants.OK_DNI_MESS);
            baseResponse.setObjeto(Optional.of(response));
        } else {
            baseResponse.setCode(Constants.ERROR_CODE_LOGIN);
            baseResponse.setMessage(Constants.ERROR_MESS_LOGIN);
            baseResponse.setObjeto(Optional.empty());
        }
        return ResponseEntity.ok(baseResponse);
    }

    @Override
    public boolean validateToken(String token) {
        return Optional.ofNullable(token)
                .filter(Utils::isNotNullOrEmpty)
                .map(t -> t.substring(7))
                .map(jwt -> {
                    String userEmail = jwtService.extractUsername(jwt);
                    return Utils.isNotNullOrEmpty(userEmail) ? Optional.of(userEmail) : Optional.empty();
                })
                .flatMap(userEmailOpt -> userEmailOpt.map(userEmail -> {
                    UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail.toString());
                    return jwtService.validateToken(token.substring(7), userDetails);
                }))
                .orElse(false);
    }

    private UserEntity getUsuarioEntity(UserRequest userRequest) {
        UserEntity userEntity = new UserEntity();
        ReniecResponse response = executionReniec(userRequest.getNumDoc());
        if (Objects.nonNull(response)) {
            userEntity.setNombres(response.getNombres());
            userEntity.setApPaterno(response.getApellidoPaterno());
            userEntity.setApMaterno(response.getApellidoMaterno());
            userEntity.setTipoDoc(response.getTipoDocumento());
            userEntity.setNumDoc(response.getNumeroDocumento());

            userEntity.setEmail(userRequest.getEmail());
            userEntity.setIsAccountNonExpired(Constants.STATUS_ACTIVE);
            userEntity.setIsAccountNonLocked(Constants.STATUS_ACTIVE);
            userEntity.setIsCredentialsNonExpired(Constants.STATUS_ACTIVE);

            userEntity.setUsuaCrea(Constants.USU_CREA);
            userEntity.setDateCrea(new Timestamp(System.currentTimeMillis()));
        }
        return userEntity;
    }

    private ReniecResponse executionReniec(String dni) {
        String auth = "Bearer " + tokenApi;
        return reniecClient.getPersonaReniec(dni, auth);
    }

    private UserEntity getUsuarioEntityUpdate(UserRequest userRequest, UserEntity usuarioDB) {
        UserEntity userEntity = new UserEntity();
        if (userRequest != null) {
            redisService.deleteByKey(Constants.REDIS_KEY_API_PERSON + userRequest.getNumDoc());

            userEntity.setId(usuarioDB.getId());
            userEntity.setNombres(userRequest.getNombres());
            userEntity.setApPaterno(userRequest.getApPaterno());
            userEntity.setApMaterno(userRequest.getApMaterno());
            userEntity.setTipoDoc(userRequest.getTipoDoc());
            userEntity.setNumDoc(userRequest.getNumDoc());

            userEntity.setEmail(userRequest.getEmail());
            userEntity.setIsAccountNonExpired(Constants.STATUS_ACTIVE);
            userEntity.setIsAccountNonLocked(Constants.STATUS_ACTIVE);
            userEntity.setIsCredentialsNonExpired(Constants.STATUS_ACTIVE);

            userEntity.setUsuaUpda(Constants.USU_CREA);
            userEntity.setDateUpda(new Timestamp(System.currentTimeMillis()));
            userEntity.setUsuaCrea(usuarioDB.getUsuaCrea());
            userEntity.setDateCrea(usuarioDB.getDateCrea());
            userEntity.setUsuaDele(usuarioDB.getUsuaDele());
            userEntity.setDateDele(usuarioDB.getDateDele());
        }
        return userEntity;
    }

    private Optional<UserEntity> executionBuscarUsuarioDni(String dni) {
        String redisInfo = redisService.getValueByKey(Constants.REDIS_KEY_API_PERSON + dni);
        if (Objects.nonNull(redisInfo)) {
            UserRedisDTO userRedisDTO = Utils.convertirDesdeString(redisInfo, UserRedisDTO.class);
            UserEntity userEntity = new UserEntity();

            userEntity.setId(Long.valueOf(userRedisDTO.getId()));
            userEntity.setNombres(userRedisDTO.getNombres());
            userEntity.setApPaterno(userRedisDTO.getApPaterno());
            userEntity.setApMaterno(userRedisDTO.getApMaterno());
            userEntity.setTipoDoc(userRedisDTO.getTipoDoc());
            userEntity.setNumDoc(userRedisDTO.getNumDoc());
            userEntity.setEmail(userRedisDTO.getEmail());

            userEntity.setIsEnabled(userRedisDTO.getIsEnabled().equals("ACTIVO"));

            Rol rol = new Rol();
            rol.setId(Long.valueOf(userRedisDTO.getId()));
            rol.setNombre(userRedisDTO.getRolName());
            userEntity.setRoles(Stream.of(rol).collect(Collectors.toSet()));

            return Optional.of(userEntity);
        } else {
            Optional<UserEntity> usuarioBuscar = userRepository.findByNumDoc(dni);
            if (usuarioBuscar.isPresent()) {
                UserEntity userEntity = usuarioBuscar.get();
                UserRedisDTO userRedisDTO = new UserRedisDTO();

                userRedisDTO.setId(userEntity.getId().toString());
                userRedisDTO.setNombres(userEntity.getNombres());
                userRedisDTO.setApPaterno(userEntity.getApPaterno());
                userRedisDTO.setApMaterno(userEntity.getApMaterno());
                userRedisDTO.setTipoDoc(userEntity.getTipoDoc());
                userRedisDTO.setNumDoc(userEntity.getNumDoc());
                userRedisDTO.setEmail(userEntity.getEmail());
                userRedisDTO.setIsEnabled(userEntity.getIsEnabled() ? "ACTIVO" : "INACTIVO");
                userRedisDTO.setRolId(userEntity.getRoles().stream().findFirst().get().getId().toString());
                userRedisDTO.setRolName(userEntity.getRoles().stream().findFirst().get().getNombre().toString());

                String dataForRedis = Utils.convertirAString(userRedisDTO);
                redisService.saveKeyValue(Constants.REDIS_KEY_API_PERSON + dni, dataForRedis, Constants.REDIS_EXP);
            }
            return usuarioBuscar;
        }
    }

    private Rol getRoles(Role rolBuscado) {
        return rolRepository.findByNombre(rolBuscado.name())
                .orElseThrow(() -> new RuntimeException("EL ROL BUSCADO NO EXISTE: "
                        + rolBuscado.name()));
    }
}
