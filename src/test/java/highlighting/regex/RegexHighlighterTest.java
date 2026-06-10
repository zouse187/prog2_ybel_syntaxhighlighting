package highlighting.regex;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RegexHighlighterTest {

  private final RegexHighlighter highlighter = new RegexHighlighter();

  // ohne Überlappung
  @Test
  void noOverlaps() {
    // given
    String text = "public class Test";

    // when
    var regions = highlighter.computeRegions(text);

    // then
    assertEquals(2, regions.size());
  }

  // Keyword innerhalb eines Kommentars
  @Test
  void keywordInsideComment() {
    // given
    String text = "// public";

    // when
    var regions = highlighter.computeRegions(text);

    // then
    assertEquals(1, regions.size());
    assertEquals(0, regions.get(0).start());
    assertEquals(9, regions.get(0).end());
  }

  // Javadoc‑Kommentar, der auch vom normalen Blockkommentar‑Token matchbar wäre
  @Test
  void javadocPreferredOverBlockComment() {
    // given
    String text =
        """
        /**
         * Test
         */
        """;

    // when
    var regions = highlighter.computeRegions(text);

    // then
    assertEquals(1, regions.size());
  }

  // aufeinanderfolgenden Regionen
  @Test
  void regionsNextToEachOther_doNotOverlap() {
    // given
    String text = "new// test";

    // when
    var regions = highlighter.computeRegions(text);

    // then
    assertEquals(2, regions.size());
    assertEquals(0, regions.get(0).start());
    assertEquals(3, regions.get(0).end());
    assertEquals(3, regions.get(1).start());
    assertEquals(10, regions.get(1).end());
  }

  // Leerstring bzw. Text ohne Matches.
  @Test
  void emptyString() {
    // given
    String text = "123";

    // when
    var regions = highlighter.computeRegions(text);

    // then
    assertTrue(regions.isEmpty());
  }
}
