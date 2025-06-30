package com.neptunedreams.refBuilder;

import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.function.Function;

import com.neptunedreams.refBuilder.exception.MismatchException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 6/28/25
 * <br>Time: 12:14 AM
 * <br>@author Miguel Muñoz</p>
 */
public class ParseTests {
  
  @Test
  void testParseRefOpenNoName() {
    String input = "< ref >";
    doSucceedTest(input, ReferenceParser::parseRefOpen, "< ref >");
//    ReferenceParser parser = new ReferenceParser(input);
//    parser.parseRefOpen();
    
//    assertEquals(0, parser.getWikiReferenceTestOnly().getName().length() );
//    final FreePushbackReader reader = parser.getReaderTestOnly();
//    assertEquals(input, reader.getStringSoFar());
//    assertEquals(-1, reader.read());
  }

  /**
   * Convert the parser.parseRefOpen, which return void, to a Function that returns the parsed name. This is because
   * calling the {@code parseRefOpen()} method generates a name, but the method doesn't return that name. 
   * @param parser The parser on which the {@code parseRefOpen} method will be called.
   * @return the value of the Name that was parsed
   */
  private  String doRefOpen(ReferenceParser parser) {
    parser.parseRefOpen();
    return parser.getWikiReferenceTestOnly().getName();
  }

  @Test
  void testParseRefOpenWithNameSucceed() {
    doSucceedTest("< ref name=\"theName\">", this::doRefOpen, "theName", "< ref name=\"theName\">");
  }

  @Test 
  void testParseRefOpenFail() {
    doFailureTest("<Xref name=\"theName\">---", ReferenceParser::parseRefOpen, "xref", "<Xref");
    doFailureTest("< ref Xname=\"theName\">---", ReferenceParser::parseRefOpen, "xname", "< ref Xname");
    doFailureTest("<ref name|=\"theName\">---", ReferenceParser::parseRefOpen, "|", "<ref name|");
    doFailureTest("<ref name=X\"theName\">---", ReferenceParser::parseRefOpen, "x", "<ref name=X");
    doFailureTest("<ref name=\"theName>---", ReferenceParser::parseRefOpen, "&gt;", "<ref name=\"theName>");
    doFailureTest("<ref name=theName>---", ReferenceParser::parseRefOpen, "thename", "<ref name=theName");
    doFailureTest("<ref name=\"theName\"|>---", ReferenceParser::parseRefOpen, "|", "<ref name=\"theName\"|");
  }
  
  @Test
  void testParseRefCloseSucceed() {
    doSucceedTest("</ref>|",    ReferenceParser::parseRefClose, "</ref>|");
    doSucceedTest("</ ref >|",  ReferenceParser::parseRefClose, "</ ref >|");
    doSucceedTest("< / ref >|", ReferenceParser::parseRefClose, "< / ref >|");
    doSucceedTest("</Ref>|",    ReferenceParser::parseRefClose, "</Ref>|");
    doSucceedTest("</REF>|",    ReferenceParser::parseRefClose, "</REF>|");
  }

  @Test
  void testParseRefCloseFail() {
    doFailureTest("X</ref>---", ReferenceParser::parseRefClose, "x", "X");
    doFailureTest("<X/ref>---", ReferenceParser::parseRefClose, "x", "<X");
    doFailureTest("</refX>---", ReferenceParser::parseRefClose, "x", "</refX");
    doFailureTest("=</ref>---", ReferenceParser::parseRefClose, "=", "=");
    doFailureTest("<=/ref>---", ReferenceParser::parseRefClose, "=", "<=");
    doFailureTest("</=ref>---", ReferenceParser::parseRefClose, "=", "</=");
    doFailureTest("</ref=>---", ReferenceParser::parseRefClose, "=", "</ref=");
  }

  @Test
  void testParseQuotedNameSucceed() {
    doSucceedTest("\"Name One\"", ReferenceParser::parseQuotedName, "Name One", "\"Name One\"");
    doSucceedTest("\" Name One \"", ReferenceParser::parseQuotedName, "Name One", "\" Name One \"");
  }
  
  @Test
  void testParseQuotedNameFail() {
    doFailureTest("Name One|", ReferenceParser::parseQuotedName, "name", "Name");
    doFailureTest("\"Name One|", ReferenceParser::parseQuotedName, "|", "\"Name One|");
    doFailureTest("\"|", ReferenceParser::parseQuotedName, "|", "\"|");
  }
  
  private String doParseRefData(ReferenceParser parser) {
    parser.parseRefData();
    return parser.getWikiReferenceTestOnly().toString();
  }
  
