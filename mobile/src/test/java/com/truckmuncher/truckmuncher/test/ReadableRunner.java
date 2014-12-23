package com.truckmuncher.truckmuncher.test;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class ReadableRunner extends BlockJUnit4ClassRunner {

    public ReadableRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected String testName(FrameworkMethod method) {
        return StringUtils.humanize(method.getName());
    }
}
