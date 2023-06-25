package org.app.core.input;

import org.app.hexagonal.Port;
import org.app.hexagonal.test.IHelloWorldProtocol;

public class InputPort extends Port<InputModule, IInputProtocol> implements IInputProtocol {
    @Override
    public int registerInput(String inputAlias, boolean analog) {
        return getModule().registerInput(inputAlias, analog);
    }

    @Override
    public void triggerInput(int id) {
        getModule().triggerInput(id);
    }

    @Override
    public void releaseInput(int id) {
        getModule().releaseInput(id);
    }

    @Override
    public void analogUpdate(int id, float value) {
        getModule().analogUpdate(id, value);
    }

    @Override
    public void initialize() {
        ((IInputProtocol)getAdapter()).initialize();
    }

    @Override
    public int test() {
        return getModule().test();
    }
}