  @Test
  void testParseRefDataSucceed() {
    String input1 = "{{ cite book | title=Bored of the Rings | first =  National  |   last   =   Lampoon   }}";
    String input2 = "{{cite book|title=Bored of the Rings|first=National|last=Lampoon}}";
    String input3 = "{{CITE BOOK|TITLE=Bored of the Rings|FIRST=National|LAST=Lampoon}}";
    String expected = "<ref>{{ cite book | title = Bored of the Rings | first = National | last = Lampoon }}</ref>";
    doSucceedTest(input1, this::doParseRefData, expected, input1);
    doSucceedTest(input2, this::doParseRefData, expected, input2);
    doSucceedTest(input3, this::doParseRefData, expected, input3);
  }
  
  @Test
  void testParseRefDataFail() {
    String input1 = "{{ book | title=Bored of the Rings | first =  National  |   last   =   Lampoon   }}";
    String input2 = "{{ cite book | =Bored of the Rings | first =  National  |   last   =   Lampoon   }}";
    doFailureTest(input1, ReferenceParser::parseRefData, "book", "{{ book");
    doFailureTest(input2, ReferenceParser::parseRefData, "=", "{{ cite book | =");
  }

  private String doParse(ReferenceParser parser, String... extras) {
    List<WikiReference> wikiReferenceList = parser.parse();
    final WikiReference wikiReference = wikiReferenceList.get(0);
    // Simulate a user-edited field
    for (String extra : extras) {
      StringTokenizer tokenizer = new StringTokenizer(extra, "=");
      wikiReference.setKeyValuePair(tokenizer.nextToken(), tokenizer.nextToken());
    }
    return wikiReference.toString();
  }

  @Test
  void testParseSucceed() {
    String input1 = "<ref name=\"BOTR\">{{CITE book|title=Bored of the Rings|edition=first|website=There weren't any web sites back then!|first=National|last=Lampoon}}</ref>";
    String expected1 = "<ref name=\"BOTR\">{{ cite book | title = Bored of the Rings | edition = first | website = There weren't any web sites back then! | first = National | last = Lampoon | language = lv }}</ref>";
    String input2 = "<ref  name  =  \"BOTR\" > { {  cite  book   |  title  =   Bored of the Rings  | edition  =  first  |  website=There weren't any web sites back then! |  language  =  lv  |  first =  National  |   last   =   Lampoon   }}</ref>";
    String input3 = "<REF NAME=\"BOTR\">{{ CITE BOOK | TITLE=Bored of the Rings |EDITION=first|   WEBSITE = There weren't any web sites back then!|LANGUAGE=lv | FIRST =  National  |   LAST   =   Lampoon   }}</REF>";
    String expected = "<ref name=\"BOTR\">{{ cite book | title = Bored of the Rings | edition = first | website = There weren't any web sites back then! | language = lv | first = National | last = Lampoon }}</ref>";

    doSucceedTest(input1, (p) -> doParse(p, "language=lv"), expected1, input1);
    doSucceedTest(input2, this::doParse, expected, input2);
    doSucceedTest(input3, this::doParse, expected, input3);
  }

  /**
   * <p>Convert a {@literal Consumer<ReferenceParser>} to a Function<ReferenceParser, String> that returns an
   * empty String. This allows us to use the same method to test both cases where there is no return value and 
   * where a return value is expected.</p>
   * @param c The Consumer to convert.
   * @return A function that takes a ReferenceParser and returns a String.
   */
  private static Function<ReferenceParser, String> toFn(Consumer<ReferenceParser> c) {
    return (p) -> { c.accept(p); return ""; };
  }

  private static void doSucceedTest(String input, Consumer<ReferenceParser> f, String soFar) {
    doSucceedTest(input, toFn(f), "", soFar);
  }

  private static void doSucceedTest(String input, Function<ReferenceParser, String> c, String expected, String expectedSoFar) {
    ReferenceParser parser = new ReferenceParser(input);

    // Take substring to avoid using an interned name
    final String retrievedName = c.apply(parser);
    assertEquals(expected, retrievedName);
    if (expected.isEmpty()) {
      //noinspection ConstantValue
      assertEquals(0, expected.length());
    } else {
      //noinspection StringEquality
      assertNotSame(expected, retrievedName, String.format("notSame(<%s>, <%s> (== is %b", expected, retrievedName, expected == retrievedName)); // Make sure it's not interned.
    }
    final FreePushbackReader reader = parser.getReaderTestOnly();
    final String stringSoFar = reader.getStringSoFar();
    assertEquals(expectedSoFar, stringSoFar);
    assertNotSame(expectedSoFar, stringSoFar);
    assertEquals(-1, reader.read());
  }

  private static void doFailureTest(String input, Consumer<ReferenceParser> c, String badToken, String expectedStringSoFar) {
    ReferenceParser parser = new ReferenceParser(input);
    MismatchException ex = assertThrows(MismatchException.class, () -> c.accept(parser));
    System.out.printf("For Input of %s, Message: %s%n", input, ex.getMessage()); // NON-NLS
    assertThat(ex.getMessage(), containsString(badToken));
    FreePushbackReader reader = parser.getReaderTestOnly();
    assertEquals(expectedStringSoFar, reader.getStringSoFar().trim());
  }
}
  
    
