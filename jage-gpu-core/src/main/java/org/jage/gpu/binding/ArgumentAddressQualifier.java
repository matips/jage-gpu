package org.jage.gpu.binding;

import org.jocl.CL;

/**
 * CRIF IT Solutions Poland
 */
public enum ArgumentAddressQualifier {
    CONSTANT(CL.CL_KERNEL_ARG_ADDRESS_CONSTANT),
    PRIVATE(CL.CL_KERNEL_ARG_ADDRESS_PRIVATE),
    LOCAL(CL.CL_KERNEL_ARG_ADDRESS_LOCAL),
    GLOBAL(CL.CL_KERNEL_ARG_ADDRESS_GLOBAL);

    private final int code;

    ArgumentAddressQualifier(int code) {
        this.code = code;
    }

    public static ArgumentAddressQualifier fromCode(int accessQualifierCode) {
        for (ArgumentAddressQualifier argumentAccessQualifier : values()) {
            if (argumentAccessQualifier.code == accessQualifierCode) {
                return argumentAccessQualifier;
            }
        }
        throw new RuntimeException("INVALID cl_kernel_arg_address_qualifier: " + accessQualifierCode);
    }
}
