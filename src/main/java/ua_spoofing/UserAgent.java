package ua_spoofing;

import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;

import java.io.File;
import java.io.IOException;

/**
 * Created by wimde on 11-6-2020.
 */
public class UserAgent {

  private String ua;
  private File location;

  public UserAgent(String ua, File location) {
    this.ua       = ua;
    this.location = location;
  }

  public void classify() throws IOException, ParseException {
    Capabilities fields = new UserAgentService().loadParser().parse(ua);

    OutputHandler.newline();

    OutputHandler.print(OutputHandler.Type.INF,
        "User Agent:\t\"" + ua + "\"");
    OutputHandler.print(OutputHandler.Type.INF,
        "File:\t\t" + location.getPath());

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
  }

  public String toString() {
    return String.format("\"%s\" in file %s", ua, location.getPath());
  }

}
