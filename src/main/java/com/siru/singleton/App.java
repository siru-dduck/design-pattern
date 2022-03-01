package com.siru.singleton;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class App {

    public static void main(String[] args) {
        Settings settings1 = Settings.INSTANCE;
        Settings settings2 = Settings.INSTANCE;

        System.out.println(settings1 == settings2);
    }
}
