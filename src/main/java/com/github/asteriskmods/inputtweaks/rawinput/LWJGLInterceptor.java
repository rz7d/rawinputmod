package com.github.asteriskmods.inputtweaks.rawinput;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import com.github.asteriskmods.inputtweaks.InputTweaks;
import lombok.SneakyThrows;

public final class LWJGLInterceptor {

    private static final int WM_INPUT = 0x00FF;

    private static final Class<?> WindowsDisplay;
    private static final Method handleMessage;
    private static final MethodHandle METHOD_handleMessage;
    private static final Method defWindowProc;
    private static final MethodHandle METHOD_defWindowProc;

    private static final Method setWindowProc;
    private static boolean first = true;

    static {
        try {
            WindowsDisplay = Class.forName("org.lwjgl.opengl.WindowsDisplay");

            handleMessage = WindowsDisplay.getDeclaredMethod("handleMessage", long.class, int.class, long.class, long.class, long.class);
            handleMessage.setAccessible(true);
            METHOD_handleMessage = MethodHandles.lookup().unreflect(handleMessage);

            defWindowProc = WindowsDisplay.getDeclaredMethod("defWindowProc", long.class, int.class, long.class, long.class);
            defWindowProc.setAccessible(true);
            METHOD_defWindowProc = MethodHandles.lookup().unreflect(defWindowProc);

            setWindowProc = WindowsDisplay.getDeclaredMethod("setWindowProc", Method.class);
            setWindowProc.setAccessible(true);
        } catch (ReflectiveOperationException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    @SneakyThrows
    public static void replaceWindowProc() {
        Method windowProc = LWJGLInterceptor.class.getDeclaredMethod("interceptWndProc", long.class, int.class, long.class, long.class, long.class);
        setWindowProc.invoke(null, windowProc);
        WindowsNatives.init();
    }

    @SneakyThrows
    private static long interceptWndProc(long hwnd, int msg, long wParam, long lParam, long millis) {
        if (InputTweaks.isRawInput) {
            if (first) {
                first = false;
                WindowsNatives.registerRawInputDevices(hwnd);
            }
            if (msg == WM_INPUT) {
                WindowsNatives.rawInputWindowProc(hwnd, msg, wParam, lParam);
                return (long) METHOD_defWindowProc.invokeExact(hwnd, msg, wParam, lParam);
            }
        }
        return (long) METHOD_handleMessage.invokeExact(hwnd, msg, wParam, lParam, millis);
    }

    private LWJGLInterceptor() { }

}
