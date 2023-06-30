package org.app.core.scene;

import org.apache.commons.lang3.ArrayUtils;
import org.app.core.RenderSystem;
import org.app.core.input.Action;
import org.app.core.input.IInputProtocol;
import org.app.core.input.InputManager;
import org.app.core.input.InputModule;
import org.app.ecs.ECSManager;
import org.app.ecs.ECSPort;
import org.app.hexagonal.Adapter;
import org.app.utils.Logger;
import org.app.utils.SQLiteHelper;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SceneManager {
    private InputManager inputManager;
    private ECSManager ecsManager;
    private ECSPort ecs;

    private Scene currentScene;

    private RenderSystem renderSystem;

    private boolean primed = false;

    public SceneManager(Adapter<IInputProtocol>[] inputAdapters) {
        inputManager = new InputManager(inputAdapters.length);
        for (int i = 0; i < inputAdapters.length; ++i) {
            inputManager.connectAdapter(i, inputAdapters[i]);
        }
        inputManager.initialize();

        ecsManager = new ECSManager();
    }

    private void setupInputs(Connection conn) {
        try {
            PreparedStatement actionStmt;
            ResultSet actionResultSet;
            actionStmt = conn.prepareStatement("""
                    SELECT * FROM action
                    """);
            actionResultSet = actionStmt.executeQuery();

            while (actionResultSet.next()) {
                String alias = actionResultSet.getString(1);

                PreparedStatement a2iStmt;
                ResultSet a2iResultSet;
                a2iStmt = conn.prepareStatement("""
                        SELECT *
                        FROM Action2Input
                        WHERE Action2Input.ActionID IS ?
                        """);
                a2iStmt.setString(1, alias);
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
        primed = true;
        Logger.logInfo("Scene set");
    }

    public void init() {
        if (!primed) {
            Logger.logError("Tried to initialize SceneManager without it being set up");
            return;
        }
        Logger.logInfo("Setting up scene manager...");
        Logger.insetLog();

        this.ecsManager.connectAdapter(this.currentScene.getEcsAdapter());
        this.ecs = (ECSPort) ecsManager.getAdapter().getPort();

        this.currentScene.init();

        ecsManager.swapAdapter(currentScene.getEcsAdapter());
        this.renderSystem = this.currentScene.getRenderSystem();


        // TEMPORARY

        File db = new File("src/main/resources/sql/input.db");
        if ( !db.exists() ) {
            Logger.logWarn("No input database found, creating one");

            InputModule inputModule = getCurrentScene().getInputModule();
            getInputManager().initialize();

            inputModule.registerAction("Exit", new Action(new int[] {
              inputModule.getInputID("KEY_ESCAPE"),
            }));
            inputModule.registerAction("Forwards", new Action(new int[] {
              inputModule.getInputID("KEY_W"),
              inputModule.getInputID("KEY_UP"),
            }));
            inputModule.registerAction("Backwards", new Action(new int[] {
              inputModule.getInputID("KEY_S"),
              inputModule.getInputID("KEY_DOWN"),
            }));
            inputModule.registerAction("Left", new Action(new int[] {
              inputModule.getInputID("KEY_A"),
              inputModule.getInputID("KEY_LEFT"),
            }));
            inputModule.registerAction("Right", new Action(new int[] {
              inputModule.getInputID("KEY_D"),
              inputModule.getInputID("KEY_RIGHT"),
            }));

            inputModule.registerAction("ToggleMouseLock", new Action(new int[] {
              inputModule.getInputID("KEY_L"),
            }));

            inputModule.registerAction("MouseXAxis", new Action(new int[] {
              inputModule.getInputID("MOUSE_X_AXIS"),
            }));
            inputModule.registerAction("MouseYAxis", new Action(new int[] {
              inputModule.getInputID("MOUSE_Y_AXIS"),
            }));
            inputModule.tick();
            Connection conn = SQLiteHelper.createInputDB(new File("src/main/resources/sql/input.db"));
            if ( conn != null )
                inputManager.saveConfiguration(conn);
            db = new File("src/main/resources/sql/input.db");
        }

        setupInputs(SQLiteHelper.connectToDB(db));

        Logger.outsetLog();
        Logger.logInfo("Scene manager set up");
    }

    public void saveScene(Connection conn) {

    }

    public InputManager getInputManager() {
        return inputManager;
    }

    public RenderSystem getRenderSystem() {
        return renderSystem;
    }

    public ECSPort getEcs() {
        return ecs;
    }

    public Scene getCurrentScene() {
        return currentScene;
    }
}
