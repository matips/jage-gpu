package org.jage.gpu.binding.jocl;

import org.jage.gpu.helpers.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class JoclKernelSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(JoclKernelSource.class);
    final String kernelSource;
    final String kernelName;
    private final Pattern sourceFilePattern = Pattern.compile("__kernel\\s+void\\s+(\\w+)\\s*\\((.*?)\\)", Pattern.DOTALL);
    private final Pattern argPattern = Pattern.compile("(?:(?:(?:__)?(?:local|global|private|constant|const|unsigned|volatile))\\s+)*(\\w+)((?:\\s*\\**\\s*)|\\s+)(\\w+)", Pattern.DOTALL);
    private final ArrayList<String> types = new ArrayList<>();


    JoclKernelSource(String kernelFileContent, String kernelName) {
        this.kernelSource = includeHeadres(kernelFileContent);
        this.kernelName = kernelName;

    }

    private void init() {
        Matcher regexMatcher = sourceFilePattern.matcher(kernelSource);
        while (regexMatcher.find()) {
            String name = regexMatcher.group(1);
            if (name.equals(kernelName)) {
                String argumentsDef = regexMatcher.group(2);
                String[] allArguments = argumentsDef.split(",");
                for (String argumentDef : allArguments) {
                    Matcher argMatch = argPattern.matcher(argumentDef.trim());
                    argMatch.find();
                    String typeName = argMatch.group(1) + Optional.ofNullable(argMatch.group(2)).orElse("").trim();
                    types.add(typeName.trim());
                }
            }
        }
    }

    public String getSource() {
        return kernelSource;
    }

    /**
     * Try to include files from classpath
     */
    private String includeHeadres(String kernelFileContent) {
        Pattern p = Pattern.compile("^#include\\s*\"(.*)\"$", Pattern.MULTILINE);

        StringBuffer resultString = new StringBuffer();
        Matcher regexMatcher = p.matcher(kernelFileContent);
        while (regexMatcher.find()) {
            String fileName = regexMatcher.group(1);

            String replacement;
            try {
                replacement = Utils.getResourceAsString(fileName);
            } catch (IOException | NullPointerException e) {
                LOGGER.warn("Cannot find file " + fileName);
                replacement = regexMatcher.group();
            }
            regexMatcher.appendReplacement(resultString, replacement);
        }
        regexMatcher.appendTail(resultString);
        return resultString.toString();
    }

    /**
     * This is walk around, because clGetKernelArgInfo is not reliable
     *
     * @param paramNumber
     * @return
     */
    String loadDeclaredArgumentType(int paramNumber) {
        if (types.isEmpty())
            init();
        return types.get(paramNumber);
    }

}
