package highlighting.presets;

import static org.junit.jupiter.api.Assertions.*;

import highlighting.regex.Token;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class MiniJavaTokensTest {

  // Strings
  @Test
  void string_matchesCorrectly() {
    // given
    var token =
        Token.of(Pattern.compile("\"([^\"\\\\]|\\\\.)*\""), MiniJavaColours.STRING_LITERAL_COLOUR);
    String text = "\"abc\" x \"def\"";

    // when
    var matches = token.test(text);

    // then
    assertEquals(2, matches.size());
    assertEquals(0, matches.get(0).start());
    assertEquals(5, matches.get(0).end());
    assertEquals(8, matches.get(1).start());
    assertEquals(13, matches.get(1).end());
  }

  // Characters
  @Test
  void character_matchesCorrectly() {
    // given
    var token =
        Token.of(Pattern.compile("'([^'\\\\]|\\\\.)'"), MiniJavaColours.CHAR_LITERAL_COLOUR);
    String text = "'a' '\\n'";

    // when
    var matches = token.test(text);

    // then
    assertEquals(2, matches.size());
    assertEquals(0, matches.get(0).start());
    assertEquals(3, matches.get(0).end());
    assertEquals(4, matches.get(1).start());
    assertEquals(8, matches.get(1).end());
  }

  // Keywords
  @Test
  void keyword_matchesOnlyWholeWords() {
    // given
    var token =
        Token.of(
            Pattern.compile("\\b(package|import|class|public|private|final|return|null|new)\\b"),
            MiniJavaColours.KEYWORD_COLOUR);
    String text = "public class Test { return null; } newValue";

    // when
    var matches = token.test(text);

    // then
    assertEquals(4, matches.size()); // public, class, return, null
  }

  // Annotationen
  @Test
  void annotation_detectedCorrectly() {
    // given
    var token =
        Token.of(Pattern.compile("@[A-Za-z_][A-Za-z0-9_-]*"), MiniJavaColours.ANNOTATION_COLOUR);
    String text = "   @Override @Inject";

    // when
    var matches = token.test(text);

    // then
    assertEquals(2, matches.size());
    assertEquals(3, matches.get(0).start()); // @Override
  }

  // Einzeilige Kommentare
  @Test
  void lineComment_matchesCorrectly() {
    // given
    var token = Token.of(Pattern.compile("//.*"), MiniJavaColours.LINE_COMMENT_COLOUR);
    String text = "int x = 0; // comment";

    // when
    var matches = token.test(text);

    // then
    assertEquals(1, matches.size());
    assertEquals(11, matches.get(0).start());
  }

  // Javadoc‑Kommentare
  @Test
  void javadocComment_matchesCorrectly() {
    // given
    var token =
        Token.of(Pattern.compile("/\\*\\*[\\s\\S]*?\\*/"), MiniJavaColours.JAVADOC_COMMENT_COLOUR);
    String text =
        """
        /**
         * Test
         */
        """;

    // when
    var matches = token.test(text);

    // then
    assertEquals(1, matches.size());
  }

  // Mehrzeilige Kommentare
  @Test
  void blockComment_matchesCorrectly() {
    // given
    var token =
        Token.of(Pattern.compile("/\\*[\\s\\S]*?\\*/"), MiniJavaColours.BLOCK_COMMENT_COLOUR);
    String text = "/* comment */";

    // when
    var matches = token.test(text);

    // then
    assertEquals(1, matches.size());
  }
}
