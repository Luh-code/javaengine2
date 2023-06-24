package org.app.core.input;

public enum InputState {
    PRESSED,
    RELEASED,
    ANALOG;

    private int inputTick;
    private float value;

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
}
