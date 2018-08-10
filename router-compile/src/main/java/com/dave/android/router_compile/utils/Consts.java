package com.dave.android.router_compile.utils;

/**
 * @author rendawei
 * @date 2018/4/9
 */
public class Consts {

    public static final String PROJECT = "RouterProcessor";
    // Log
    static final String PREFIX_OF_LOGGER = PROJECT + "::Compiler ";

    // Options of processor
    public static final String KEY_MODULE_NAME = "moduleName";
    public static final String KEY_OUT_PATH = "routerHtmlPath";
    public static final String KEY_TEMPLATE_PATH = "routerTemplatePath";
    public static final String KEY_DOC_TYPE = "routerDocType";

    // System interface
    public static final String ACTIVITY = "android.app.Activity";
    public static final String FRAGMENT = "android.app.Fragment";
    public static final String FRAGMENT_V4 = "android.support.v4.app.Fragment";
    public static final String SERVICE = "android.app.Service";
    public static final String PARCELABLE = "android.os.Parcelable";

    // Java type
    private static final String LANG = "java.lang";
    public static final String BYTE = LANG + ".Byte";
    public static final String SHORT = LANG + ".Short";
    public static final String INTEGER = LANG + ".Integer";
    public static final String LONG = LANG + ".Long";
    public static final String FLOAT = LANG + ".Float";
    public static final String DOUBEL = LANG + ".Double";
    public static final String BOOLEAN = LANG + ".Boolean";
    public static final String STRING = LANG + ".String";

}
