package org.app.core;

import glm_.mat4x4.Mat4;
import glm_.mat4x4.Mat4d;
import glm_.vec2.Vec2;
import glm_.vec2.Vec2i;
import glm_.vec3.Vec3;
import glm_.vec4.Vec4;
import glm_.*;
import org.app.core.components.Actor;
import org.app.core.components.Camera;
import org.app.core.data.Material;
import org.app.core.data.Mesh;
import org.app.core.data.Texture;
import org.app.core.data.Vertex;
import org.app.core.data.shader.Shader;
import org.app.core.data.shader.ShaderProgram;
import org.app.core.data.shader.ShaderType;
import org.app.ecs.ECS;
import org.app.ecs.Entity;
import org.app.ecs.Signature;
import org.app.utils.Logger;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;

import java.io.File;

import static java.lang.Math.*;
import static org.app.utils.Logger.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL42.*;

public class RenderingTest {
    final static glm glmi = glm.INSTANCE;

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
        insetLog();
        ECS ecs = new ECS();

        // Register Types
        ecs.registerResourceType_s(Mesh.class);
        ecs.registerResourceType_s(Material.class);
        ecs.registerResourceType_s(Texture.class);
        ecs.registerComponent_s(Actor.class);
        ecs.registerComponent_s(Camera.class);

        // Register Systems
        RenderSystem renderSystem = ecs.registerSystem_s(RenderSystem.class, ecs);
        Signature renderSystemSignature = new Signature();
        renderSystemSignature.flipBit(ecs.getComponentType(Actor.class));
        ecs.setSystemSignature(renderSystemSignature, RenderSystem.class);
        outsetLog();

        // Define Resources
        logDebug("Setting up resources...");
        insetLog();
        Vertex[] cubeVertices = {
                new Vertex(new Vec3(-.5, -.5, -.5), new Vec3(1.f, .0f, .0f), new Vec2(.0f, .0f)),
                new Vertex(new Vec3(.5, -.5, -.5), new Vec3(.5f, .5f, .0f), new Vec2(1.f, .0f)),
                new Vertex(new Vec3(.5, .5, -.5), new Vec3(.0f, .1f, .0f), new Vec2(1.f, 1.f)),
                new Vertex(new Vec3(-.5, .5, -.5), new Vec3(0.f, .5f, .5f), new Vec2(.0f, 1.f)),
                new Vertex(new Vec3(-.5, -.5, .5), new Vec3(0.f, .0f, 1.f), new Vec2(.0f, 1.f)),
                new Vertex(new Vec3(.5, -.5, .5), new Vec3(.5f, .0f, .5f), new Vec2(1.f, 1.f)),
                new Vertex(new Vec3(.5, .5, .5), new Vec3(1.f, .0f, .0f), new Vec2(1.f, .0f)),
                new Vertex(new Vec3(-.5, .5, .5), new Vec3(.5f, .5f, .0f), new Vec2(.0f, .0f)),
        };

        Vertex[] cubeVertices2 = {
                //                       Translation            Color               UV
                new Vertex( new float[] { -.5f, -.5f, -.5f,     1.f, .0f, .0f,      .0f, .0f} ),
                new Vertex( new float[] {  .5f, -.5f, -.5f,     .5f, .5f, .0f,      1.f, .0f } ),
                new Vertex( new float[] {  .5f,  .5f, -.5f,     .0f, .1f, .0f,      1.f, 1.f } ),
                new Vertex( new float[] { -.5f,  .5f, -.5f,     0.f, .5f, .5f,      .0f, 1.f } ),
                new Vertex( new float[] { -.5f, -.5f,  .5f,     0.f, .0f, 1.f,      .0f, 1.f }),
                new Vertex( new float[] {  .5f, -.5f,  .5f,     .5f, .0f, .5f,      1.f, 1.f } ),
                new Vertex( new float[] {  .5f,  .5f,  .5f,     1.f, .0f, .0f,      1.f, .0f } ),
                new Vertex( new float[] { -.5f,  .5f,  .5f,     .5f, .5f, .0f,      .0f, .0f } ),
        };

