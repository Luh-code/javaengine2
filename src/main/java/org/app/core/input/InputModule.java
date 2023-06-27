package org.app.core.input;

import kotlin.Pair;
import org.app.utils.Logger;

import java.util.*;

public class InputModule {
    private Map<String, Integer> inputAliases = new HashMap<>();
    private Map<Integer, String> revInputAliases = new HashMap<>();
    private Map<Integer, InputState> inputStates = new HashMap<>();
    private Queue<Pair<Integer, InputState>> inputQueue = new LinkedList<>();
    private int nextID = -1;

    private int inputTick = 0;

    private float binaryToAnalogSpeed = 30.0f;

    private Map<String, Action> actionAliases = new HashMap<>();
    private Map<Action, String> revActionAliases = new HashMap<>();

    public int registerInput(String inputAlias, boolean analog) {
        if( inputAlias == null )
            Logger.logAndThrow("Tried to register input with empty alias!", RuntimeException.class);
        if ( inputAliases.containsKey(inputAlias) )
            Logger.logWarn("Re-registering input alias '" + inputAlias
                    + "' (analog=" + analog + ")");
        inputAliases.put(inputAlias, ++nextID);
        revInputAliases.put(nextID, inputAlias);
        InputState state = new InputState((analog ? InputMode.ANALOG : InputMode.RELEASED));
//        state.setInputTick(inputTick);
        state.setValue(0.0f);
        inputQueue.add(new Pair<>(nextID, state));
        //inputStates.put(nextID, state);
        return nextID;
    }

    public void triggerInput(int id) {
        if ( !inputStates.containsKey(id) ) {
            Logger.logError("Tried to trigger input for invalid ID: '" + id + "'");
            return;
        }
        InputState state = inputStates.get(id);
        InputMode mode = state.getMode();
        if ( mode == InputMode.TRIGGERED )
            return;
        if ( mode != InputMode.ANALOG )
            state.setMode(InputMode.TRIGGERED);
        else
            Logger.logWarn("Tried to trigger analog input: '" + id + "', setting value to 1.0f");
//        state.setInputTick(inputTick);
        state.setValue(binaryToAnalogSpeed);
//        inputStates.put(id, state);
        inputQueue.add(new Pair<>(id, state));
    }

    public void releaseInput(int id) {
        if ( !inputStates.containsKey(id)  ) {
            Logger.logError("Tried to release input for invalid ID: '" + id + "'");
            return;
        }
        InputState state = inputStates.get(id);
        InputMode mode = state.getMode();
        if ( mode == InputMode.RELEASED )
            return;
        if ( mode != InputMode.ANALOG )
            state.setMode(InputMode.RELEASED);
        else
            Logger.logWarn("Tried to release analog input: '" + id + "', setting value to -1.0f");
//        state.setInputTick(inputTick);
        state.setValue(-binaryToAnalogSpeed);
//        inputStates.put(id, state);
        inputQueue.add(new Pair<>(id, state));
    }

