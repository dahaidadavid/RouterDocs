package com.dave.android.router_compile.utils;


import static com.dave.android.router_compile.utils.Consts.BOOLEAN;
import static com.dave.android.router_compile.utils.Consts.BYTE;
import static com.dave.android.router_compile.utils.Consts.DOUBEL;
import static com.dave.android.router_compile.utils.Consts.FLOAT;
import static com.dave.android.router_compile.utils.Consts.INTEGER;
import static com.dave.android.router_compile.utils.Consts.LONG;
import static com.dave.android.router_compile.utils.Consts.PARCELABLE;
import static com.dave.android.router_compile.utils.Consts.SHORT;
import static com.dave.android.router_compile.utils.Consts.STRING;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Utils for type exchange
 *
 * @author zhilong <a href="mailto:zhilong.lzl@alibaba-inc.com">Contact me.</a>
 * @version 1.0
 * @since 2017/2/21 下午1:06
 */
public class TypeUtils {

    private Types types;
    private Elements elements;
    private TypeMirror parcelableType;

    public TypeUtils(Types types, Elements elements) {
        this.types = types;
        this.elements = elements;

        parcelableType = this.elements.getTypeElement(PARCELABLE).asType();
    }

    /**
     * Diagnostics out the true java type
     *
     * @param element Raw type
     * @return Type class of java
     */
    public int typeExchange(Element element) {
        TypeMirror typeMirror = element.asType();

        // Primitive
        if (typeMirror.getKind().isPrimitive()) {
            return element.asType().getKind().ordinal();
        }

        switch (typeMirror.toString()) {
            case BYTE:
                return TypeKind.BYTE.ordinal();
            case SHORT:
                return TypeKind.SHORT.ordinal();
            case INTEGER:
                return TypeKind.INT.ordinal();
            case LONG:
                return TypeKind.LONG.ordinal();
            case FLOAT:
                return TypeKind.FLOAT.ordinal();
            case DOUBEL:
                return TypeKind.DOUBLE.ordinal();
            case BOOLEAN:
                return TypeKind.BOOLEAN.ordinal();
            case STRING:
                return TypeKind.STRING.ordinal();
            default:    // Other side, maybe the PARCELABLE or OBJECT.
                if (types.isSubtype(typeMirror, parcelableType)) {  // PARCELABLE
                    return TypeKind.PARCELABLE.ordinal();
                } else {    // For others
                    return TypeKind.OBJECT.ordinal();
                }
        }
    }
}
