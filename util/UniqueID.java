package util;

public final class UniqueID {
    public static int getID() {
        return (int) (Math.random() * 999999);
    }
}