package org.app.core;

import glm_.mat4x4.Mat4;
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
import org.app.core.data.loading.ModelFormat;
import org.app.core.data.loading.ModelLoader;
import org.app.core.data.shader.Shader;
import org.app.core.data.shader.ShaderProgram;
import org.app.core.data.shader.ShaderType;
import org.app.core.input.*;
import org.app.ecs.*;
import org.app.utils.Logger;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;

import java.io.File;

import static java.lang.Math.*;
import static org.app.utils.Logger.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL42.*;

public class RenderingTest {
    private final static glm glmi = glm.INSTANCE;
    private static InputModule inputModule;

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

        logInfo("Setting up inputs...");
        insetLog();

        // Create Input Manager
        InputManager manager = new InputManager(2);
        manager.connectAdapter(0, new KeyboardAdapter());
        manager.connectAdapter(1, new MouseAdapter());
        manager.initialize();
        inputModule = manager.getInputModule();

        // Set up actions
        inputModule.registerAction("Exit", new Action(new int[] {
                inputModule.getInputID("KEY_ESCAPE"),
        }));
        inputModule.registerAction("Forwards", new Action(new int[] {
                inputModule.getInputID("KEY_W"),
                inputModule.getInputID("KEY_UP"),
        }));
        inputModule.registerAction("Backwards", new Action(new int[] {
                inputModule.getInputID("KEY_S"),
                inputModule.getInputID("KEY_DOWN"),
        }));
        inputModule.registerAction("Left", new Action(new int[] {
                inputModule.getInputID("KEY_A"),
                inputModule.getInputID("KEY_LEFT"),
        }));
        inputModule.registerAction("Right", new Action(new int[] {
                inputModule.getInputID("KEY_D"),
                inputModule.getInputID("KEY_RIGHT"),
        }));

        inputModule.registerAction("MouseXAxis", new Action(new int[] {
                inputModule.getInputID("MOUSE_X_AXIS"),
        }));
        inputModule.registerAction("MouseYAxis", new Action(new int[] {
                inputModule.getInputID("MOUSE_Y_AXIS"),
        }));

        outsetLog();
        logInfo("Inputs set up successfully");

        logInfo("Setting up scene...");
        insetLog();

        // Set up ECS
        logDebug("Setting up ECS...");
        insetLog();
        ECSManager ecsManager = new ECSManager();
        ecsManager.connectAdapter(new ECSAdapter());
        ECSAdapter ecs = (ECSAdapter) ecsManager.getAdapter();

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

        //Mesh cubeMesh = new Mesh(cubeVertices4, cubeIndices2);
        Mesh cubeMesh = ModelLoader.loadModel(new File("src/main/resources/models/bru.obj"), ModelFormat.OBJ);
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
                        0.1f, 10000.0f
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
                new Vec3(0.0f, 1.0f, 0.0f),
                new Vec3(0.0f, 0.0f, -1.0f)
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
            processInput(window, renderSystem);

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

    private static float lastX = 640;
    private static float lastY = 310;

    private static float yaw = 0.0f;
    private static float pitch = 0.0f;
    private static boolean firstMouse = true;

    private static void processInput(long window, RenderSystem rs) {
        float cameraSpeed = 8.0f * rs.getFrameDelta();

        inputModule.tick();

        Camera c = rs.getEcs().getComponent(Camera.class, rs.getCurrentCamera());

        // Get keyboard input
        Vec3 translation = new Vec3(0.0f, 0.0f, 0.0f);

        if ( inputModule.isActionTriggered("Forwards") ) {
            translation = translation.plus(c.getFront().times(cameraSpeed));
        }
        if ( inputModule.isActionTriggered("Backwards") ) {
            translation = translation.minus(c.getFront().times(cameraSpeed));
        }
        if ( inputModule.isActionTriggered("Left") ) {
            translation = translation.minus(c.getFront().cross(c.getUpDirection()).normalize().times(cameraSpeed));
        }
        if ( inputModule.isActionTriggered("Right") ) {
            translation = translation.plus(c.getFront().cross(c.getUpDirection()).normalize().times(cameraSpeed));
        }

        // Get Mouse Input
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
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
        direction.setX((float)cos(toRadians(yaw)) * (float)cos(toRadians(pitch)));
        direction.setY((float)sin(toRadians(pitch)));
        direction.setZ((float)sin(toRadians(yaw)) * (float)cos(toRadians(pitch)));
        c.setFront(direction.normalize());

        c.setTranslation(c.getTranslation().plus(translation));
        //c.setTarget(c.getTranslation().plus(c.getFront()));

        if ( inputModule.isInputTriggered("KEY_T") ) {
            c.lookAt(new Vec3(0.0f, 0.0f, 0.0f));
        }

        if ( inputModule.isActionTriggered("Exit") ) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            glfwSetWindowShouldClose(window, true);
        }

    }
}
