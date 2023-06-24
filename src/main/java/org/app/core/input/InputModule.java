package org.app.core.input;

import kotlin.Pair;
import org.app.utils.Logger;

import java.util.*;

public class InputModule implements IInputProtocol {
    private Map<String, Integer> inputAliases = new HashMap<>();
    private Map<Integer, InputState> inputStates = new HashMap<>();
    private Queue<Pair<Integer, InputState>> inputQueue = new LinkedList<>();
    private int nextID = -1;

    private int inputTick = 0;

    @Override
    public int registerInput(String inputAlias, boolean analog) {
        if( inputAlias == null )
            Logger.logAndThrow("Tried to register input with empty alias!", RuntimeException.class);
        if ( inputAliases.containsKey(inputAlias) )
            Logger.logWarn("Re-registering input alias '" + inputAlias
                    + "' (analog=" + analog + ")");
        inputAliases.put(inputAlias, ++nextID);
        InputState state = (analog ? InputState.ANALOG : InputState.RELEASED);
//        state.setInputTick(inputTick);
        state.setValue(0.0f);
        inputQueue.add(new Pair<>(nextID, state));
        //inputStates.put(nextID, state);
        return nextID;
    }

    @Override
    public void triggerInput(int id) {
        if ( !inputStates.containsKey(id) ) {
            Logger.logError("Tried to trigger input for invalid ID: '" + id + "'");
            return;
        }
        InputState state = inputStates.get(id);
        if ( state != InputState.ANALOG )
            state = InputState.PRESSED;
        else
            Logger.logWarn("Tried to trigger analog input: '" + id + "', setting value to 1.0f");
//        state.setInputTick(inputTick);
        state.setValue(1.0f);
//        inputStates.put(id, state);
        inputQueue.add(new Pair<>(id, state));
    }

    @Override
    public void releaseInput(int id) {
        if ( !inputStates.containsKey(id)  ) {
            Logger.logError("Tried to release input for invalid ID: '" + id + "'");
            return;
        }
        InputState state = inputStates.get(id);
        if ( state != InputState.ANALOG )
            state = InputState.RELEASED;
        else
            Logger.logWarn("Tried to release analog input: '" + id + "', setting value to 0.0f");
//        state.setInputTick(inputTick);
        state.setValue(0.0f);
//        inputStates.put(id, state);
        inputQueue.add(new Pair<>(id, state));
    }

    @Override
    public void analogUpdate(int id, float value) {
        if ( !inputStates.containsKey(id)  ) {
            Logger.logError("Tried to update analog input for invalid ID: '" + id + "'");
            return;
        }
        InputState state = inputStates.get(id);
        if ( state != InputState.ANALOG )
            Logger.logWarn("Tried to update analog input for binary input: '" + id + "'");
//        state.setInputTick(inputTick);
        state.setValue(value);
//        inputStates.put(id, state);
        inputQueue.add(new Pair<>(id, state));
    }

    public void tick() {
        ++inputTick;
        while ( !inputQueue.isEmpty() ) {
            Pair<Integer, InputState> temp = inputQueue.remove();
            int id = temp.component1();
            InputState state = temp.component2();
            state.setInputTick(inputTick);
            inputStates.put(id, state);
        }
    }

    @Override
    public int test() {
        return 0;
    }

    public boolean isInputPressed(int id) {
        if ( !inputStates.containsKey(id) )
            Logger.logAndThrow("Tried to retrieve input from invalid ID: '" + id + "'", RuntimeException.class);
        return inputStates.get(id) == InputState.PRESSED;
    }

    public boolean isInputPressed(String alias) {
        if ( !inputAliases.containsKey(alias) )
            Logger.logAndThrow("Tried to retrieve ID from invalid alias: '" + alias + "'", RuntimeException.class);
        int id = inputAliases.get(alias);
        if ( !inputStates.containsKey(id) )
            Logger.logAndThrow("Tried to retrieve input from invalid ID: '" + id + "'", RuntimeException.class);
        return inputStates.get(id) == InputState.PRESSED;
    }
    public boolean isInputReleased(int id) {
        if ( !inputStates.containsKey(id) )
            Logger.logAndThrow("Tried to retrieve input from invalid ID: '" + id + "'", RuntimeException.class);
        return inputStates.get(id) == InputState.RELEASED;
    }

