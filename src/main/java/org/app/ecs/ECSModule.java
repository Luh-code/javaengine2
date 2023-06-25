package org.app.ecs;

public class ECSModule implements IECSProtocol {
    private ECSPort port;

    @Override
    public Entity createEntity() {
        return null;
    }

    @Override
    public void destroyEntity(Entity e) {

    }

    @Override
    public void setSignature(Entity e, Signature s) {

    }

    @Override
    public Signature getSignature(Entity e) {
        return null;
    }

    @Override
    public <T> void registerComponent(Class<? extends T> c) {

    }

    @Override
    public <T> void registerComponent_s(Class<? extends T> c) {

    }

    @Override
    public <T> int getComponentType(Class<? extends T> c) {
        return 0;
    }

    @Override
    public <T> void addComponent(Entity e, T c) {

    }

    @Override
    public <T> void removeComponent(Entity e, Class<? extends T> c) {

    }

    @Override
    public <T> T getComponent(Class<? extends T> c, Entity e) {
        return null;
    }

    @Override
    public <T> T registerSystem(Class<? extends T> c, Object... args) {
        return null;
    }

    @Override
    public <T> T registerSystem_s(Class<? extends T> c, Object... args) {
        return null;
    }

    @Override
    public <T> void setSystemSignature(Signature s, Class<? extends T> c) {

    }

    @Override
    public void entitySignatureChanged(Entity e, Signature s) {

    }

    @Override
    public <T> void registerResourceType(Class<? extends T> c) {

    }

    @Override
    public <T> void registerResourceType_s(Class<? extends T> c) {

    }

    @Override
    public <T> T getResource(String key, Class<? extends T> c) {
        return null;
    }

    @Override
    public <T> void setResource(String key, T value) {

    }

    @Override
    public <T> void deleteResource(String key, Class<? extends T> c) {

    }

    @Override
    public void deleteAllResources() {

    }

    @Override
    public void entityDestroyed(Entity e) {

    }

    @Override
    public int test() {
        return 0;
    }
}
