package org.app.ecs;

import org.app.hexagonal.ControlledProtocol;

public interface IECSProtocol extends ControlledProtocol {
    // Entities
    Entity createEntity();
    void destroyEntity(Entity e);
    void setSignature(Entity e, Signature s);
    Signature getSignature(Entity e);

    // Components
    <T> void registerComponent(Class<? extends T> c);
    <T> void registerComponent_s(Class<? extends T> c);
    <T> int getComponentType(Class<? extends T> c);
    <T> void addComponent(Entity e, T c);
    <T> void removeComponent(Entity e, Class<? extends T> c);
    <T> T getComponent(Class<? extends T> c, Entity e);

    // Systems
    <T> T registerSystem(Class<? extends T> c, Object... args);
    <T> T registerSystem_s(Class<? extends T> c, Object... args);
    <T> void setSystemSignature(Signature s, Class<? extends T> c);
    void entitySignatureChanged(Entity e, Signature s);

    // Resources
    <T> void registerResourceType(Class<? extends T> c);
    <T> void registerResourceType_s(Class<? extends T> c);
    <T> T getResource(String key, Class<? extends T> c);
    <T> void setResource(String key, T value);
    <T> void deleteResource(String key, Class<? extends T> c);
    void deleteAllResources();

    // General
    void entityDestroyed(Entity e);
}
