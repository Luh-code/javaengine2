package org.app.hexagonal;

import org.app.utils.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HexHelper {
    /**
     * Connects an adapter to a port
     *
     * @param port The port to be plugged into
     * @param adapter The adapter to connect
     * @param <M> The Module Type
     * @param <A> The Adapter Type
     */
    @SuppressWarnings("unused, unchecked")
    public static <M, A> void connect(Port<M, A> port, Adapter<A> adapter) {
        port.setAdapter(adapter);
        adapter.setPort((A)port);
        Logger.logDebug("Connected adapter '" + adapter + "' to port '" + port + "'");
    }

    public static final int CONNECTION_EXCEPTION = -Integer.MAX_VALUE;
    public static final int TEST_NOT_FOUND_EXCEPTION = Integer.MAX_VALUE;

    /**
     * Safe version of HexHelper::connect. Checks if port is already in use and runs the test method of the protocol.
     * When an error in the adapter occurs it is not getting connected.
     *
     * @param port The port to be plugged into
     * @param adapter The adapter to connect
     * @return Return value of the test method in the protocol, if return value is CONNECTION_EXCEPTION the port already had an adapter plugged in. If the return value is TEST_NOT_FOUND_EXCEPTION, there was no test found in the protocol
     * @param <M> The Module Type
     * @param <A> The Adapter Type
     */
    @SuppressWarnings("unused, unchecked, UnusedReturnValue")
    public static <M, A> int connect_s(Port<M, A> port, Adapter<A> adapter) {
        // Check if port already has adapter
        if ( (port.getStatus() & PortStatus.HAS_ADAPTER) == PortStatus.HAS_ADAPTER ) {
            Logger.logError("Port '" + port + "' already has an adapter connected");
            return CONNECTION_EXCEPTION;
        }

        // Connect adapter to port
        connect(port, adapter);

        // Run test
        Method m;
        try {
            m = ((A) port).getClass().getMethod("test");
        } catch (NoSuchMethodException e) {
            Logger.logError("No test found in protocol, keeping connection");
            return TEST_NOT_FOUND_EXCEPTION;
        }

        // Check for Test return type
        int ret = 0;
        try {
            ret = (int)(m.invoke(port));
        } catch (IllegalAccessException | InvocationTargetException e) {
            Logger.logAndThrow(e.getMessage(), RuntimeException.class);
        }
        if ( ret < 0 ) {
            Logger.logError("Test on adapter '" + adapter + "' returned an error: " + ret + ", removing connection");
            disconnect(port, adapter);
        } else if ( ret > 0 ) {
            Logger.logWarn("Test on adapter '" + adapter + "' returned a warning: " + ret + ", keeping connection");
        } else {
            Logger.logDebug("Test on adapter '" + adapter + "' successful, keeping connection");
        }

        // Return test for external handling
        return ret;
    }

    @SuppressWarnings("unused")
    public static <M, A> void disconnect(Port<M, A> port, Adapter<A> adapter) {
        port.setAdapter(null);
        adapter.setPort(null);
        Logger.logDebug("Disconnected adapter '" + adapter + "' from port '" + port + "'");
    }

    @SuppressWarnings("unused, UnusedReturnValue")
    public static <M, A> int disconnect_s(Port<M, A> port, Adapter<A> adapter) {
        if ( port.getAdapter() == null ) {
            Logger.logWarn("No adapter to disconnect found on port '" + port + "' ");
            return 1;
        }
        if ( adapter.getPort() != port ) {
            Logger.logError("Adapter mismatch: adapter '" + adapter + "' is not connected to port '" + port + "'");
            return -1;
        }
        disconnect(port, adapter);
        return 0;
    }

    @SuppressWarnings("unused")
    public static <M, A> void connect_module(Port<M, A> port, M module)
    {
        port.setModule(module);
    }

    @SuppressWarnings("unused")
    public static <M, A> void disconnect_module(Port<M, A> port)
    {
        port.setModule(null);
    }
}
