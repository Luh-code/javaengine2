package org.app.core.input;

import org.app.core.GLManager;
import org.app.hexagonal.Adapter;

import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;

public class MouseAdapter extends Adapter<IInputProtocol> implements IInputProtocol {
    @Override
    public int registerInput(String inputAlias, boolean analog) {
        return getPort().registerInput(inputAlias, analog);
    }

    @Override
    public void triggerInput(int id) {
        getPort().triggerInput(id);
    }

    @Override
    public void releaseInput(int id) {
        getPort().releaseInput(id);
    }

    @Override
    public void analogUpdate(int id, float value) {
        getPort().analogUpdate(id, value);
    }

    @Override
    public void initialize() {
        int mouseX = registerInput("MOUSE_X_AXIS", true);
        int mouseY = registerInput("MOUSE_Y_AXIS", true);

        glfwSetCursorPosCallback(GLManager.getWindow(), (handle, xpos, ypos) -> {
            analogUpdate(mouseX, (float)xpos);
            analogUpdate(mouseY, (float)ypos);
        });
    }

    @Override
    public int test() {
        return 0;
    }
}
