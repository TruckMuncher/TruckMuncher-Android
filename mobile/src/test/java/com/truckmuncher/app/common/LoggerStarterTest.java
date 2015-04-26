package com.truckmuncher.app.common;

import com.truckmuncher.testlib.ReadableRobolectricTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.List;

import timber.log.Timber;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ReadableRobolectricTestRunner.class)
public class LoggerStarterTest {

    @Test
    public void loggersAreOnlySetupOnce() throws NoSuchFieldException, IllegalAccessException {
        LoggerStarter.start();
        LoggerStarter.start();

        Field loggers = Timber.class.getDeclaredField("FOREST");
        loggers.setAccessible(true);
        List<Timber.Tree> trees = (List<Timber.Tree>) loggers.get(null);
        assertThat(trees).hasSize(1);
    }
}
