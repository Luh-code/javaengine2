package org.app.core.input;

import org.app.hexagonal.ControlledProtocol;

public interface IInputProtocol extends ControlledProtocol {
    /**
     * Registers an input with an according alias. If the input is analog, it will be initialized with a value of 0.0f
     * @param inputAlias The alias for the input. Later used for assigning inputs to actions
     * @param analog Is the input analog?
     * @return The inputID
     */
    int registerInput(String inputAlias, boolean analog);

    /**
     * Triggers input of the input according to the ID. If input is already triggered, it will be reset to just pressed
     * @param id The id of the input to be triggered
     */
    void triggerInput(int id);

    /**
     * Releases input of the input according to the ID. In input is already released, it will be reset to just released
     * @param id The id of the input to be released
     */
    void releaseInput(int id);

    /**
     * Updates the analog value of the input according to the ID
     * @param id The id of the input, whose value is to be changed
     * @param value The new value for the analog input
     */
    void analogUpdate(int id, float value);
}
