package util;

public class ProperSingleton {

    private static ProperSingleton me = null;

    private ProperSingleton() {
    }

    public static ProperSingleton instance() {
        if (me == null) {
            me = new ProperSingleton();
        }
        return me;
    }

    @Override
    public Object clone() {
        throw new RuntimeException("Cloning of " + this.getClass().toString() + " not supported.");
    }
}
