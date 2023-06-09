package org.app.core;

import glm_.vec2.Vec2i;
import org.app.utils.Logger;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLManager {
    private static Vec2i screenSize = new Vec2i(1280, 720);
    private static long window;

    public static long init() {
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
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        Logger.logDebug("Using OpenGL 4.2");

        // Create window
        window = glfwCreateWindow(screenSize.getX(), screenSize.getY(), "Java Engine", NULL, NULL);
        if ( window == NULL )
            Logger.logAndThrow("Failed to create GLFW window", RuntimeException.class);

        // Set up a key callback to exit when key is pressed
//        glfwSetKeyCallback(window, (wnd, key, scancode, action, mods) -> {
//            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
//                glfwSetWindowShouldClose(wnd, true);
//                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
//            }
//        });

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
        glfwSetFramebufferSizeCallback(window, GLManager::framebuffer_size_callback);
        // Enable V-Sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        Logger.logDebug("Window initialized");
        return window;
    }

    public static int compileShader(int shaderType, CharSequence shaderSource)
    {
        // Check if shaderType is valid
        switch ( shaderType ) {
            case GL_VERTEX_SHADER:
            case GL_FRAGMENT_SHADER:
                break;
            default:
                Logger.logError("'" + shaderType + "' not recognized as valid shader Type");
                return -1;
        }

        // Create Shader
        int shader = glCreateShader(shaderType);

        // Compile shader
        glShaderSource(shader, shaderSource);
        glCompileShader(shader);

        // Control if shader compiled properly
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer success = stack.mallocInt(1);
            glGetShaderiv(shader, GL_COMPILE_STATUS, success);

            if ( success.get(0) == GL_FALSE )
            {
                String log = glGetShaderInfoLog(shader);
                Logger.logError("An error occurred whilst compiling a shader: " + log);
            }
        }

        return shader;
    }

    public static void cleanup(long window)
    {
        // Free window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and the error Callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();

        Logger.logDebug("Window destroyed");
    }

    public static void framebuffer_size_callback(long window, int w, int h) {
        glViewport(0, 0, w, h);
        screenSize = new Vec2i(w, h);
    }

    public static Vec2i getScreenSize() {
        return screenSize;
    }

    public static long getWindow() {
        return window;
    }
}
