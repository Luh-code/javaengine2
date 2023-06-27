package org.app.core.scene;

import glm_.glm;
import glm_.mat4x4.Mat4;
import glm_.vec2.Vec2i;
import glm_.vec3.Vec3;
import glm_.vec4.Vec4;
import org.app.core.GLManager;
import org.app.core.RenderSystem;
import org.app.core.components.Actor;
import org.app.core.components.Camera;
import org.app.core.data.Material;
import org.app.core.data.Mesh;
import org.app.core.data.Texture;
import org.app.core.data.loading.ModelFormat;
import org.app.core.data.loading.ModelLoader;
import org.app.core.data.shader.Shader;
import org.app.core.data.shader.ShaderProgram;
import org.app.core.data.shader.ShaderType;
import org.app.core.input.InputModule;
import org.app.ecs.*;
import org.app.hexagonal.Adapter;
import org.app.utils.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.lang.Math.toRadians;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;

public class Scene {
    private glm glmi = glm.INSTANCE;
    private InputModule inputModule;
    private Adapter<IECSProtocol> ecsAdapter;
    private ECSPort ecs;
    private RenderSystem renderSystem;

    public Scene(InputModule inputModule) {
        this.inputModule = inputModule;

        this.ecsAdapter = new ECSAdapter();
    }

    public void init() {
        this.ecs = (ECSPort) this.ecsAdapter.getPort();

        Logger.logInfo("Initializing scene...");
        Logger.insetLog();

        // TEMPORARY
        Logger.logInfo("Initializing the ECS...");
        Logger.insetLog();

        this.renderSystem = ecs.registerSystem_s(RenderSystem.class, ecs);

        ecs.registerResourceType_s(Mesh.class);
        ecs.registerResourceType_s(Material.class);
        ecs.registerResourceType_s(Texture.class);
        ecs.registerComponent_s(Actor.class);
        ecs.registerComponent_s(Camera.class);

        Signature renderSystemSignature = new Signature();
        renderSystemSignature.flipBit(ecs.getComponentType(Actor.class));
        ecs.setSystemSignature(renderSystemSignature, RenderSystem.class);

        Logger.outsetLog();
        Logger.logInfo("ECS initialized");

        // STILL TEMPORARY
        Logger.logInfo("Registering resources...");
        Logger.insetLog();

        Mesh gun = ModelLoader.loadModel(new File("src/main/resources/models/bru.obj"), ModelFormat.OBJ);
        gun.genBuffers();
        ecs.setResource("gunMesh", gun);

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
                        (float) GLManager.getScreenSize().getX()/(float)GLManager.getScreenSize().getY(),
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

        Material basicMaterial = new Material(shaderProgram, new Texture[] { wallTexture, wallTexture2 });
        basicMaterial.compile();
        ecs.setResource("basicMaterial", basicMaterial);

        Logger.outsetLog();
        Logger.logInfo("Resources registered");

        Logger.logInfo("Setting up entities...");
        Logger.insetLog();

        Entity e1 = ecs.createEntity();

        Actor a1 = new Actor(
                new Vec3(.0, .0, 0.0),
                new Vec4(.0, .0, .0, .0),
                ecs.getResource("gunMesh", Mesh.class),
                ecs.getResource("basicMaterial", Material.class)
        );

        ecs.addComponent(e1, a1);

        Entity e2 = ecs.createEntity();

        Actor a2 = new Actor(
                new Vec3(.0, .0, -2.0),
                new Vec4(.0, .0, .0, .0),
                ecs.getResource("gunMesh", Mesh.class),
                ecs.getResource("basicMaterial", Material.class)
        );

        Entity camera = ecs.createEntity();

        Camera c = new Camera(
                new Vec3(0.0f, 0.0f, 3.0f),
                new Vec4(.0f, .0f, .0f, .0f),
                a1.getTranslation(),
                (float)toRadians(90.0f),
                new Vec3(0.0f, 1.0f, 0.0f),
                new Vec3(0.0f, 0.0f, -1.0f)
        );

        ecs.addComponent(camera, c);
        renderSystem.setCurrentCamera(camera);

        ecs.addComponent(e2, a2);

        Logger.outsetLog();
        Logger.logInfo("Entities set up");

        Logger.outsetLog();
        Logger.logInfo("Scene set up");
    }

    public void saveScene(Connection conn) {
        for (PreparedStatement stmt :
                ecsAdapter.getPort().getSaveQueries(conn)) {
            try {
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public InputModule getInputModule() {
        return inputModule;
    }

    public Adapter<IECSProtocol> getEcsAdapter() {
        return ecsAdapter;
    }

    public RenderSystem getRenderSystem() {
        return renderSystem;
    }
}
