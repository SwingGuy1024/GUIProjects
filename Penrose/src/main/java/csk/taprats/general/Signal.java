//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.taprats.general;

import java.util.Observable;

public class Signal extends Observable {
  public Signal() {
  }

  public void signotify(Object var1) {
    this.setChanged();
    this.notifyObservers(var1);
    this.clearChanged();
  }
}
