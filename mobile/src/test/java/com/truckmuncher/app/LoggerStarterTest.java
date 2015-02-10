package com.truckmuncher.app;

import android.content.Context;

import com.truckmuncher.testlib.ReadableRobolectricTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import timber.log.Timber;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ReadableRobolectricTestRunner.class)
public class LoggerStarterTest {

    @Test
    public void startMethodIsSynchronized() throws NoSuchMethodException {
        Method method = LoggerStarter.class.getMethod("start", Context.class);
        assertThat(Modifier.isSynchronized(method.getModifiers())).isTrue();
    }

    @Test
    public void loggersAreOnlySetupOnce() throws NoSuchFieldException, IllegalAccessException {
        LoggerStarter.start(Robolectric.application);
        LoggerStarter.start(Robolectric.application);

        Field loggers = Timber.class.getDeclaredField("FOREST");
        loggers.setAccessible(true);
        List<Timber.Tree> trees = (List<Timber.Tree>) loggers.get(null);
        assertThat(trees).hasSize(1);
    }
}
