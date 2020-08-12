package uaspoofing.output;

import com.google.common.base.Strings;

/**
 * Handles all output to System.out
 */
public class OutputHandler {

  /**
   * Types of output (INFO, WARNING or ERROR)
   */
  public enum Type {
    INF,
    WRN,
    ERR
  }

  /**
   * Print to System.out
   * @param t type of output (INFO, WARNING or ERROR)
   * @param msg message to be printed
   */
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

  /**
   * Print newline
   */
  public static void newline() {
    print(Type.INF, "");
  }

  /**
   * Print separator (80 times '=')
   */
  public static void separator() {
    print(Type.INF, Strings.repeat("=", 80));
  }

  /**
   * Indicate File mode
   */
  public static void fileMode() {
    print(Type.INF,
        "==================================FILE MODE=====================================");
  }

  /**
   * Indicate Directory mode
   */
  public static void dirMode() {
    print(Type.INF,
        "=================================DIRECTORY MODE=================================");
  }

}