    public boolean isInputReleased(String alias) {
        if ( !inputAliases.containsKey(alias) )
            Logger.logAndThrow("Tried to retrieve ID from invalid alias: '" + alias + "'", RuntimeException.class);
        int id = inputAliases.get(alias);
        if ( !inputStates.containsKey(id) )
            Logger.logAndThrow("Tried to retrieve input from invalid ID: '" + id + "'", RuntimeException.class);
        return inputStates.get(id) == InputState.RELEASED;
    }

    public boolean isInputJustPressed(int id) {
        if ( !inputStates.containsKey(id) )
            Logger.logAndThrow("Tried to retrieve input from invalid ID: '" + id + "'", RuntimeException.class);
        return inputStates.get(id) == InputState.PRESSED && inputStates.get(id).getInputTick() == inputTick;
    }

    public boolean isInputJustPressed(String alias) {
        if ( !inputAliases.containsKey(alias) )
            Logger.logAndThrow("Tried to retrieve ID from invalid alias: '" + alias + "'", RuntimeException.class);
        int id = inputAliases.get(alias);
        if ( !inputStates.containsKey(id) )
            Logger.logAndThrow("Tried to retrieve input from invalid ID: '" + id + "'", RuntimeException.class);
        return inputStates.get(id) == InputState.PRESSED && inputStates.get(id).getInputTick() == inputTick;
    }

    public boolean isInputJustReleased(int id) {
        if ( !inputStates.containsKey(id) )
            Logger.logAndThrow("Tried to retrieve input from invalid ID: '" + id + "'", RuntimeException.class);
        return inputStates.get(id) == InputState.RELEASED && inputStates.get(id).getInputTick() == inputTick;
    }

    public boolean isInputJustReleased(String alias) {
        if ( !inputAliases.containsKey(alias) )
            Logger.logAndThrow("Tried to retrieve ID from invalid alias: '" + alias + "'", RuntimeException.class);
        int id = inputAliases.get(alias);
        if ( !inputStates.containsKey(id) )
            Logger.logAndThrow("Tried to retrieve input from invalid ID: '" + id + "'", RuntimeException.class);
        return inputStates.get(id) == InputState.RELEASED && inputStates.get(id).getInputTick() == inputTick;
    }

    public float getAnalogInputValue(int id) {
        if ( !inputStates.containsKey(id) )
            Logger.logAndThrow("Tried to retrieve analog input from invalid ID: '" + id + "'", RuntimeException.class);
        if ( inputStates.get(id) != InputState.ANALOG )
            Logger.logWarn("Tried to retrieve analog input from binary input, returning 1.0f or 0.0f");
        return inputStates.get(id).getValue();
    }

    public float getAnalogInputValue(String alias) {
        if ( !inputAliases.containsKey(alias) )
            Logger.logAndThrow("Tried to retrieve ID from invalid alias: '" + alias + "'", RuntimeException.class);
        int id = inputAliases.get(alias);
        if ( !inputStates.containsKey(id) )
            Logger.logAndThrow("Tried to retrieve analog input from invalid ID: '" + id + "'", RuntimeException.class);
        if ( inputStates.get(id) != InputState.ANALOG )
            Logger.logWarn("Tried to retrieve analog input from binary input, returning 1.0f or 0.0f");
        return inputStates.get(id).getValue();
    }

    public boolean hasAnalogInputChanged(int id) {
        if ( !inputStates.containsKey(id) )
            Logger.logAndThrow("Tried to retrieve input from invalid ID: '" + id + "'", RuntimeException.class);
        if ( inputStates.get(id) != InputState.ANALOG )
            Logger.logWarn("Tried to check change in input from binary input");
        return inputStates.get(id).getInputTick() == inputTick;
    }
}
