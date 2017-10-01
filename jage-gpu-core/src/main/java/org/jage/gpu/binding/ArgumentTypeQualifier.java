package org.jage.gpu.binding;

import static org.jocl.CL.*;

/**
 * CRIF IT Solutions Poland
 */
public enum ArgumentTypeQualifier {
    CONST(CL_KERNEL_ARG_TYPE_CONST),
    RESTRICT(CL_KERNEL_ARG_TYPE_RESTRICT),
    VOLATILE(CL_KERNEL_ARG_TYPE_VOLATILE),
    NONE(CL_KERNEL_ARG_TYPE_NONE);

    private final long code;

    ArgumentTypeQualifier(long code) {
        this.code = code;
    }

    public static ArgumentTypeQualifier fromCode(long argumentQualifierCode) {
        for (ArgumentTypeQualifier argumentTypeQualifier : values()) {
            if (argumentTypeQualifier.code == argumentQualifierCode) {
                return argumentTypeQualifier;
            }
        }
        return NONE; //todo: allow to return multiple responses
//        throw new RuntimeException("INVALID cl_kernel_arg_access_qualifier: " + argumentQualifierCode);
    }
}
