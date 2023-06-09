package org.app.core;

import org.app.core.components.Actor;
import org.app.core.data.Material;
import org.app.core.data.Mesh;
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

    private void drawElement(Entity e)
    {
        Actor actor = ecs.getComponent(Actor.class, e);
        Mesh mesh = actor.getMesh();
        Material material = actor.getMaterial();

        glUseProgram(material.getShaderProgram());
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
}
