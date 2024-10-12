package com.codigo.ms_order.constants;

public class Constants {
    public static final Integer OK_ORDER_CODE = 2000;
    public static final String OK_ORDER_MESS = "REGISTRADO XVR!";
    public static final String STATUS_PENDIENTE = "PENDIENTE";
    public static final String STATUS_PROCESADO = "PROCESADO";
    public static final Integer ERROR_ORDER_CODE = 2004;
    public static final String ERROR_ORDER_MESS = "ERROR EN LA ORDEN";
    public static final Integer ERROR_CODE_LIST_EMPTY = 2005;
    public static final String ERROR_MESS_LIST_EMPTY = "NO HAY REGISTROS!!!";
    public static final Integer ERROR_CODE_PROCESADO = 2004;
    public static final String ERROR_MESS_PROCESADO = "ERROR AL PROCESAR ORDEN";
    public static final Integer ERROR_UPD_CODE = 2006;
    public static final String ERROR_UPD_MESS = "ERROR AL ACTUALIZAR ORDEN";
    public static final String REDIS_KEY_API_ORDER = "MS:ORDER:";
    public static final Integer REDIS_EXP = 5;
}
