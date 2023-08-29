#include <Windows.h>
#include "com_github_asteriskmods_inputtweaks_rawinput_WindowsNatives.h"

static jclass javaClass;
static jmethodID javaMouseDeltaHandler;

#define HID_USAGE_PAGE_GENERIC 0x01

#define HID_USAGE_GENERIC_MOUSE 0x02

static BOOL InitRawInput(HWND hWnd)
{
    RAWINPUTDEVICE rid;
    rid.usUsagePage = HID_USAGE_PAGE_GENERIC;
    rid.usUsage = HID_USAGE_GENERIC_MOUSE;
    rid.dwFlags = 0;
    rid.hwndTarget = hWnd;
    return RegisterRawInputDevices(&rid, 1, sizeof(RAWINPUTDEVICE));
}

static void RawInputWindowProc(JNIEnv *env, HWND hWnd, UINT msg, WPARAM wParam, LPARAM lParam)
{
    LONG lastX, lastY;

    UINT dwSize = 0;
    GetRawInputData((HRAWINPUT) lParam, RID_INPUT, NULL, &dwSize, sizeof(RAWINPUTHEADER));
    LPBYTE buf = calloc(dwSize, 1);
    if (buf == NULL) {
        return;
    }
    GetRawInputData((HRAWINPUT) lParam, RID_INPUT, buf, &dwSize, sizeof(RAWINPUTHEADER));
    RAWINPUT *raw = (RAWINPUT*) buf;
    if (raw->header.dwType == RIM_TYPEMOUSE) {
        lastX = raw->data.mouse.lLastX;
        lastY = raw->data.mouse.lLastY;
    }
    free(buf);
    (*env)->CallStaticVoidMethod(env, javaClass, javaMouseDeltaHandler, (jint)lastX, (jint)lastY);
}

JNIEXPORT void JNICALL Java_com_github_asteriskmods_inputtweaks_rawinput_WindowsNatives_setMouseDeltaHandler(JNIEnv *env, jclass clazz, jobject method) {
	javaClass = (*env)->NewGlobalRef(env, clazz);
	javaMouseDeltaHandler = (*env)->FromReflectedMethod(env, method);
}

JNIEXPORT jboolean JNICALL Java_com_github_asteriskmods_inputtweaks_rawinput_WindowsNatives_registerRawInputDevices(JNIEnv *env, jclass unused, jlong hWnd) {
    return InitRawInput((HWND)(INT_PTR)hWnd);
}

JNIEXPORT void JNICALL Java_com_github_asteriskmods_inputtweaks_rawinput_WindowsNatives_rawInputWindowProc(JNIEnv *env, jclass unused, jlong hWnd, jint msg, jlong wParam, jlong lParam) {
    RawInputWindowProc(env, (HWND)(INT_PTR)hWnd, msg, wParam, lParam);
}
