package org.app.core.input;

import org.app.hexagonal.Adapter;
import org.app.hexagonal.HexHelper;
import org.app.hexagonal.PortStatus;
import org.app.utils.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.BitSet;

import static org.app.hexagonal.HexHelper.CONNECTION_EXCEPTION;
import static org.app.hexagonal.HexHelper.TEST_NOT_FOUND_EXCEPTION;

public class InputManager {
    private InputModule inputModule;

    private InputPort[] ports;
    private BitSet initialitzed;

    public InputManager(int portCount) {
        this.inputModule = new InputModule();
        this.ports = new InputPort[portCount];
        for (int i = 0; i < ports.length; ++i) {
            ports[i] = new InputPort();
            HexHelper.connect_module(ports[i], inputModule);
        }
        this.initialitzed = new BitSet(portCount);
    }

    public void connectAdapter(int port, Adapter<IInputProtocol> adapter) {
        if ( port < 0 || port > ports.length ) {
            Logger.logError(String.format(
                    "Couldn't plug adapter '%s' into port(index) '%d', as it is out of range",
                    adapter, port
            ));
            return;
        }
        int error = HexHelper.connect_s(ports[port], adapter);
        switch (error) {
            case TEST_NOT_FOUND_EXCEPTION -> {
                Logger.logWarn("No test was located in adapter '" + adapter + "'");
            }
            case CONNECTION_EXCEPTION -> {
                Logger.logError(String.format(
                        "Could not connect adapter '%s' to port '%s', as a port is already connected",
                        adapter, ports[port]
                ));
            }
        }
        this.initialitzed.set(port, false);
    }

    public void disconnectAdapter(int port) {
        if ( port < 0 || port > ports.length ) {
            Logger.logError(String.format(
                    "Couldn't unplug the adapter from port(index) '%d', as it is out of range",
                    port
            ));
            return;
        }
        if ( (ports[port].getStatus() & PortStatus.HAS_ADAPTER) != PortStatus.HAS_ADAPTER ) {
            Logger.logError("Tried to unplug adapter from port (" + ports[port] + ") without adapter connected");
            return;
        }
        Adapter<IInputProtocol> adapter = ports[port].getAdapter();
        int error = HexHelper.disconnect_s(ports[port], adapter);
        switch (error) {
            case 1, -1 -> {
                return;
            }
        }
        this.initialitzed.set(port, false);
    }

    public void initialize() {
        for (int i = 0; i < ports.length; ++i) {
            InputPort port = ports[i];
            if ( port == null || initialitzed.get(i) ) continue;
            port.initialize();
            int test = port.test();
            if ( test > 0 )
                Logger.logWarn(String.format("Port '%s' has returned a warning: %d",
                        port, test));
            else if ( test < 0 )
                Logger.logError(String.format("Port '%s' has returned an error: %d",
                        port, test));
            initialitzed.set(i, true);
        }
    }

    public void saveConfiguration(Connection conn) {
        StringBuilder mode = new StringBuilder();
        mode.append("INSERT INTO inputmode VALUES");
        mode.append(String.format(
                "(%s), (%s), (%s)",
                InputMode.TRIGGERED.toString(),
                InputMode.RELEASED.toString(),
                InputMode.ANALOG.toString()
        ));

        StringBuilder input = new StringBuilder();
        input.append("INSERT INTO input VALUES");

        for (Integer key :
                inputModule.getRevInputAliases().keySet()) {
            input.append(String.format(
                    "(%d, %s, %s),",
                    key, inputModule.getRevInputAliases().get(key),
                    inputModule.getInputStates().get(key).toString()
            ));
        }
        input.delete(input.length()-2, input.length()-1);

        StringBuilder action = new StringBuilder();
        action.append("INSERT INTO action VALUES");
        for (String key :
                inputModule.getActionAliases().keySet()) {
            action.append(String.format(
                    "(%s),",
                    key
            ));
        }
        action.delete(input.length()-2, input.length()-1);

        StringBuilder action2input = new StringBuilder();
        action2input.append("INSERT INTO action2input VALUES");
        for (String key :
                inputModule.getActionAliases().keySet()) {
            int[] inputs = inputModule.getActionAliases().get(key)
                    .getInputs();
            for (int i = 0; i < inputs.length; ++i) {
                action2input.append(String.format(
                        "(%s, $d),",
                        key, inputs[i]
                ));
            }
        }
        action2input.delete(input.length()-2, input.length()-1);


        try {
            PreparedStatement modeStmt = conn.prepareCall(mode.toString());

            PreparedStatement inputStmt = conn.prepareCall(input.toString());

            PreparedStatement actionStmt = conn.prepareCall(action.toString());
//            int idx = 1;
//            for (String key :
//                    inputModule.getActionAliases().keySet()) {
//
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                ObjectOutputStream oos = new ObjectOutputStream(baos);
//                oos.writeObject(inputModule.getActionAliases().get(key));
//
//                byte[] objAsBytes = baos.toByteArray();
//
//                ByteArrayInputStream bais = new ByteArrayInputStream(objAsBytes);
//                actionStmt.setBinaryStream(idx, bais, (long) objAsBytes.length);
//                ++idx;
//            }
            PreparedStatement action2inputStmt = conn.prepareCall(action2input.toString());

            modeStmt.executeUpdate();
            inputStmt.executeUpdate();
            actionStmt.executeUpdate();
            action2inputStmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public InputModule getInputModule() {
        return inputModule;
    }
}
