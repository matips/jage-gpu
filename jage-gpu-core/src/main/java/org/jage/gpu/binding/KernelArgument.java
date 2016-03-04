package org.jage.gpu.binding;

public class KernelArgument {
    private final int argumentIndex;
    private final String argumentName;
    private final ArgumentAccessQualifier accessQualifier;
    private final ArgumentType type;
    private final boolean isIn;
    private final boolean isOut;

    public KernelArgument(int argumentIndex, String argumentName, ArgumentAccessQualifier accessQualifier, ArgumentType type, boolean isIn, boolean isOut) {
        this.argumentIndex = argumentIndex;
        this.argumentName = argumentName;
        this.accessQualifier = accessQualifier;
        this.type = type;
        this.isIn = isIn;
        this.isOut = isOut;
    }

    public String getArgumentName() {
        return argumentName;
    }

    public ArgumentAccessQualifier getAccessQualifier() {
        return accessQualifier;
    }

    public ArgumentType getType() {
        return type;
    }

    public int getArgumentIndex() {
        return argumentIndex;
    }

    public boolean isOut() {
        return isOut;
    }

    public boolean isIn() {
        return isIn;
    }
}
