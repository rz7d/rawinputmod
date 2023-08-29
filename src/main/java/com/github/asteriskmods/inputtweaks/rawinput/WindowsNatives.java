package com.github.asteriskmods.inputtweaks.rawinput;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import lombok.SneakyThrows;

public final class WindowsNatives {

    @SneakyThrows
    static void init() {
        InputStream nativeLib = WindowsNatives.class.getResourceAsStream("/inputtweaks.dll");
        if (nativeLib == null) {
            throw new InternalError();
        }
        Path tempLib = Files.createTempFile("native-", "-inputtweaks.dll");
        Files.copy(nativeLib, tempLib, StandardCopyOption.REPLACE_EXISTING);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Files.deleteIfExists(tempLib);
            } catch (IOException ignored) {
            }
        }));
        System.load(tempLib.toAbsolutePath().toString());

        Method updateMouseDelta = WindowsNatives.class.getDeclaredMethod("updateMouseDeltaCallback", int.class, int.class);
        setMouseDeltaCallback(updateMouseDelta);
    }

    static native boolean registerRawInputDevices(long hWnd);

    static native void rawInputWindowProc(long hWnd, int msg, long wParam, long lParam);

    private static native void setMouseDeltaCallback(Method updateMouseDeltaCallback);

    private static void updateMouseDeltaCallback(int x, int y) {
        RawInputMouseHelper.dx += x;
        RawInputMouseHelper.dy += y;
    }

    private WindowsNatives() {
    }

}
