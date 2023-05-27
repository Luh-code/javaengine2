package org.app.hexagonal;

public class Adapter <A> implements IAdapter {
    private A port;

    public void setPort(A port) {
        this.port = port;
    }

    public A getPort() {
        return port;
    }

    public int getStatus() {
        return  (this.port != null ? AdapterStatus.IS_CONNECTED : 0);
    }
}
