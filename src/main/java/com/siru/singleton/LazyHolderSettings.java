package com.siru.singleton;

public class LazyHolderSettings {

    private LazyHolderSettings() {}

    private static class SettingsHolder {
        private static final LazyHolderSettings INSTANCE = new LazyHolderSettings();
    }

    public static LazyHolderSettings getInstance() {
        return SettingsHolder.INSTANCE;
    }
}
