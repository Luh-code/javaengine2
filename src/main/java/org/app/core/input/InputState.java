package org.app.core.input;

public class InputState {
    private int inputTick;
    private float value;

    private InputMode mode;

    public InputState(InputMode mode) {
        this.mode = mode;
    }

    public int getInputTick() {
        return inputTick;
    }

    public void setInputTick(int inputTick) {
        this.inputTick = inputTick;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public InputMode getMode() {
        return mode;
    }

    public void setMode(InputMode mode) {
        this.mode = mode;
    }
}
