package org.app.window;

import org.app.utils.Logger;
import org.lwjgl.Version;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL42.*;
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
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        Logger.logDebug("Using OpenGL 4.2");

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

    public int compileShader(int shaderType, CharSequence shaderSource)
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

    public void loop()
    {
        // Required by LWJGL to allow for interoperation with GLFW's OpenGL context
        GL.createCapabilities();

        // Set clear color
        glClearColor(0.1f, 0.1f, 0.1f, 0.0f);

        // Vertex Array
        float[] vertices = {
                -0.5f, -0.5f,  0.0f,
                0.5f, -0.5f,  0.0f,
                0.0f,  0.5f,  0.0f
        };

        // Create Vertex Array Object
        int VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        // Create Vertex Buffer Object
        int VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // Create the Vertex Shader
        CharSequence vertexShaderSource = """
                #version 330 core
                layout (location = 0) in vec3 aPos;

                void main()
                {
                    gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);
                }""";
        int vertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderSource);

        // Create Fragment Shader
        CharSequence fragmentShaderSource = """
                #version 330 core
                out vec4 FragColor;

                void main()
                {
                    FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);
                }\s
                """;
        int fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource);

        // Create Shader Program
        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        // Control if program linked properly
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer success = stack.mallocInt(1);
            glGetProgramiv(shaderProgram, GL_LINK_STATUS, success);

            if ( success.get(0) == GL_FALSE )
            {
                String log = glGetProgramInfoLog(shaderProgram);
                Logger.logError("An error occurred whilst linking a shaderProgram: " + log);
            }
        }

        // Set OpenGL to use shaderProgram
        glUseProgram(shaderProgram);

        // Delete shaders (not longer required after linking)
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        // Set up Vertex Attributes
        /*
        Parameters in order:
        1. Position (layout = 0)
        2. Amt of values per vertex (vec3 = 3 values)
        3. Type of data
        4. Normalized values? (if true limits range from -1 to 1)
        5. Stride (data taken up by each vertex in bytes)
        6. Offset (becomes useful when data is added to the Vertex Attributes later)
         */
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0);
        glEnableVertexAttribArray(0); // Selects which vertex Attribute array to use

        // Main loop
        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear the framebuffer

            glUseProgram(shaderProgram);
            glBindVertexArray(VAO);
            glDrawArrays(GL_TRIANGLES, 0, 3);

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
