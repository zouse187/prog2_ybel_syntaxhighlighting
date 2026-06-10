package highlighting;

import highlighting.antlr.*;
import highlighting.core.SyntaxHighlighter;
import highlighting.presets.Texts;
import highlighting.regex.*;
import highlighting.ui.EditorUI;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.Scanner;

public class Main {

  public static void main(String... args) {
    // Phase I: RegexHighlighter
    SyntaxHighlighter regex = new RegexHighlighter();

    // Phase II: ScanningHighlighter
    SyntaxHighlighter scanning = new ScanningHighlighter();

    // Phase III: AntlrTokenCollector (tokenbasiert)
    SyntaxHighlighter antlrToken = new AntlrTokenCollector();

    // and go ...
    // EditorUI.show(Texts.START_TEXT, regex);
    // EditorUI.show(Texts.START_TEXT, scanning);
    // EditorUI.show(Texts.START_TEXT, antlrToken);

    // Pretty Printing
    String input1 =
        """
        class A {
        private int number;

        void m() {}
        }
        """;

      String input2 =
          """
          class A {
          void m() {
          if (x) {
          y;x;
          } else {return;}
          while(true) {}
          }
          }
          """;

      String input3 =
          """
          class A {
          void m() {
          if (x) {
          y;x;
          while (true) {
          if (y) {
          y;
          }
          }
          } else {return;}
          }
          }
          """;

    var charStream = CharStreams.fromString(input3);
    var lexer = new MiniJavaLexer(charStream);
    var tokens = new CommonTokenStream(lexer);
    var parser = new MiniJavaParser(tokens);

    MiniJavaParser.CompilationUnitContext tree = parser.compilationUnit();

    Scanner spaces = new Scanner(System.in);
    System.out.print("Indent width (z.B. 2, 4, 8): ");
    int indentWidth = spaces.nextInt();

    PrettyPrinterVisitor visitor = new PrettyPrinterVisitor(indentWidth); // z.B. 4 Spaces
    visitor.visit(tree);

    System.out.println("---- Pretty Printed ----");
    System.out.println(visitor.result());
  }
}
