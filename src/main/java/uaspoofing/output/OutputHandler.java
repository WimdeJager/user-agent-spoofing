package uaspoofing.output;

import com.google.common.base.Strings;
import uaspoofing.ua.UserAgent;
import uaspoofing.apk.UAList;

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
        System.out.print("\n[INFO\t] " + msg);
        break;
      case WRN:
        System.out.print("\n[WARNING] " + msg);
        break;
      case ERR:
        System.err.print("\n[ERROR\t] " + msg);
        break;
      default:
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

}
