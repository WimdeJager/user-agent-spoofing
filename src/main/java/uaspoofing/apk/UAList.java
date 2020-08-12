package uaspoofing.apk;

import uaspoofing.ua.UserAgent;

import java.util.ArrayList;

/**
 * Wrapper for ArrayList containing UserAgents
 */
public class UAList {

  /**
   * The list of ```UserAgent```s
   */
  private ArrayList<UserAgent> list;

  /**
   * Constructor, creates ArrayList
   */
  public UAList() {
    this.list = new ArrayList<UserAgent>();
  }

  /**
   * Add a UA to list
   * @param ua the UA to be added
   */
  public void add(UserAgent ua) {
    list.add(ua);
  }

  /**
   * Returns number of UAs in list
   * @return size of list
   */
  int size() {
    return list.size();
  }

  /**
   * Call classifyAll on all UAs in ```list```
   */
  void classifyAll() {
    for (UserAgent ua : list) {
      ua.classify();
    }
  }

}
