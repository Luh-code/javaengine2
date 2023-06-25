package org.app.ecs;

import org.app.hexagonal.Adapter;
import org.app.hexagonal.HexHelper;

public class ECSManager {
    private ECSModule module;
    private ECSPort port;

    public ECSManager() {
        this.module = new ECSModule();
        this.port = new ECSPort();
        HexHelper.link_module(port, module);
    }

    public void connectAdapter(Adapter<IECSProtocol> adapter) {
        HexHelper.connect_s(port, adapter);
    }

    public void swapAdapter(Adapter<IECSProtocol> adapter) {
        HexHelper.connect(port, adapter);
    }

    public Adapter<IECSProtocol> getAdapter() {
        return port.getAdapter();
    }
}
