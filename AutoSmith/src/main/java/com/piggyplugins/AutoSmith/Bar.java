package com.piggyplugins.AutoSmith;

public enum Bar {
    BRONZE, IRON, STEEL, MITHRIL, ADAMANT;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase() + " bar";
    }

    public String platebody() {
        return name().charAt(0) + name().substring(1).toLowerCase() + " platebody";
    }

}
