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

import static org.app.utils.Logger.*;

public class RenderingTest {
    public static void main(String[] args) {
        // Set up logger
        Logger.activateLoggingToFile("logs/", true);

        logInfo("Setting up scene...");

        tabDown();
        // Set up ECS
        logDebug("Setting up ECS...");
        ECS ecs = new ECS();
        // Register Types
        ecs.registerResourceType_s(Mesh.class);
        ecs.registerResourceType_s(Material.class);
        ecs.registerComponent_s(Actor.class);
        // Register Systems
        ecs.registerSystem_s(RenderSystem.class);
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

        int[] cubeIndices = {
                0, 1, 3, 3, 1, 2,
                1, 5, 2, 2, 5, 6,
                5, 4, 6, 6, 4, 7,
                4, 0, 7, 7, 0, 3,
                3, 2, 7, 7, 2, 6,
                4, 5, 0, 0, 5, 1
        };

        Mesh cubeMesh = new Mesh(cubeVertices, cubeIndices);
        ecs.setResource("cubeMesh", cubeMesh);

        Material cubeMaterial = new Material();
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
        tabUp();
        logDebug("Scene set up successfully");

        ecs.addComponent(cube, a);

        // Clean up Scene
        ecs.deleteAllResources();

        // Shut down logger
        Logger.deactivateLoggingToFile();
    }
}
