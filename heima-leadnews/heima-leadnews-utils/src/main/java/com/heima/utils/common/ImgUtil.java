package com.heima.utils.common;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class ImgUtil {



    public static String detect(InputStream in) throws IOException {
        byte[] header = new byte[8];
        int read = in.read(header);

        if (read < 2) {
            return "unknown";
        }

        if ((header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8) {
            return "jpg";
        }
        if ((header[0] & 0xFF) == 0x89 && (header[1] & 0xFF) == 0x50) {
            return "png";
        }
        if ((header[0] & 0xFF) == 0x47 && (header[1] & 0xFF) == 0x49) {
            return "gif";
        }
        if ((header[0] & 0xFF) == 0x42 && (header[1] & 0xFF) == 0x4D) {
            return "bmp";
        }
        return "unknown";
    }

}
