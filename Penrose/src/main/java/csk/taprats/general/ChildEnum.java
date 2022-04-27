//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.taprats.general;

import java.util.Enumeration;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class ChildEnum implements Enumeration {
  NodeList list;
  int index;

  ChildEnum(Node var1) {
    this.list = var1.getChildNodes();
    this.index = -1;
    this.advance();
  }

  public final boolean hasMoreElements() {
    return this.index < this.list.getLength();
  }

  public final Object nextElement() {
    Node var1 = this.list.item(this.index);
    this.advance();
    return var1;
  }

  private void advance() {
    ++this.index;

    while (this.index < this.list.getLength()) {
      Node var1 = this.list.item(this.index);
      if (var1.getNodeType() == 1) {
        break;
      }

      ++this.index;
    }

  }
}
