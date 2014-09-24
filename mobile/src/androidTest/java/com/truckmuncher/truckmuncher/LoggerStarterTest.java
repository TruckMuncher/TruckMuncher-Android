package com.truckmuncher.truckmuncher;

import android.content.Context;
import android.test.AndroidTestCase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import timber.log.Timber;

import static org.assertj.core.api.Assertions.assertThat;

public class LoggerStarterTest extends AndroidTestCase {

    public void testStartMethodIsSynchronized() throws NoSuchMethodException {
        Method method = LoggerStarter.class.getMethod("start", Context.class);
        assertThat(Modifier.isSynchronized(method.getModifiers())).isTrue();
    }

    public void testLoggersAreOnlyBeSetupOnce() throws NoSuchFieldException, IllegalAccessException {
        LoggerStarter.start(getContext());
        LoggerStarter.start(getContext());

        Field loggers = Timber.class.getDeclaredField("FOREST");
        loggers.setAccessible(true);
        List<Timber.Tree> trees = (List<Timber.Tree>) loggers.get(null);
        assertThat(trees).hasSize(1);
    }
}
