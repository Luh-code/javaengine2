package org.app.core.input;

import org.apache.commons.lang3.ArrayUtils;
import org.app.core.GLManager;
import org.app.hexagonal.Adapter;
import org.app.utils.Logger;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardAdapter extends Adapter<IInputProtocol> implements IInputProtocol {
    private int[] keys;
    private String[] aliases;

    private int[] inputs;

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
        Field[] allFields = GLFW.class.getDeclaredFields();
        ArrayList<Integer> keys1 = new ArrayList<>();
        ArrayList<String> aliases1 = new ArrayList<>();
        for (Field f :
                allFields) {
            if ( f.getName().startsWith("GLFW_KEY") && f.getType() == int.class ) {
                try {
                    keys1.add(f.getInt(GLFW.class));
                    aliases1.add(f.getName().substring(5));
                } catch (IllegalAccessException e) {
                    Logger.logAndThrow("Tried to access field '" + f.getName() + "', but failed",
                            RuntimeException.class);
                }
            }
        }
        keys = ArrayUtils.toPrimitive(keys1.toArray(new Integer[0]));
        aliases = aliases1.toArray(new String[0]);

        inputs = new int[keys.length];
        for(int i = 0; i < keys.length; ++i) {
            inputs[i] = registerInput(aliases[i], false);
        }

        long window = GLManager.getWindow();
        glfwSetKeyCallback(window, (wnd, key, scancode, action, mods) -> {
            for (int i = 0; i < keys.length; ++i) {
                if ( key == keys[i] ) {
                    switch (action) {
                        case GLFW_PRESS -> {
                            triggerInput(inputs[i]);
                        }
                        case GLFW_RELEASE -> {
                            releaseInput(inputs[i]);
                        }
                        case GLFW_REPEAT -> { }
                        default -> {
                            Logger.logError(String.format("Tried to execute an invalid action(%d) on key '%s'",
                                    action, aliases[i]));
                        }
                    }
                }
            }
        });
    }

    @Override
    public int test() {
        return 0;
    }
}
