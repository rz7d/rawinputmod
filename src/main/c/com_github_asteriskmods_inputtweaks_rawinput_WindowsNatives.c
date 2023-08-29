#include <Windows.h>
#include "com_github_asteriskmods_inputtweaks_rawinput_WindowsNatives.h"

#define HID_USAGE_PAGE_GENERIC 0x01

#define HID_USAGE_GENERIC_MOUSE 0x02

static jclass javaClass;
static jmethodID javaMouseDeltaCallback;

static __inline BOOL InitRawInput(HWND hWnd)
{
    RAWINPUTDEVICE rid;
    rid.usUsagePage = HID_USAGE_PAGE_GENERIC;
    rid.usUsage = HID_USAGE_GENERIC_MOUSE;
    rid.dwFlags = 0;
    rid.hwndTarget = hWnd;
    return RegisterRawInputDevices(&rid, 1, sizeof(RAWINPUTDEVICE));
}

static __inline void RawInputWindowProc(JNIEnv *env, HWND hWnd, UINT msg, WPARAM wParam, LPARAM lParam)
{
    UINT dwSize = 0;

    GetRawInputData((HRAWINPUT) lParam, RID_INPUT, NULL, &dwSize, sizeof(RAWINPUTHEADER));
    LPBYTE buf = calloc(dwSize, 1);
    if (buf == NULL) {
        return;
    }
    GetRawInputData((HRAWINPUT) lParam, RID_INPUT, buf, &dwSize, sizeof(RAWINPUTHEADER));
    RAWINPUT *raw = (RAWINPUT *) buf;

    BOOL isMouse = raw->header.dwType == RIM_TYPEMOUSE;
    LONG lastX, lastY;
    if (isMouse) {
        lastX = raw->data.mouse.lLastX;
        lastY = raw->data.mouse.lLastY;
    }
    free(buf);
    if (isMouse) {
       (*env)->CallStaticVoidMethod(env, javaClass, javaMouseDeltaCallback, (jint)lastX, (jint)lastY);
    }
}

JNIEXPORT void JNICALL Java_com_github_asteriskmods_inputtweaks_rawinput_WindowsNatives_setMouseDeltaCallback(JNIEnv *env, jclass clazz, jobject method) {
	javaClass = (*env)->NewGlobalRef(env, clazz);
	javaMouseDeltaCallback = (*env)->FromReflectedMethod(env, method);
}

JNIEXPORT jboolean JNICALL Java_com_github_asteriskmods_inputtweaks_rawinput_WindowsNatives_registerRawInputDevices(JNIEnv *env, jclass unused, jlong hWnd) {
    return InitRawInput((HWND)(INT_PTR)hWnd);
}

JNIEXPORT void JNICALL Java_com_github_asteriskmods_inputtweaks_rawinput_WindowsNatives_rawInputWindowProc(JNIEnv *env, jclass unused, jlong hWnd, jint msg, jlong wParam, jlong lParam) {
    RawInputWindowProc(env, (HWND)(INT_PTR)hWnd, msg, wParam, lParam);
}