        float[] cubeVertices3 = {
            //  Translation             Color               UV
                -.5f, -.5f, -.5f,       1.f, .0f, .0f,      .0f, .0f,
                 .5f, -.5f, -.5f,       .5f, .5f, .0f,      1.f, .0f,
                 .5f,  .5f, -.5f,       .0f, .1f, .0f,      1.f, 1.f,
                -.5f,  .5f, -.5f,       0.f, .5f, .5f,      .0f, 1.f,
                -.5f, -.5f,  .5f,       0.f, .0f, 1.f,      .0f, 1.f,
                 .5f, -.5f,  .5f,       .5f, .0f, .5f,      1.f, 1.f,
                 .5f,  .5f,  .5f,       1.f, .0f, .0f,      1.f, .0f,
                -.5f,  .5f,  .5f,       .5f, .5f, .0f,      .0f, .0f,
        };

        int[] cubeIndices = {
                0, 1, 3, 3, 1, 2,
                1, 5, 2, 2, 5, 6,
                5, 4, 6, 6, 4, 7,
                4, 0, 7, 7, 0, 3,
                3, 2, 7, 7, 2, 6,
                4, 5, 0, 0, 5, 1
        };

        float[] cubeVertices4 = {
            //  Translation             Color               UV
                -0.5f, -0.5f, -0.5f,    1.f, .0f, .0f,      0.0f, 0.0f,
                 0.5f, -0.5f, -0.5f,    0.f, .5f, .5f,      1.0f, 0.0f,
                 0.5f,  0.5f, -0.5f,    .0f, .1f, .0f,      1.0f, 1.0f,
                 0.5f,  0.5f, -0.5f,    .5f, .5f, .0f,      1.0f, 1.0f,
                -0.5f,  0.5f, -0.5f,    0.f, .0f, 1.f,      0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,    .5f, .5f, .0f,      0.0f, 0.0f,

                -0.5f, -0.5f,  0.5f,    1.f, .0f, .0f,      0.0f, 0.0f,
                 0.5f, -0.5f,  0.5f,    0.f, .5f, .5f,      1.0f, 0.0f,
                 0.5f,  0.5f,  0.5f,    .0f, .1f, .0f,      1.0f, 1.0f,
                 0.5f,  0.5f,  0.5f,    .5f, .5f, .0f,      1.0f, 1.0f,
                -0.5f,  0.5f,  0.5f,    0.f, .0f, 1.f,      0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f,    .5f, .5f, .0f,      0.0f, 0.0f,

                -0.5f,  0.5f,  0.5f,    1.f, .0f, .0f,      1.0f, 0.0f,
                -0.5f,  0.5f, -0.5f,    0.f, .5f, .5f,      1.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,    .0f, .1f, .0f,      0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,    .5f, .5f, .0f,      0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f,    0.f, .0f, 1.f,      0.0f, 0.0f,
                -0.5f,  0.5f,  0.5f,    .5f, .5f, .0f,      1.0f, 0.0f,

                 0.5f,  0.5f,  0.5f,    1.f, .0f, .0f,      1.0f, 0.0f,
                 0.5f,  0.5f, -0.5f,    0.f, .5f, .5f,      1.0f, 1.0f,
                 0.5f, -0.5f, -0.5f,    .0f, .1f, .0f,      0.0f, 1.0f,
                 0.5f, -0.5f, -0.5f,    .5f, .5f, .0f,      0.0f, 1.0f,
                 0.5f, -0.5f,  0.5f,    0.f, .0f, 1.f,      0.0f, 0.0f,
                 0.5f,  0.5f,  0.5f,    .5f, .5f, .0f,      1.0f, 0.0f,

                -0.5f, -0.5f, -0.5f,    1.f, .0f, .0f,      0.0f, 1.0f,
                 0.5f, -0.5f, -0.5f,    0.f, .5f, .5f,      1.0f, 1.0f,
                 0.5f, -0.5f,  0.5f,    .0f, .1f, .0f,      1.0f, 0.0f,
                 0.5f, -0.5f,  0.5f,    .5f, .5f, .0f,      1.0f, 0.0f,
                -0.5f, -0.5f,  0.5f,    0.f, .0f, 1.f,      0.0f, 0.0f,
                -0.5f, -0.5f, -0.5f,    .5f, .5f, .0f,      0.0f, 1.0f,

                -0.5f,  0.5f, -0.5f,    1.f, .0f, .0f,      0.0f, 1.0f,
                 0.5f,  0.5f, -0.5f,    0.f, .5f, .5f,      1.0f, 1.0f,
                 0.5f,  0.5f,  0.5f,    .0f, .1f, .0f,      1.0f, 0.0f,
                 0.5f,  0.5f,  0.5f,    .5f, .5f, .0f,      1.0f, 0.0f,
                -0.5f,  0.5f,  0.5f,    0.f, .0f, 1.f,      0.0f, 0.0f,
                -0.5f,  0.5f, -0.5f,    .5f, .5f, .0f,      0.0f, 1.0f
        };

