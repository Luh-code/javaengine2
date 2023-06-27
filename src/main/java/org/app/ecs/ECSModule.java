package org.app.ecs;

import org.app.hexagonal.AdapterStatus;
import org.app.hexagonal.LinkedModule;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ECSModule extends LinkedModule<ECSPort> implements IECSProtocol {
    @Override
    public Entity createEntity() {
        return getPort().createEntity();
    }

    @Override
    public void destroyEntity(Entity e) {
        getPort().destroyEntity(e);
    }

    @Override
    public void setSignature(Entity e, Signature s) {
        getPort().setSignature(e, s);
    }

    @Override
    public Signature getSignature(Entity e) {
        return getPort().getSignature(e);
    }

    @Override
    public <T> void registerComponent(Class<? extends T> c) {
        getPort().registerComponent(c);
    }

    @Override
    public <T> void registerComponent_s(Class<? extends T> c) {
        getPort().registerComponent_s(c);
    }

    @Override
    public <T> int getComponentType(Class<? extends T> c) {
        return getPort().getComponentType(c);
    }

    @Override
    public <T> void addComponent(Entity e, T c) {
        getPort().addComponent(e, c);
    }

    @Override
    public <T> void removeComponent(Entity e, Class<? extends T> c) {
        getPort().removeComponent(e, c);
    }

    @Override
    public <T> T getComponent(Class<? extends T> c, Entity e) {
        return getPort().getComponent(c, e);
    }

    @Override
    public <T> T registerSystem(Class<? extends T> c, Object... args) {
        return getPort().registerSystem(c, args);
    }

    @Override
    public <T> T registerSystem_s(Class<? extends T> c, Object... args) {
        return getPort().registerSystem_s(c, args);
    }

    @Override
    public <T> void setSystemSignature(Signature s, Class<? extends T> c) {
        getPort().setSystemSignature(s, c);
    }

    @Override
    public void entitySignatureChanged(Entity e, Signature s) {
        getPort().entitySignatureChanged(e, s);
    }

    @Override
    public <T> void registerResourceType(Class<? extends T> c) {
        getPort().registerResourceType(c);
    }

    @Override
    public <T> void registerResourceType_s(Class<? extends T> c) {
        getPort().registerResourceType_s(c);
    }

    @Override
    public <T> T getResource(String key, Class<? extends T> c) {
        return getPort().getResource(key, c);
    }

    @Override
    public <T> void setResource(String key, T value) {
        getPort().setResource(key, value);
    }

    @Override
    public <T> void deleteResource(String key, Class<? extends T> c) {
        getPort().deleteResource(key, c);
    }

    @Override
    public void deleteAllResources() {
        getPort().deleteAllResources();
    }

    @Override
    public void entityDestroyed(Entity e) {
        getPort().entityDestroyed(e);
    }

    @Override
    public PreparedStatement[] getSaveQueries(Connection conn) {
        return getPort().getSaveQueries(conn);
    }

    @Override
    public int test() {
        return ( getStatus() != AdapterStatus.IS_CONNECTED ? -1 : 0 );
    }
}
