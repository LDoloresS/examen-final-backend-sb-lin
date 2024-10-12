package com.codigo.examen_ss.aggregates.DTO;

import com.codigo.examen_ss.entity.Rol;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserRedisDTO {
    private String id;
    private String nombres;
    private String apPaterno;
    private String apMaterno;
    private String tipoDoc;
    private String numDoc;
    private String email;
    private String isEnabled;
    private String rolId;
    private String rolName;

}
