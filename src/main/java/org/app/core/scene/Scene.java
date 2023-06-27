package org.app.core.scene;

import org.app.core.RenderSystem;
import org.app.core.input.InputModule;
import org.app.ecs.ECSAdapter;
import org.app.ecs.IECSProtocol;
import org.app.hexagonal.Adapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Scene {
    private InputModule inputModule;
    private Adapter<IECSProtocol> ecs;
    private RenderSystem renderSystem;

    public Scene(InputModule inputModule) {
        this.inputModule = inputModule;

        this.ecs = new ECSAdapter();
        this.renderSystem = this.ecs.getPort().
                registerSystem_s(RenderSystem.class);
    }

    public void saveScene(Connection conn) {
        for (PreparedStatement stmt :
                ecs.getPort().getSaveQueries(conn)) {
            try {
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public InputModule getInputModule() {
        return inputModule;
    }

    public Adapter<IECSProtocol> getEcs() {
        return ecs;
    }

    public RenderSystem getRenderSystem() {
        return renderSystem;
    }
}
