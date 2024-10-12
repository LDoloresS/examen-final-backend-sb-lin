package com.codigo.ms_inventory.aggregates.constants;

public class Constants {
    public static final Integer OK_PRODUCT_CODE = 2000;
    public static final String OK_PRODUCT_MESS = "REGISTRADO XVR!";
    public static final Integer ERROR_PRODUCT_CODE = 2004;
    public static final String ERROR_PRODUCT_MESS = "ERROR CON EL PRODUCT_ID";
    public static final String REDIS_KEY_API_INVENTORY = "MS:INVENTORY:";
    public static final Integer REDIS_EXP = 5;
}
