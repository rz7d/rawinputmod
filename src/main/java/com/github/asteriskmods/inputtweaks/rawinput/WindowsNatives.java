package com.github.asteriskmods.inputtweaks.rawinput;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import lombok.SneakyThrows;

public class WindowsNatives {

    @SneakyThrows
    static void initNatives() {
        InputStream nativeLib = WindowsNatives.class.getResourceAsStream("/inputtweaks.dll");
        if (nativeLib == null) {
            throw new InternalError();
        }
        Path tempFile = Files.createTempFile("native-", "-inputtweaks.dll");
        Files.copy(nativeLib, tempFile, StandardCopyOption.REPLACE_EXISTING);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException ignored) {
            }
        }));
        System.load(tempFile.toAbsolutePath().toString());

        Method updateMouseDelta = WindowsNatives.class.getDeclaredMethod("updateMouseDelta", int.class, int.class);
        setMouseDeltaHandler(updateMouseDelta);
    }

    private static native void setMouseDeltaHandler(Method updateMouseDelta);

    static native boolean registerRawInputDevices(long hWnd);

    static native void rawInputWindowProc(long hWnd, int msg, long wParam, long lParam);

    private static void updateMouseDelta(int x, int y) {
        RawInputMouseHelper.dx += x;
        RawInputMouseHelper.dy += y;
    }

}
