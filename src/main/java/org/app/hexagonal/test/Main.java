package org.app.hexagonal.test;

import org.app.hexagonal.HexHelper;

public class Main {
    public static void main(String[] args) {
        HelloWorldPort port = new HelloWorldPort();
        HelloWorldModule module = new HelloWorldModule();
        TestHelloWorldAdapter adapter = new TestHelloWorldAdapter();
        HexHelper.connect_module(port, module);
        HexHelper.connect(port, adapter);

        adapter.helloWorld();
    }
}