package org.app.core;

import org.app.core.components.Actor;
import org.app.core.components.Camera;
import org.app.core.data.Material;
import org.app.core.data.Mesh;
import org.app.core.data.Texture;
import org.app.core.data.shader.ShaderProgram;
import org.app.ecs.ECSAdapter;
import org.app.ecs.Entity;
import org.app.ecs.ECS;
import org.app.ecs.System;


import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL42.*;

public class RenderSystem extends System {
    private ECSAdapter ecs;
    private Entity currentEntity;
    private Entity currentCamera;
    private float frameDelta = 0.0f;
    private float lastFrame = 0.0f;

    public RenderSystem(ECSAdapter ecs) {
        this.ecs = ecs;
    }

    /**
     * Rendering function for forward rendering based on depth buffer information
     */
    private void drawElement()
    {
        // Set up references for eoa
        Actor actor = ecs.getComponent(Actor.class, currentEntity);
        Mesh mesh = actor.getMesh();
        Material material = actor.getMaterial();
        ShaderProgram shaderProgram = material.getShaderProgram();

        // Bind texture if used
        material.apply();

        shaderProgram.use();
        // Update shader uniforms if needed
        shaderProgram.updateUniforms(this);

        // Bind the rest and render
        glBindVertexArray(mesh.getVAO());
        glDrawElements(GL_TRIANGLES, mesh.getIndices().length, GL_UNSIGNED_INT, 0);
        //glBindVertexArray(0);
    }

    /**
     * Changes the Drawing Mode of OpenGL to Wireframe
     */
    public void activateWireframe()
    {
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }

    /**
     * Changes the Drawing Mode of OpenGL to Fill
     */
    public void activateFill()
    {
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    /**
     * Actives depth test
     */
    public void activateDepthTest() {
        glEnable(GL_DEPTH_TEST);
    }

    /**
     * deactives depth test
     */
    public void deactivateDepthTest() {
        glDisable(GL_DEPTH_TEST);
    }

    /**
     * Renders all Entities associated with this RenderSystem in a forward rendering process
     * Also renders the depth-buffer
     * @param window The window Handle of the current glfw window
     */
    public void render(long window)
    {
        //float frameBegin = (float)glfwGetTime();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear the framebuffer

        Camera c = ecs.getComponent(Camera.class, getCurrentCamera());
        c.updateCamera();

        for (Entity entity :
                entities) {
            currentEntity = entity;
            drawElement();
        }

        glfwSwapBuffers(window); // swap the color buffers

        // Poll for all new events
        glfwPollEvents();

        currentEntity = null;
        float currentFrame = (float)glfwGetTime();
        frameDelta = currentFrame - lastFrame;
        lastFrame = currentFrame;
    }

    public Entity getCurrentEntity() {
        return currentEntity;
    }

    public Entity getCurrentCamera() {
        return currentCamera;
    }

    public void setCurrentCamera(Entity currentCamera) {
        this.currentCamera = currentCamera;
    }

    public ECSAdapter getEcs() {
        return ecs;
    }

    public void setEcs(ECSAdapter ecs) {
        // TODO: When changing to a different ECSAdapter, the entity set of the RenderSystem should be cleared and repopulated with entities from the new ECS
        this.ecs = ecs;
    }

    public float getFrameDelta() {
        return frameDelta;
    }
}
