package org.app.window;

import org.app.utils.Logger;
import org.lwjgl.Version;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class WindowManager {
    private long window;

    public void init() {
        // Set error callback to Logger::logError
        GLFWErrorCallback.createPrint(Logger.getErrorStream()).set();

        // Initialize GLFW
        if ( !glfwInit() )
            Logger.logAndThrow("Unable to initialize GLFW", IllegalAccessException.class);

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create window
        window = glfwCreateWindow(1280, 720, "Java Engine", NULL, NULL);
        if ( window == NULL ){
            Logger.logCrit("Failed to create GLFW window");
            throw new RuntimeException("Failed to create GLFW window");
        }
    }

    public void run() {
        Logger.logDebug("LWJGL Version: " + Version.getVersion());

        init();
    }
}
