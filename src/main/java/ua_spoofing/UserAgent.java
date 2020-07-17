package ua_spoofing;

import java.io.File;

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

  public void classify() {

  }

  public String toString() {
    return String.format("\"%s\" in file %s", ua, location.getPath());
  }

}
