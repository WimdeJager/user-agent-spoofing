package ua_spoofing;

import com.google.common.base.Strings;

/**
 * Created by wimde on 29-5-2020.
 */
public class OutputHandler {

  public enum Type {
    INF,
    WRN,
    ERR
  }

  public static void print(Type t, String msg) {
    switch (t) {
      case INF:
        System.out.println("[INFO\t] " + msg);
        break;
      case WRN:
        System.out.println("[WARN\t] " + msg);
        break;
      case ERR:
        System.err.println("[ERROR\t] " + msg);
        break;
      default:
        System.err.println("unknown print type!");
    }
  }

  public static void newline() {
    System.out.println("[INFO\t] " + Strings.repeat("=", 80));
  }

}
