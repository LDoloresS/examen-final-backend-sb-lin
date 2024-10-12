package com.codigo.examen_ss.controller.advice.personalizado;

public class ApiException extends Exception {
    public ApiException(String mensaje) {
        super(mensaje);
    }
}