    public void analogUpdate(int id, float value) {
        if ( !inputStates.containsKey(id)  ) {
            Logger.logError("Tried to update analog input for invalid ID: '" + id + "'");
            return;
        }
        InputState state = inputStates.get(id);
        InputMode mode = state.getMode();
        if ( mode != InputMode.ANALOG )
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

    public int test() {
        return 0;
    }

    public int getInputID(String alias) {
        if ( !inputAliases.containsKey(alias) )
            Logger.logAndThrow("Tried to retrieve ID from invalid alias: '" + alias + "'", RuntimeException.class);
        return inputAliases.get(alias);
    }

    public String getAlias(int id) {
        if ( !revInputAliases.containsKey(id) )
            Logger.logAndThrow("Tried to retrieve alias from invalid ID: '" + id + "'", RuntimeException.class);
        return revInputAliases.get(id);
    }

    public boolean isInputTriggered(int id) {
        if ( !inputStates.containsKey(id) )
            Logger.logAndThrow("Tried to retrieve input from invalid ID: '" + id + "'", RuntimeException.class);
        return inputStates.get(id).getMode() == InputMode.TRIGGERED;
    }

    public boolean isInputTriggered(String alias) {
        return isInputTriggered(getInputID(alias));
    }
    public boolean isInputReleased(int id) {
        if ( !inputStates.containsKey(id) )
            Logger.logAndThrow("Tried to retrieve input from invalid ID: '" + id + "'", RuntimeException.class);
        return inputStates.get(id).getMode() == InputMode.RELEASED;
    }

    public boolean isInputReleased(String alias) {
        return isInputReleased(getInputID(alias));
    }

    public boolean isInputJustTriggered(int id) {
        if ( !inputStates.containsKey(id) )
            Logger.logAndThrow("Tried to retrieve input from invalid ID: '" + id + "'", RuntimeException.class);
        return inputStates.get(id).getMode() == InputMode.TRIGGERED && inputStates.get(id).getInputTick() == inputTick;
    }

    public boolean isInputJustTriggered(String alias) {
        return isInputJustTriggered(alias);
    }

    public boolean isInputJustReleased(int id) {
        if ( !inputStates.containsKey(id) )
            Logger.logAndThrow("Tried to retrieve input from invalid ID: '" + id + "'", RuntimeException.class);
        return inputStates.get(id).getMode() == InputMode.RELEASED && inputStates.get(id).getInputTick() == inputTick;
    }

    public boolean isInputJustReleased(String alias) {
        return isInputJustReleased(getInputID(alias));
    }

    public float getAnalogInputValue(int id) {
        if ( !inputStates.containsKey(id) )
            Logger.logAndThrow("Tried to retrieve analog input from invalid ID: '" + id + "'", RuntimeException.class);
        if ( inputStates.get(id).getMode() != InputMode.ANALOG )
            Logger.logWarn("Tried to retrieve analog input from binary input");
        return inputStates.get(id).getValue();
    }

    public float getAnalogInputValue(String alias) {
        return getAnalogInputValue(getInputID(alias));
    }

    public boolean hasAnalogInputChanged(int id) {
        if ( !inputStates.containsKey(id) )
            Logger.logAndThrow("Tried to retrieve input from invalid ID: '" + id + "'", RuntimeException.class);
        if ( inputStates.get(id).getMode() != InputMode.ANALOG )
            Logger.logWarn("Tried to check change in input from binary input");
        return inputStates.get(id).getInputTick() == inputTick;
    }

    public boolean hasAnalogInputChanged(String alias) {
        return hasAnalogInputChanged(getInputID(alias));
    }


    public void registerAction(String actionAlias, Action a) {
        if( actionAlias == null )
            Logger.logAndThrow("Tried to register action with empty alias!", RuntimeException.class);
        if ( actionAliases.containsKey(actionAlias) )
            Logger.logWarn("Re-registering action alias '" + actionAlias + "'");
        actionAliases.put(actionAlias, a);
        revActionAliases.put(a, actionAlias);
    }

    public Action getAction(String alias) {
        if ( !actionAliases.containsKey(alias) )
            Logger.logAndThrow("Tried to retrieve action for invalid alias: '" + alias + "'", RuntimeException.class);
        return actionAliases.get(alias);
    }

    public String getActionAlias(Action a) {
        if ( !revActionAliases.containsKey(a) )
            Logger.logAndThrow("Tried to retrieve alias for invalid action: '" + "'", RuntimeException.class);
        return revActionAliases.get(a);
    }

    public void deleteAction(Action a) {
        actionAliases.remove(getActionAlias(a));
        revActionAliases.remove(a);
    }

    public void deleteAction(String alias) {
        deleteAction(getAction(alias));
    }

    public boolean isActionTriggered(Action a) {
        for (int id :
                a.getInputs()) {
            InputState state = inputStates.get(id);
            InputMode mode = state.getMode();
            if ( mode == InputMode.TRIGGERED )
                if ( isInputTriggered(id) ) return true;
        }
        return false;
    }

    public boolean isActionTriggered(String alias) {
        return isActionTriggered(getAction(alias));
    }

    public boolean isActionReleased(Action a) {
        for (int id :
                a.getInputs()) {
            InputState state = inputStates.get(id);
            InputMode mode = state.getMode();
            if ( mode == InputMode.RELEASED )
                if ( isInputTriggered(id) ) return true;
        }
        return false;
    }

    public boolean isActionReleased(String alias) {
        return isActionReleased(getAction(alias));
    }

    public boolean isActionJustTriggered(Action a) {
        for (int id :
                a.getInputs()) {
            InputState state = inputStates.get(id);
            InputMode mode = state.getMode();
            if ( mode == InputMode.TRIGGERED )
                if ( isInputJustTriggered(id) ) return true;
        }
        return false;
    }

    public boolean isActionJustTriggered(String alias) {
        return isActionJustTriggered(getAction(alias));
    }

    public boolean isActionJustReleased(Action a) {
        for (int id :
                a.getInputs()) {
            InputState state = inputStates.get(id);
            InputMode mode = state.getMode();
            if ( mode == InputMode.RELEASED )
                if ( isInputJustReleased(id) ) return true;
        }
        return false;
    }

    public boolean isActionJustReleased(String alias) {
        return isActionJustReleased(getAction(alias));
    }

    public float getAnalogActionValue(Action a) {
        boolean hasAnalog = false;
        float value = 0.0f;
        for (int id :
                a.getInputs()) {
            InputState state = inputStates.get(id);
            InputMode mode = state.getMode();
            if ( mode == InputMode.ANALOG ) {
                hasAnalog = true;
                value += getAnalogInputValue(id);
            }
        }
        if ( hasAnalog ) {
            return value;
        }
        Logger.logWarn("Tried to retrieve analog value from binary action: '" + a + "'");
        return (isActionTriggered(a) ? binaryToAnalogSpeed : 0.0f);
    }

    public float getAnalogActionValue(String alias) {
        return getAnalogActionValue(getAction(alias));
    }

    public Map<String, Integer> getInputAliases() {
        return inputAliases;
    }

    public Map<Integer, String> getRevInputAliases() {
        return revInputAliases;
    }

    public Map<Integer, InputState> getInputStates() {
        return inputStates;
    }

    public Map<String, Action> getActionAliases() {
        return actionAliases;
    }

    public Map<Action, String> getRevActionAliases() {
        return revActionAliases;
    }
}
