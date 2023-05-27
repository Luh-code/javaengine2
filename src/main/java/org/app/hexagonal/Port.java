package org.app.hexagonal;

public class Port <M, A> implements IPort{

    private M module;
    private Adapter<A> adapter;

    public void setModule(M module) {
        this.module = module;
    }

    public M getModule() {
        return this.module;
    }

    public void setAdapter(Adapter<A> adapter) {
        this.adapter = adapter;
        //adapter.setPort((A) this);
    }

    public Adapter<A> getAdapter() {
        return adapter;
    }

    public int isEmpty() {
        return  (this.module != null ? PortStatus.HAS_MODULE : 0) |
                (this.adapter != null ? PortStatus.HAS_ADAPTER : 0);
    }
}
