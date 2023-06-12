package org.app.core;

import glm_.vec3.Vec3;
import glm_.vec4.Vec4;
import org.app.core.components.Actor;
import org.app.core.data.Material;
import org.app.core.data.Mesh;
import org.app.core.data.Vertex;
import org.app.ecs.ECS;
import org.app.ecs.Entity;
import org.app.ecs.Signature;
import org.app.utils.Logger;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;

import static org.app.utils.Logger.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL42.*;

public class RenderingTest {
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

        logInfo("Setting up scene...");
        insetLog();

        // Set up ECS
        logDebug("Setting up ECS...");
        ECS ecs = new ECS();

        // Register Types
        ecs.registerResourceType_s(Mesh.class);
        ecs.registerResourceType_s(Material.class);
        ecs.registerComponent_s(Actor.class);

        // Register Systems
        RenderSystem renderSystem = ecs.registerSystem_s(RenderSystem.class, ecs);
        Signature renderSystemSignature = new Signature();
        renderSystemSignature.flipBit(ecs.getComponentType(Actor.class));
        ecs.setSystemSignature(renderSystemSignature, RenderSystem.class);

        // Define Resources
        logDebug("Setting up resources...");
        Vertex[] cubeVertices = {
                new Vertex(new Vec3(-.5, -.5, -.5)),
                new Vertex(new Vec3(.5, -.5, -.5)),
                new Vertex(new Vec3(.5, .5, -.5)),
                new Vertex(new Vec3(-.5, .5, -.5)),
                new Vertex(new Vec3(-.5, -.5, .5)),
                new Vertex(new Vec3(.5, -.5, .5)),
                new Vertex(new Vec3(.5, .5, .5)),
                new Vertex(new Vec3(-.5, .5, .5)),
        };

        Vertex[] quadVertices = {
                new Vertex(new Vec3(.5, .5, .0)),
                new Vertex(new Vec3(.5, -.5, .0)),
                new Vertex(new Vec3(-.5, -.5, .0)),
                new Vertex(new Vec3(-.5, .5, .0)),
        };

        int[] cubeIndices = {
                0, 1, 3, 3, 1, 2,
                1, 5, 2, 2, 5, 6,
                5, 4, 6, 6, 4, 7,
                4, 0, 7, 7, 0, 3,
                3, 2, 7, 7, 2, 6,
                4, 5, 0, 0, 5, 1
        };

        int[] quadIndices = {
                0, 1, 3,
                1, 2, 3
        };

        Mesh cubeMesh = new Mesh(cubeVertices, cubeIndices);
        cubeMesh.genBuffers();
        ecs.setResource("cubeMesh", cubeMesh);

        CharSequence vertexShaderSource = """
                #version 330 core
                layout (location = 0) in vec3 aPos;

                void main()
                {
                    gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);
                }""";
        CharSequence fragmentShaderSource = """
                #version 330 core
                out vec4 FragColor;

                void main()
                {
                    FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);
                }\s
                """;
        Material cubeMaterial = new Material(vertexShaderSource, fragmentShaderSource);
        cubeMaterial.compile();
        ecs.setResource("cubeMaterial", cubeMaterial);

        // Create entities
        logDebug("Setting up entities...");
        Entity cube = ecs.createEntity();

        Actor a = new Actor(
                new Vec3(.0, .0, .0),
                new Vec4(.0, .0, .0, .0),
                ecs.getResource("cubeMesh", Mesh.class),
                ecs.getResource("cubeMaterial", Material.class)
        );

        ecs.addComponent(cube, a);

        outsetLog();
        logInfo("Scene set up successfully");

        // Loop
        logInfo("Setup complete, starting Program...");
        insetLog();

        // Configure renderSystem
        renderSystem.activateFill();

        // Main loop
        while ( !glfwWindowShouldClose(window) ) {
            renderSystem.render(window);
        }

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
}