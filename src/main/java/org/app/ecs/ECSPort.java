package org.app.ecs;

import org.app.hexagonal.Port;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ECSPort extends Port<ECSModule, IECSProtocol> implements IECSProtocol {

    @Override
    public Entity createEntity() {
        return ((IECSProtocol)getAdapter()).createEntity();
    }

    @Override
    public void destroyEntity(Entity e) {
        ((IECSProtocol)getAdapter()).destroyEntity(e);
    }

    @Override
    public void setSignature(Entity e, Signature s) {
        ((IECSProtocol)getAdapter()).setSignature(e, s);
    }

    @Override
    public Signature getSignature(Entity e) {
        return ((IECSProtocol)getAdapter()).getSignature(e);
    }

    @Override
    public <T> void registerComponent(Class<? extends T> c) {
        ((IECSProtocol)getAdapter()).registerComponent(c);
    }

    @Override
    public <T> void registerComponent_s(Class<? extends T> c) {
        ((IECSProtocol)getAdapter()).registerComponent_s(c);
    }

    @Override
    public <T> int getComponentType(Class<? extends T> c) {
        return ((IECSProtocol)getAdapter()).getComponentType(c);
    }

    @Override
    public <T> void addComponent(Entity e, T c) {
        ((IECSProtocol)getAdapter()).addComponent(e, c);
    }

    @Override
    public <T> void removeComponent(Entity e, Class<? extends T> c) {
        ((IECSProtocol)getAdapter()).removeComponent(e, c);
    }

    @Override
    public <T> T getComponent(Class<? extends T> c, Entity e) {
        return ((IECSProtocol)getAdapter()).getComponent(c, e);
    }

    @Override
    public <T> T registerSystem(Class<? extends T> c, Object... args) {
        return ((IECSProtocol)getAdapter()).registerSystem(c, args);
    }

    @Override
    public <T> T registerSystem_s(Class<? extends T> c, Object... args) {
        return ((IECSProtocol)getAdapter()).registerSystem_s(c, args);
    }

    @Override
    public <T> void setSystemSignature(Signature s, Class<? extends T> c) {
        ((IECSProtocol)getAdapter()).setSystemSignature(s, c);
    }

    @Override
    public void entitySignatureChanged(Entity e, Signature s) {
        ((IECSProtocol)getAdapter()).entitySignatureChanged(e, s);
    }

    @Override
    public <T> void registerResourceType(Class<? extends T> c) {
        ((IECSProtocol)getAdapter()).registerResourceType(c);
    }

    @Override
    public <T> void registerResourceType_s(Class<? extends T> c) {
        ((IECSProtocol)getAdapter()).registerResourceType_s(c);
    }

    @Override
    public <T> T getResource(String key, Class<? extends T> c) {
        return ((IECSProtocol)getAdapter()).getResource(key, c);
    }

    @Override
    public <T> void setResource(String key, T value) {
        ((IECSProtocol)getAdapter()).setResource(key, value);
    }

    @Override
    public <T> void deleteResource(String key, Class<? extends T> c) {
        ((IECSProtocol)getAdapter()).deleteResource(key, c);
    }

    @Override
    public void deleteAllResources() {
        ((IECSProtocol)getAdapter()).deleteAllResources();
    }

    @Override
    public void entityDestroyed(Entity e) {
        ((IECSProtocol)getAdapter()).entityDestroyed(e);
    }

    @Override
    public PreparedStatement[] getSaveQueries(Connection conn) {
        return ((IECSProtocol)getAdapter()).getSaveQueries(conn);
    }

    @Override
    public int test() {
        return ((IECSProtocol)getAdapter()).test();
    }
}
