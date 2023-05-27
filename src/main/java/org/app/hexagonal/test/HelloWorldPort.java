package org.app.hexagonal.test;

import org.app.hexagonal.Port;

public class HelloWorldPort extends Port<HelloWorldModule, IHelloWorldProtocol> implements IHelloWorldProtocol {

    @Override
    public void helloWorld() {
        getModule().helloWorld();
    }
}
