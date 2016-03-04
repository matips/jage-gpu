package org.jage.gpu.binding;

public enum ArgumentAccessQualifier {
    READ_ONLY(4512), WRITE_ONLY(4513), READ_WRITE(4514), NONE(4515);

    private final int code;

    ArgumentAccessQualifier(int code) {
        this.code = code;
    }

    public static ArgumentAccessQualifier fromCode(int accessQualifierCode) {
        for (ArgumentAccessQualifier argumentAccessQualifier : values()) {
            if (argumentAccessQualifier.code == accessQualifierCode) {
                return argumentAccessQualifier;
            }
        }
        throw new RuntimeException("INVALID cl_kernel_arg_access_qualifier: " + accessQualifierCode);
    }
}
