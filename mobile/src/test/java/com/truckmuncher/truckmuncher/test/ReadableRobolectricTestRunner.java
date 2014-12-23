package com.truckmuncher.truckmuncher.test;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;

public class ReadableRobolectricTestRunner extends RobolectricTestRunner {

    public ReadableRobolectricTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected String testName(FrameworkMethod method) {
        return StringUtils.humanize(method.getName());
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        String manifestProperty = "src/main/AndroidManifest.xml";
        String resProperty = "src/main/res";
        return new AndroidManifest(Fs.fileFromPath(manifestProperty), Fs.fileFromPath(resProperty)) {
            @Override
            public int getTargetSdkVersion() {
                return 18;
            }
        };
    }
}