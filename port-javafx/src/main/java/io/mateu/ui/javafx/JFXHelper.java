package io.mateu.ui.javafx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by miguel on 4/1/17.
 */
public class JFXHelper {

    public static byte[] read(InputStream input) {

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        long count = 0;
        int n = 0;
        try {
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toByteArray();
    }

}
