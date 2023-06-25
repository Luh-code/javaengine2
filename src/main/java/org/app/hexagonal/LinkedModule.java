package org.app.hexagonal;

public class LinkedModule <P> {
    private P port;

    public P getPort() {
        return port;
    }

    public void setPort(P port) {
        this.port = port;
    }

    public int getStatus() {
        return (this.port != null ? AdapterStatus.IS_CONNECTED : 0);
    }
}
