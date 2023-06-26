package org.app.core.data.loading;

public enum ResourceType {
    WAVEFRONT("wavefront"),
    SHADER("shader"),
    TEXTURE("texture");

    private String alias;

    ResourceType(String alias) {
        this.alias = alias;
    }
}
