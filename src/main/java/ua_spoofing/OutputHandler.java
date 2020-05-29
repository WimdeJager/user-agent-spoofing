package ua_spoofing;

/**
 * Created by wimde on 29-5-2020.
 */
public class OutputHandler {

    public enum Type {
        INFO,
        WARNING,
        ERROR,
        EXT
    }

    public void print(Type t, String msg) {
        switch (t) {
            case INFO:
                System.out.println("[INFO\t] " + msg);
            case WARNING:
                System.out.println("[WARN\t] " + msg);
            case ERROR:
                System.err.println("[ERROR\t] " + msg);
            case EXT:
                System.out.println("[EXT\t] " + msg);
            default:
                System.err.println("unknown print type!");
        }
    }

}
