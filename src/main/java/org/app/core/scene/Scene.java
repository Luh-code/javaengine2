package org.app.core.scene;

import org.app.core.input.InputModule;
import org.app.ecs.IECSProtocol;
import org.app.hexagonal.Adapter;

public class Scene {
    private InputModule inputModule;
    private Adapter<IECSProtocol> ecs;

    public Scene(InputModule inputModule) {
        this.inputModule = inputModule;

    }

    public InputModule getInputModule() {
        return inputModule;
    }

    public Adapter<IECSProtocol> getEcs() {
        return ecs;
    }
}
