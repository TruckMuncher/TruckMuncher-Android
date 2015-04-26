package com.truckmuncher.app.test.rules;

import android.app.Application;
import android.support.test.InstrumentationRegistry;

import com.truckmuncher.app.App;
import com.truckmuncher.app.dagger.Modules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dagger.ObjectGraph;

/**
 * @ JUnit 4 Rule to generate a new object graph and replace the one that the {@link App}
 * is using. This rule always uses the modules provided by {@link Modules#list(Application)}.
 */
public class GraphReplacementRule implements TestRule {

    public GraphReplacementRule(Object... modules) {
        App app = App.get(InstrumentationRegistry.getTargetContext());

        List<Object> combinedModules = new ArrayList<>(Arrays.asList(Modules.list(app)));
        Collections.addAll(combinedModules, modules);

        try {
            // Use reflection to set the app graph. Not great, but hey, this is Android
            Field graphField = App.class.getDeclaredField("objectGraph");
            graphField.setAccessible(true);
            graphField.set(app, ObjectGraph.create(combinedModules.toArray()));
        } catch (NoSuchFieldException ignored) {
            throw new AssertionError("The field was probably renamed in the Application class");
        } catch (IllegalAccessException ignored) {
            throw new AssertionError("We're already setting the field as accessible");
        }
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                base.evaluate();
            }
        };
    }
}
