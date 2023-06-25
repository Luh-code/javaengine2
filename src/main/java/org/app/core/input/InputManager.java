package org.app.core.input;

import org.app.hexagonal.Adapter;
import org.app.hexagonal.HexHelper;
import org.app.hexagonal.PortStatus;
import org.app.utils.Logger;

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

    public void plugInAdapter(int port, Adapter<IInputProtocol> adapter) {
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

    public void unplugAdapter(int port) {
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

    public InputModule getInputModule() {
        return inputModule;
    }
}
