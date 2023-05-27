package org.app.hexagonal.test;

import org.app.hexagonal.Adapter;

public class TestHelloWorldAdapter extends Adapter<IHelloWorldProtocol> implements IHelloWorldProtocol {
    @Override
    public void helloWorld() {
        getPort().helloWorld();
    }
}
