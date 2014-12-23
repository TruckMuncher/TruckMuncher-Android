package com.truckmuncher.truckmuncher.test;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class ReadableRunner extends BlockJUnit4ClassRunner {

    public ReadableRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    public static String humanize(String camelCase) {
        char[] chars = camelCase.toCharArray();
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toUpperCase(chars[0]));
        for (int i = 1; i < chars.length; i++) {
            if (Character.isUpperCase(chars[i])) {
                sb.append(' ');
            }
            sb.append(chars[i]);
        }
        return sb.toString();
    }

    @Override
    protected String testName(FrameworkMethod method) {
        return humanize(method.getName());
    }
}