        int[] cubeIndices2 = new int[cubeVertices4.length/Vertex.getDataSize()];
        for (int i = 0; i < cubeIndices2.length; i++) {
            cubeIndices2[i] = i;
        }

        Vertex[] quadVertices = {
                new Vertex(new Vec3(.5, .5, .0), new Vec3(1.f, .0f, .0f), new Vec2(1.f, 1.f)),
                new Vertex(new Vec3(.5, -.5, .0), new Vec3(.0f, 1.f, .0f), new Vec2(1.f, .0f)),
                new Vertex(new Vec3(-.5, -.5, .0), new Vec3(.0f, .0f, .1f), new Vec2(.0f, .0f)),
                new Vertex(new Vec3(-.5, .5, .0), new Vec3(1.f, 1.f, .0f), new Vec2(.0f, 1.f)),
        };

        int[] quadIndices = {
                0, 1, 3,
                1, 2, 3
        };

        float[] triangleVertices = {
            //  Translation             Color               UV
                 .0f,  .5f,  .0f,       1.f, .0f, .0f,      .5f, 1.f,
                -.5f, -.5f,  .0f,       .0f, 1.f, .0f,      .0f, .0f,
                 .5f, -.5f,  .0f,       .0f, .0f, 1.f,      1.f, .0f,
        };

        int[] triangleIndices = {
                0, 1, 2,
        };

        Mesh cubeMesh = new Mesh(cubeVertices4, cubeIndices2);
        cubeMesh.genBuffers();
        ecs.setResource("cubeMesh", cubeMesh);
        Mesh quadMesh = new Mesh(quadVertices, quadIndices);
        quadMesh.genBuffers();
        ecs.setResource("quadMesh", quadMesh);
        Mesh triangleMesh = new Mesh(triangleVertices, triangleIndices);
        triangleMesh.genBuffers();
        ecs.setResource("triangleMesh", triangleMesh);

