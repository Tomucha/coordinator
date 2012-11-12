package cz.clovekvtisni.coordinator.android.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

public class FileTool {

    protected FileTool() {
    }

    public static byte[] getFileContent(InputStream fis) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        copy(fis, bos);
        return bos.toByteArray();
    }

    public static byte[] getFileContent(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        return getFileContent(fis);
    }

    public static void copy(InputStream fis, OutputStream bos) throws IOException {
        byte[] buffer = new byte[2048];
        int read = fis.read(buffer);
        while (read != -1) {
            bos.write(buffer, 0, read);
            read = fis.read(buffer);
        }
        fis.close();
        bos.close();
    }

    public static void copyNoClose(InputStream fis, OutputStream bos) throws IOException {
        byte[] buffer = new byte[2048];
        int read = fis.read(buffer);
        while (read != -1) {
            bos.write(buffer, 0, read);
            read = fis.read(buffer);
        }
    }

    public static boolean isExternalStorageWritable() {
        final String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean deleteDirectoryWithFiles(File directory) {
        boolean allOk = true;
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    allOk = allOk && deleteDirectoryWithFiles(file);
                }
                else {
                    allOk= allOk && file.delete();
                }
            }
        }
        return allOk && directory.delete();
    }
}
