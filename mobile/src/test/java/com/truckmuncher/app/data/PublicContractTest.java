package com.truckmuncher.app.data;

import android.net.Uri;

import com.truckmuncher.app.BuildConfig;
import com.truckmuncher.testlib.ReadableRobolectricTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import static com.truckmuncher.app.test.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ReadableRobolectricTestRunner.class)
public class PublicContractTest {

    @Test
    public void authorityIsAsExpected() {
        assertThat(PublicContract.CONTENT_AUTHORITY).isEqualTo(BuildConfig.APPLICATION_ID + ".provider");
    }

    @Test
    public void urisHaveCorrectAuthority() throws IllegalAccessException {
        Field[] fields = PublicContract.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().endsWith("_URI")) {
                Uri uriField = (Uri) field.get(null);
                assertThat(uriField).hasAuthority(PublicContract.CONTENT_AUTHORITY);
            }
        }
    }

    @Test
    public void urisHaveContentScheme() throws IllegalAccessException {
        Field[] fields = PublicContract.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().endsWith("_URI")) {
                Uri uriField = (Uri) field.get(null);
                assertThat(uriField).hasContentScheme();
            }
        }
    }

    @Test
    public void uriPathMatchesTableName() throws IllegalAccessException {
        Field[] fields = PublicContract.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().endsWith("_URI")) {
                String tableName = field.getName().replaceAll("_URI", "");

                Uri uriField = (Uri) field.get(null);
                assertThat(uriField).hasPath("/" + tableName);
            }
        }
    }

    @Test
    public void typeMatchesTableName() throws IllegalAccessException {
        Field[] fields = PublicContract.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().startsWith("URI_TYPE")) {
                String tableName = field.getName().replaceAll("URI_TYPE_", "").toLowerCase(Locale.US);

                String type = (String) field.get(null);
                assertThat(type).isEqualTo("vnd.android.cursor.dir/vnd.truckmuncher." + tableName);
            }
        }
    }

    @Test
    public void convertListToStringWorks() {
        // Test none
        assertThat(PublicContract.convertListToString(Collections.<String>emptyList()))
                .isEmpty();

        // Test single
        assertThat(PublicContract.convertListToString(Arrays.asList("cat")))
                .isEqualTo("cat");

        // Test multiple
        assertThat(PublicContract.convertListToString(Arrays.asList("cat", "dog")))
                .isEqualTo("cat,dog");
    }

    @Test
    public void convertStringToListWorks() {
        String input = "cat,dog,horse";
        assertThat(PublicContract.convertStringToList(input)).containsExactly("cat", "dog", "horse");
    }
}
