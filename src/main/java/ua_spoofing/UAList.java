package ua_spoofing;

import java.util.ArrayList;

/**
 * Created by wimde on 17-7-2020.
 */
public class UAList {

  private ArrayList<UserAgent> list;

  public UAList() {
    this.list = new ArrayList<UserAgent>();
  }

  public void print() {

  }

  public void add(UserAgent ua) {
    list.add(ua);
  }

  public int size() {
    return list.size();
  }

  public ArrayList<UserAgent> getList() {
    return list;
  }

  public void removeDuplicates() {

  }

}
