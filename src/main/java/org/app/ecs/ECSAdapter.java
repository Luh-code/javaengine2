package org.app.ecs;

import org.app.hexagonal.Adapter;

public class ECSAdapter extends Adapter<IECSProtocol> implements IECSProtocol {
    private ECS ecs;

    @Override
    public Entity createEntity() {
        return ecs.createEntity();
    }

    @Override
    public void destroyEntity(Entity e) {
        ecs.destroyEntity(e);
    }

    @Override
    public void setSignature(Entity e, Signature s) {
        ecs.setSignature(e, s);
    }

    @Override
    public Signature getSignature(Entity e) {
        return ecs.getSignature(e);
    }

    @Override
    public <T> void registerComponent(Class<? extends T> c) {
        ecs.registerComponent(c);
    }

    @Override
    public <T> void registerComponent_s(Class<? extends T> c) {
        ecs.registerComponent_s(c);
    }

    @Override
    public <T> int getComponentType(Class<? extends T> c) {
        return ecs.getComponentType(c);
    }

    @Override
    public <T> void addComponent(Entity e, T c) {
        ecs.addComponent(e, c);
    }

    @Override
    public <T> void removeComponent(Entity e, Class<? extends T> c) {
        ecs.removeComponent(e, c);
    }

    @Override
    public <T> T getComponent(Class<? extends T> c, Entity e) {
        return ecs.getComponent(c, e);
    }

    @Override
    public <T> T registerSystem(Class<? extends T> c, Object... args) {
        return ecs.registerSystem(c, args);
    }

    @Override
    public <T> T registerSystem_s(Class<? extends T> c, Object... args) {
        return ecs.registerSystem_s(c, args);
    }

    @Override
    public <T> void setSystemSignature(Signature s, Class<? extends T> c) {
        ecs.setSystemSignature(s, c);
    }

    @Override
    public void entitySignatureChanged(Entity e, Signature s) {
        ecs.entitySignatureChanged(e, s);
    }

    @Override
    public <T> void registerResourceType(Class<? extends T> c) {
        ecs.registerResourceType(c);
    }

    @Override
    public <T> void registerResourceType_s(Class<? extends T> c) {
        ecs.registerResourceType_s(c);
    }

    @Override
    public <T> T getResource(String key, Class<? extends T> c) {
        return ecs.getResource(key, c);
    }

    @Override
    public <T> void setResource(String key, T value) {
        ecs.setResource(key, value);
    }

    @Override
    public <T> void deleteResource(String key, Class<? extends T> c) {
        ecs.deleteResource(key, c);
    }

    @Override
    public void deleteAllResources() {
        ecs.deleteAllResources();
    }

    @Override
    public void entityDestroyed(Entity e) {
        ecs.entityDestroyed(e);
    }

    @Override
    public int test() {
        return 0;
    }
}
