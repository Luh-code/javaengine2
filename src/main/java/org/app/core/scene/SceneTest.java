package org.app.core.scene;

import glm_.vec3.Vec3;
import org.app.core.GLManager;
import org.app.core.RenderSystem;
import org.app.core.components.Camera;
import org.app.core.input.*;
import org.app.ecs.ECSPort;
import org.app.hexagonal.Adapter;
import org.app.utils.Logger;
import org.app.utils.SQLiteHelper;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;

import java.io.File;
import java.sql.Connection;

import static java.lang.Math.*;
import static java.lang.Math.toRadians;
import static org.app.utils.Logger.*;
import static org.app.utils.Logger.logInfo;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;

public class SceneTest {
    public static InputModule inputModule;

    public static void main(String[] args) {
        // Set up logger
        Logger.activateLoggingToFile("logs/", true);

        // Set up OpenGL
        logInfo("Setting up OpenGL and GLFW...");
        insetLog();

        Logger.logDebug("LWJGL Version: " + Version.getVersion());

        final long window = GLManager.init();

        // Required by LWJGL to allow for interoperation with GLFW's OpenGL context
        GL.createCapabilities();

        // Set clear color
        glClearColor(0.1f, 0.1f, 0.1f, 0.0f);

        outsetLog();
        logInfo("OpenGL and GLFW set up successfully");

        SceneManager sceneManager = new SceneManager(
                new Adapter[]{
                        new KeyboardAdapter(),
                        new MouseAdapter()
                }
        );
        sceneManager.setScene(
                new Scene(sceneManager.getInputManager().getInputModule()));
        sceneManager.init();

        // Loop
        logInfo("Setup complete, starting Program...");
        insetLog();

        RenderSystem renderSystem = sceneManager.getRenderSystem();
        InputManager inputManager = sceneManager.getInputManager();
        inputModule = inputManager.getInputModule();
        ECSPort ecs = sceneManager.getEcs();

        // Configure renderSystem
        renderSystem.activateFill();
        renderSystem.activateDepthTest();

        // Main loop
        while (!glfwWindowShouldClose(window)) {
            processInput(window, renderSystem);

            renderSystem.render(window);
        }

        Connection conn = SQLiteHelper.createInputDB(
                new File("src/main/resources/sql/input.db"));
        if (conn != null)
            inputManager.saveConfiguration(conn);

        outsetLog();
        logInfo("Program finished");

        // Cleanup
        logInfo("Cleaning up program...");
        insetLog();

        GLManager.cleanup(window);

        ecs.deleteAllResources();

        outsetLog();
        logInfo("Program cleaned up successfully");

        // Shut down logger
        Logger.deactivateLoggingToFile();
    }

    private static float lastX = 640;
    private static float lastY = 310;

    private static float yaw = 0.0f;
    private static float pitch = 0.0f;
    private static boolean firstMouse = true;

    private static boolean mouseLocked = true;

    private static void processInput(long window, RenderSystem rs) {
        float cameraSpeed = 24.0f * rs.getFrameDelta();

        inputModule.tick();

        Camera c = rs.getEcs().getComponent(Camera.class, rs.getCurrentCamera());

        // Get keyboard input
        Vec3 translation = new Vec3(0.0f, 0.0f, 0.0f);

        if (inputModule.isActionTriggered("Forwards")) {
            translation = translation.plus(c.getFront().times(cameraSpeed));
        }
        if (inputModule.isActionTriggered("Backwards")) {
            translation = translation.minus(c.getFront().times(cameraSpeed));
        }
        if (inputModule.isActionTriggered("Left")) {
            translation = translation.minus(c.getFront().cross(c.getUpDirection()).normalize().times(cameraSpeed));
        }
        if (inputModule.isActionTriggered("Right")) {
            translation = translation.plus(c.getFront().cross(c.getUpDirection()).normalize().times(cameraSpeed));
        }

        if ( inputModule.isActionJustTriggered("ToggleMouseLock") ) {
            mouseLocked = !mouseLocked;
        }

        // Get Mouse Input
        if ( mouseLocked ) glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        else glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        float xpos = inputModule.getAnalogActionValue("MouseXAxis");
        float ypos = inputModule.getAnalogActionValue("MouseYAxis");

        if (firstMouse) {
            lastX = xpos;
            lastY = ypos;
            firstMouse = false;
        }

        float xoffset = xpos - lastX;
        float yoffset = lastY - ypos;
        lastX = xpos;
        lastY = ypos;

        float sensitivity = 0.2f;
        xoffset *= sensitivity;
        yoffset *= sensitivity;
        yaw += xoffset;
        pitch += yoffset;

        if (pitch > 89.0f)
            pitch = 89.0f;
        if (pitch < -89.0f)
            pitch = -89.0f;

        // Assign computed values
        Vec3 direction = new Vec3();
        direction.setX((float) cos(toRadians(yaw)) * (float) cos(toRadians(pitch)));
        direction.setY((float) sin(toRadians(pitch)));
        direction.setZ((float) sin(toRadians(yaw)) * (float) cos(toRadians(pitch)));
        c.setFront(direction.normalize());

        c.setTranslation(c.getTranslation().plus(translation));
        //c.setTarget(c.getTranslation().plus(c.getFront()));

        if (inputModule.isInputTriggered("KEY_T")) {
            c.lookAt(new Vec3(0.0f, 0.0f, 0.0f));
        }

        if (inputModule.isActionTriggered("Exit")) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            glfwSetWindowShouldClose(window, true);
        }

    }
}