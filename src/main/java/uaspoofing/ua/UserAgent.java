package uaspoofing.ua;

import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentService;
import uaspoofing.output.OutputHandler;

import java.io.File;
import java.io.IOException;

/**
 * Class representing a User-Agent string
 */
public class UserAgent {

  /**
   * String containing the UA
   */
  private String ua;

  /**
   * File that it was found in
   */
  private File location;

  /**
   * Constructor
   * @param ua the UA
   * @param location File that UA was found in
   */
  public UserAgent(String ua, File location) {
    this.ua       = ua;
    this.location = location;
  }

  /**
   * Try to retrieve information about UA
   */
  public void classify() {
    OutputHandler.newline();

    OutputHandler.print(OutputHandler.Type.INF,
        "User Agent:\t\"" + ua + "\"");
    OutputHandler.print(OutputHandler.Type.INF,
        "File:\t\t" + location.getPath());

    Capabilities fields;

    try {
      OutputHandler.print(OutputHandler.Type.INF, "parsing...");

      // use browscap to retrieve information
      fields = new UserAgentService().loadParser().parse(ua);

      OutputHandler.print(OutputHandler.Type.INF,
          "Browser:\t\t" + fields.getBrowser());
      OutputHandler.print(OutputHandler.Type.INF,
          "Browser type:\t\t" + fields.getBrowserType());
      OutputHandler.print(OutputHandler.Type.INF,
          "Browser version:\t" + fields.getBrowserMajorVersion());
      OutputHandler.print(OutputHandler.Type.INF,
          "Device type:\t\t" + fields.getDeviceType());
      OutputHandler.print(OutputHandler.Type.INF,
          "Platform:\t\t" + fields.getPlatform());
      OutputHandler.print(OutputHandler.Type.INF,
          "Platform version:\t" + fields.getPlatformVersion());
      OutputHandler.newline();

      // classify
      if (!fields.getDeviceType().equals("Mobile Phone")
          && !fields.getDeviceType().equals("Unknown")) {
        OutputHandler.print(OutputHandler.Type.INF,
            "POTENTIAL THREAT: UA does not come from a mobile phone!" +
                " (Device type: " + fields.getDeviceType() + ")");
      }

      else if (!fields.getPlatform().equals("Android")
          && !fields.getPlatform().equals("Unknown")) {
        OutputHandler.print(OutputHandler.Type.INF,
            "POTENTIAL THREAT: UA does not come from an Android device!" +
                " (Platform: " + fields.getPlatform() + ")");
      }

      else if (fields.getDeviceType().equals("Mobile Phone")
          && fields.getPlatform().equals("Android")) {
        try {
          double version = Double.parseDouble(fields.getPlatformVersion());
          if (version < 4.0) {
            OutputHandler.print(OutputHandler.Type.INF,
                "POTENTIAL THREAT: UA comes from old Android version!");
          } else {
            OutputHandler.print(OutputHandler.Type.INF,
                "No threats found.");
          }
        } catch (NumberFormatException e) {
          OutputHandler.print(OutputHandler.Type.INF,
              "Platform version could not be parsed!");
        }


      }

      else {
        OutputHandler.print(OutputHandler.Type.INF,
            "Unknown UA.");
      }

    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      OutputHandler.print(OutputHandler.Type.WRN,
          "User Agent could not be parsed!");
    }
  }

}
