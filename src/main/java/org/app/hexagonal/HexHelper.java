package org.app.hexagonal;

public class HexHelper {
    @SuppressWarnings("unchecked")
    public static <M, A> void connect(Port<M, A> port, Adapter<A> adapter) {
        port.setAdapter(adapter);
        adapter.setPort((A)port);
    }

    public static <M, A> void disconnect(Port<M, A> port, Adapter<A> adapter) {
        port.setAdapter(null);
        adapter.setPort(null);
    }

    public static <M, A> void connect_module(Port<M, A> port, M module)
    {
        port.setModule(module);
    }

    public static <M, A> void disconnect_module(Port<M, A> port)
    {
        port.setModule(null);
    }
}
