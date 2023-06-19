package org.app.core;

import org.app.core.components.Actor;
import org.app.core.data.Material;
import org.app.core.data.Mesh;
import org.app.core.data.Texture;
import org.app.core.data.shader.ShaderProgram;
import org.app.ecs.Entity;
import org.app.ecs.ECS;
import org.app.ecs.System;


import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL42.*;

public class RenderSystem extends System {
    private ECS ecs;
    private Entity currentEntity;
    private Entity currentCamera;

    public RenderSystem(ECS ecs) {
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
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear the framebuffer

        for (Entity entity :
                entities) {
            currentEntity = entity;
            drawElement();
        }

        glfwSwapBuffers(window); // swap the color buffers

        // Poll for all new events
        glfwPollEvents();

        currentEntity = null;
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

    public ECS getEcs() {
        return ecs;
    }

    public void setEcs(ECS ecs) {
        // TODO: When changing to a different ECS, the entity set of the RenderSystem should be cleared and repopulated with entities from the new ECS
        this.ecs = ecs;
    }
}
