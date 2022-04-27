//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.taprats.general;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;

public class Input {
  private Reader r;
  private PushbackReader pr;
  private StringBuffer sb;

  public Input(Reader var1) {
    this.r = var1;
    this.pr = new PushbackReader(var1);
    this.sb = new StringBuffer();
  }

  public String readString() throws IOException {
    int var1;
    char var2;
    do {
      var1 = this.pr.read();
      if (var1 == -1) {
        throw new EOFException();
      }

      var2 = (char) var1;
    } while (Character.isWhitespace(var2));

    while (true) {
      this.sb.append(var2);
      var1 = this.pr.read();
      if (var1 == -1) {
        break;
      }

      var2 = (char) var1;
      if (Character.isWhitespace(var2)) {
        this.pr.unread(var1);
        break;
      }
    }

    String var3 = this.sb.toString();
    this.sb.delete(0, this.sb.length());
    return var3;
  }

  public int readInt() throws IOException {
    String var1 = this.readString();

    try {
      return Integer.parseInt(var1);
    } catch (NumberFormatException var3) {
      throw new IOException("Couldn't read an integer");
    }
  }

  public double readDouble() throws IOException {
    String var1 = this.readString();

    try {
      return new Double(var1);
    } catch (NumberFormatException var3) {
      throw new IOException("Couldn't read a floating point number");
    }
  }

  public boolean readBoolean() throws IOException {
    String var1 = this.readString();
    if (var1.equals("true")) {
      return true;
    } else if (var1.equals("false")) {
      return false;
    } else {
      throw new IOException("Couldn't read a boolean");
    }
  }

  public static final void main(String[] var0) {
    InputStreamReader var1 = new InputStreamReader(System.in);
    Input var2 = new Input(var1);

    try {
      while (true) {
        String var3 = var2.readString();
        System.out.println("string {" + var3 + "}");
        int var4 = var2.readInt();
        System.out.println("integer {" + var4 + "}");
        double var5 = var2.readDouble();
        System.out.println("double {" + var5 + "}");
        boolean var7 = var2.readBoolean();
        System.out.println("boolean {" + var7 + "}");
      }
    } catch (Exception var8) {
      System.err.println(var8);
    }
  }
}
