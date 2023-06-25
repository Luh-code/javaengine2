package org.app.core.input;

import org.apache.commons.lang3.ArrayUtils;
import org.app.utils.Logger;

public class Action {
    private int[] inputs;

    public Action(int[] inputs) {
        this.inputs = inputs;
    }

    public void swapInput(int idx, int newInput) {
        if ( idx >= inputs.length ) {
            Logger.logError("Cannot swap input at index " + idx + ", as the index doesn't exist");
            return;
        }
        inputs[idx] = newInput;
    }

    public void replaceInputs(int[] newInputs) {
        inputs = newInputs;
    }

    public void addInput(int newInput) {
        inputs = ArrayUtils.addAll(inputs, newInput);
    }

    public void addInputs(int[] newInputs) {
        inputs = ArrayUtils.addAll(inputs, newInputs);
    }

    public int[] getInputs() {
        return inputs;
    }
}
