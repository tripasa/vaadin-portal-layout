package org.vaadin.sasha.portallayout.client.ui;

public enum AnimationType {
    AT_CLOSE("AT_CLOSE"),
    AT_COLLAPSE("AT_COLLAPSE"),
    AT_ATTACH("AT_ATTACh");
    
    private String name;
    
    private AnimationType(final String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
}