        // Create the shader program
        ShaderProgram shaderProgram;
        {
            File vertexShaderFile = new File("src/main/resources/shader/testShader.vert");
            Shader vertexShader = new Shader(ShaderType.SHADER_TYPE_VERTEX, vertexShaderFile);
            File fragmentShaderFile = new File("src/main/resources/shader/testShader.frag");
            Shader fragmentShader = new Shader(ShaderType.SHADER_TYPE_FRAGMENT, fragmentShaderFile);
            shaderProgram = new ShaderProgram(vertexShader, fragmentShader, (rs, sp) -> {
                // Get entity and variable for easier access
                Entity entity = rs.getCurrentEntity();
                Actor actor = ecs.getComponent(Actor.class, entity);
                Camera camera = ecs.getComponent(Camera.class, rs.getCurrentCamera());

                // Get time for animations
                float timeValue = (float) glfwGetTime();

                // Set texture uniforms
                sp.setInt("texture1", 0);
                sp.setInt("texture2", 1);

                // Model-View-Projection
                Mat4 model = new Mat4(1.0f);
//                model = glmi.rotate(model,
//                        timeValue * (float)toRadians(50.0f), new Vec3(0.5f, 1.0f, 0.0f));
                model = glmi.rotate(model, actor.getRotation().getX(), new Vec3(1.0f, 0.0f, 0.0f));
                model = glmi.rotate(model, actor.getRotation().getY(), new Vec3(0.0f, 1.0f, 0.0f));
                model = glmi.rotate(model, actor.getRotation().getZ(), new Vec3(0.0f, 0.0f, 1.0f));
                model = glmi.translate(model, actor.getTranslation());

                Mat4 view = new Mat4(1.0f);
                //view = glmi.translate(view, actor.getTranslation());
                view = camera.getLookAt();

                Mat4 projection = glmi.perspective((float) toRadians(45.0f),
                        (float)GLManager.getScreenSize().getX()/(float)GLManager.getScreenSize().getY(),
                        0.1f, 100.0f
                );

                // Set Model-View-Projection uniforms
                sp.setMat4("model", model);
                sp.setMat4("view", view);
                sp.setMat4("projection", projection);
            });
        }

        Texture wallTexture = new Texture(
                "src/main/resources/textures/wall.jpg",
                new Vec2i(GL_REPEAT, GL_REPEAT),
                new Vec2i(GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR),
                GL_TEXTURE0,
                false
        );
        ecs.setResource("wallTexture", wallTexture);
        Texture wallTexture2 = new Texture(
                "src/main/resources/textures/wall2.jpg",
                new Vec2i(GL_REPEAT, GL_REPEAT),
                new Vec2i(GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR),
                GL_TEXTURE1,
                false
        );
        ecs.setResource("wallTexture2", wallTexture2);

        Material cubeMaterial = new Material(shaderProgram, new Texture[] { wallTexture, wallTexture2 });
        cubeMaterial.compile();
        ecs.setResource("cubeMaterial", cubeMaterial);
        outsetLog();

        // Create entities
        logDebug("Setting up entities...");
        insetLog();
        Entity cube = ecs.createEntity();

        Actor a = new Actor(
                new Vec3(.0, .0, 0.0),
                new Vec4(.0, .0, .0, .0),
                ecs.getResource("cubeMesh", Mesh.class),
                ecs.getResource("cubeMaterial", Material.class)
        );

        ecs.addComponent(cube, a);

        Entity cube2 = ecs.createEntity();

        Actor a2 = new Actor(
                new Vec3(.0, .0, -2.0),
                new Vec4(.0, .0, .0, .0),
                ecs.getResource("cubeMesh", Mesh.class),
                ecs.getResource("cubeMaterial", Material.class)
        );

        ecs.addComponent(cube2, a2);

        Entity camera = ecs.createEntity();

        Camera c = new Camera(
                new Vec3(0.0f, 0.0f, 3.0f),
                new Vec4(.0f, .0f, .0f, .0f),
                a.getTranslation(),
                (float)toRadians(90.0f),
                new Vec3(0.0f, 1.0f, 0.0f)
        );

        ecs.addComponent(camera, c);
        renderSystem.setCurrentCamera(camera);
        outsetLog();

        outsetLog();
        logInfo("Scene set up successfully");

        // Loop
        logInfo("Setup complete, starting Program...");
        insetLog();

        // Configure renderSystem
        renderSystem.activateFill();
        renderSystem.activateDepthTest();

        // Main loop
        while ( !glfwWindowShouldClose(window) ) {
            float timeValue = (float) glfwGetTime();
            float radius = 10.0f;
            float camX = (float) sin(timeValue) * radius;
            float camZ = (float) cos(timeValue) * radius;
            c.setTranslation(new Vec3(camX, 0.0f, camZ));

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
