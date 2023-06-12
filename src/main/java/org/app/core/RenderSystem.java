package org.app.core;

import org.app.core.components.Actor;
import org.app.core.data.Material;
import org.app.core.data.Mesh;
import org.app.core.data.shader.ShaderProgram;
import org.app.ecs.Entity;
import org.app.ecs.ECS;
import org.app.ecs.System;


import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL42.*;

public class RenderSystem extends System {
    private ECS ecs;

    public RenderSystem(ECS ecs) {
        this.ecs = ecs;
    }

    /**
     * Rendering function for forward rendering based on depth buffer information
     * @param e The entity to be drawn. Any entity that is passed in here is assumed to have an Actor component
     */
    private void drawElement(Entity e)
    {
        // Set up references for eoa
        Actor actor = ecs.getComponent(Actor.class, e);
        Mesh mesh = actor.getMesh();
        Material material = actor.getMaterial();
        ShaderProgram shaderProgram = material.getShaderProgram();

        glUseProgram(shaderProgram.getShaderProgram());
        // Update shader uniforms if needed
        shaderProgram.updateUniforms(this);
        // Bind the rest and render
        glBindVertexArray(mesh.getVAO());
        glDrawElements(GL_TRIANGLES, mesh.getIndices().length, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
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
     * Renders all Entities associated with this RenderSystem in a forward rendering process
     * Also renders the depth-buffer
     * @param window The window Handle of the current glfw window
     */
    public void render(long window)
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear the framebuffer

        for (Entity entity :
                entities) {
            drawElement(entity);
        }

        glfwSwapBuffers(window); // swap the color buffers

        // Poll for all new events
        glfwPollEvents();
    }

    public ECS getEcs() {
        return ecs;
    }

    public void setEcs(ECS ecs) {
        // TODO: When changing to a different ECS, the entity set of the RenderSystem should be cleared and repopulated with entities from the new ECS
        this.ecs = ecs;
    }
}
