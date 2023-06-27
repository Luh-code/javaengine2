package org.app.core.scene;

import org.apache.commons.lang3.ArrayUtils;
import org.app.core.RenderSystem;
import org.app.core.input.Action;
import org.app.core.input.IInputProtocol;
import org.app.core.input.InputManager;
import org.app.ecs.ECSManager;
import org.app.hexagonal.Adapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SceneManager {
    private InputManager inputManager;
    private ECSManager ecsManager;

    private Scene currentScene;

    private RenderSystem renderSystem;

    public SceneManager(Adapter<IInputProtocol>[] inputAdapters) {
        inputManager = new InputManager(inputAdapters.length);
        for (int i = 0; i < inputAdapters.length; ++i) {
            inputManager.connectAdapter(i, inputAdapters[i]);
        }
        inputManager.initialize();
    }

    private void setupInputs(Connection conn) {
        try {
            PreparedStatement actionStmt;
            ResultSet actionResultSet;
            actionStmt = conn.prepareCall("""
                    SELECT * FROM action
                    """);
            actionResultSet = actionStmt.executeQuery();

            while (actionResultSet.next()) {
                String alias = actionResultSet.getString(1);

                PreparedStatement a2iStmt;
                ResultSet a2iResultSet;
                a2iStmt = conn.prepareCall("""
                        SELECT action.alias, action2input.inputID
                        FROM action2input JOIN action
                        WHERE action2input IS ?
                        """);
                a2iStmt.setInt(1, actionResultSet.getInt(0));
                a2iResultSet = a2iStmt.executeQuery();
                ArrayList<Integer> actionInputs = new ArrayList<>();
                while (a2iResultSet.next()) {
                    actionInputs.add(a2iResultSet.getInt(2));
                }
                Action a = new Action(
                        ArrayUtils.toPrimitive(
                                actionInputs.toArray(new Integer[0])
                        )
                );
                this.currentScene.getInputModule()
                        .registerAction(alias, a);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setScene(Scene scene) {
        this.currentScene = scene;
        ecsManager.swapAdapter(scene.getEcs());
        this.renderSystem = scene.getRenderSystem();
    }
}
