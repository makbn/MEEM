package io.github.makbn.meem.utils;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Properties;

public class Config {


    public static void fixEncoding() {
        Properties props = System.getProperties();
        props.setProperty("Dfile.encoding", "UTF-8");
        System.setProperty("file.encoding", "UTF-8");
        Field charset;
        try {
            charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
