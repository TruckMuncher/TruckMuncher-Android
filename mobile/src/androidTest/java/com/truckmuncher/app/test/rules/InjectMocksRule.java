package com.truckmuncher.app.test.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.MockitoAnnotations;

/**
 * @ JUnit 4 rule to inject Mockito {@link org.mockito.Mock Mocks}
 */
public class InjectMocksRule implements TestRule {

    private final Object objectToInitialize;

    public InjectMocksRule(Object toInitialize) {
        objectToInitialize = toInitialize;
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                MockitoAnnotations.initMocks(objectToInitialize);
                base.evaluate();
            }
        };
    }
}
