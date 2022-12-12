package com.pandoaspen.mercury.common.utils;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class ModuleHack {

    private static boolean isJava8Plus() {
        try {
            return ModuleHack.class.getClassLoader().loadClass("java.lang.Module") != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


    public static void openModuleAccess() {
        try {
            if (!isJava8Plus()) {
                return;
            }

            ClassLoader cl = ModuleHack.class.getClassLoader();
            Class<?> cModule = cl.loadClass("java.lang.Module");
            Method mModuleImplAddOpens = cModule.getDeclaredMethod("implAddOpens", String.class, cModule);
            //        Method mModuleImplAddOpens = cModule.getDeclaredMethod("implAddOpensToAllUnnamed", String.class);
            Object moduleAwe = cModule.cast(Class.class.getDeclaredMethod("getModule").invoke(ModuleHack.class));

            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            Unsafe u = (Unsafe) f.get(null);

            u.putBooleanVolatile(mModuleImplAddOpens, detectOverrideOffset(u), true);

            Class<?> cModuleLayer = cl.loadClass("java.lang.ModuleLayer");
            Object moduleLayer = cModuleLayer.cast(cModuleLayer.getMethod("boot").invoke(null));

            Set<Object> modules = (Set<Object>) cModuleLayer.getMethod("modules").invoke(moduleLayer);

            for (Object module : modules) {
                Set<String> packageNames = (Set<String>) cModule.getDeclaredMethod("getPackages").invoke(module);
                for (String pn : packageNames) {
                    //                mModuleImplAddOpens.invoke(module, pn);
                    mModuleImplAddOpens.invoke(module, pn, moduleAwe);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static long detectOverrideOffset(final Unsafe u) {
        class HackyClass {
            private boolean privateMethod() {
                return false;
            }
        }

        final Class<HackyClass> cls = HackyClass.class;
        for (long i : new long[]{16, 12}) {
            try {
                final Method method = cls.getDeclaredMethods()[0];
                method.setAccessible(false);

                boolean old = u.getBoolean(method, i);
                u.putBoolean(method, i, true);

                if (method.isAccessible()) {
                    return i;
                }
                u.putBoolean(method, i, old);
            } catch (Throwable ex) {
                // Ignore me
            }
        }

        throw new IllegalStateException("Unable to detect 'override' field offset. Your JRE is not supported.");
    }

    public static Unsafe unsafe() throws IllegalAccessException, NoSuchFieldException {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }

}
