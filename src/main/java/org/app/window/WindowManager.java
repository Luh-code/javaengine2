package org.app.window;

import org.app.utils.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;

public class WindowManager {
    private long window;

    public void init() {
        GLFWErrorCallback.createPrint(Logger.getErrorStream()).set();
    }

    public void run() {
        Logger.logDebug("LWJGL Version: " + Version.getVersion());

        init();
    }
}
