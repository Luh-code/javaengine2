package org.app.core.scene;

import org.app.core.input.IInputProtocol;
import org.app.core.input.InputManager;
import org.app.ecs.ECSManager;
import org.app.hexagonal.Adapter;

public class SceneManager {
    private InputManager inputManager;
    private ECSManager ecsManager;

    private Scene currentScene;

    public SceneManager(Adapter<IInputProtocol>[] inputAdapters) {
        inputManager = new InputManager(inputAdapters.length);
        for (int i = 0; i < inputAdapters.length; ++i) {
            inputManager.connectAdapter(i, inputAdapters[i]);
        }
        inputManager.initialize();
    }

    public void setupInputs() {

    }

    public void setScene(Scene scene) {
        this.currentScene = scene;
        ecsManager.swapAdapter(scene.getEcs());
    }
}
