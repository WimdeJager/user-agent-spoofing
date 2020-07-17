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
    print(Type.INF, "");
  }

  public static void separator() {
    print(Type.INF, Strings.repeat("=", 80));
  }

  public static void fileMode() {
    print(Type.INF,
        "==================================FILE MODE=====================================");
  }

  public static void dirMode() {
    print(Type.INF,
        "=================================DIRECTORY MODE=================================");
  }

  public static void printList(UAList l) {
    System.out.print("[INF\t] User Agents: ");
    for (UserAgent ua : l.getList()) {
      System.out.print("\"" + ua.toString() + "\", ");
    }
    System.out.println();
  }

}
