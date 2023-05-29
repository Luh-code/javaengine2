package org.app.window;

import org.app.utils.Logger;
import org.lwjgl.Version;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.IntBuffer;
import java.util.Objects;

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
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        Logger.logDebug("Using OpenGL 4.1");

        // Create window
        window = glfwCreateWindow(1280, 720, "Java Engine", NULL, NULL);
        if ( window == NULL )
            Logger.logAndThrow("Failed to create GLFW window", RuntimeException.class);

        // Set up a key callback to exit when key is pressed
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
           if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
               glfwSetWindowShouldClose(window, true);
        });

        // Get thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size from the GLFW window
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (Objects.requireNonNull(vidmode).width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // The frame is automatically getting popped from the stack

        // Make the OPENGL context current
        glfwMakeContextCurrent(window);
        // Enable V-Sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        Logger.logInfo("Window initialized");
    }

    public void loop()
    {
        // Required by LWJGL to allow for interoperation with GLFW's OpenGL context
        GL.createCapabilities();

        // Set clear color
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        // Main loop
        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear the framebuffer

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for all new events
            glfwPollEvents();
        }
    }

    public void run() {
        Logger.logDebug("LWJGL Version: " + Version.getVersion());

        init();
        loop();

        // Free window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and the error Callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();

        Logger.logInfo("Window destroyed");
    }

    public static void main(String[] args) {
        new WindowManager().run();
    }
}
