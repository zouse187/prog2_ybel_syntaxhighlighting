package highlighting.antlr;

import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;
import highlighting.presets.MiniJavaColours;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.*;

// TODO Phase III — AntlrTokenCollector (token-based syntax highlighting).

// This highlighter uses the ANTLR-generated MiniJavaLexer to turn the input text into a token
// stream. {@code collectMatches(String)} is the only method you need to implement: extract tokens
// of interest and map them to {@code HighlightRegions} using the colours from {@code
// MiniJavaColours}. Sorting, filtering of invalid regions, and conflict handling are performed by
// the base class {@code SyntaxHighlighter} via the template method {@code computeRegions(...)}.
public class AntlrTokenCollector extends SyntaxHighlighter {

  // TODO (Phase III — implement this method): Use the token stream produced by the ANTLR-generated
  // {@code MiniJavaLexer} to collect highlight regions.
  //
  // Requirements / hints:
  // - Iterate over the lexer tokens (typically via {@code CommonTokenStream}); ignore the EOF
  // token.
  // - For each token type that should be coloured (e.g., keywords, string/char literals, comments),
  // create a {@code HighlightRegion} with the corresponding colour from {@code MiniJavaColours}.
  // - Use {@code Token#getStartIndex()} and {@code Token#getStopIndex()} (inclusive) to compute
  // {@code [start, end)} ranges: {@code start = startIndex, end = stopIndex + 1}.
  // - Do not sort, merge, or resolve overlaps here; return all candidates as you find them.
  // Normalisation and conflict resolution are handled later by the template method.
  // - Annotation highlighting: colour '@' and the immediately following IDENTIFIER token (if
  // present).
  @Override
  public List<HighlightRegion> collectMatches(String text) {
    List<HighlightRegion> result = new ArrayList<>();

    var input = CharStreams.fromString(text);
    var lexer = new MiniJavaLexer(input);
    var tokens = new CommonTokenStream(lexer);

    tokens.fill();

    for (int i = 0; i < tokens.getTokens().size(); i++) {
      Token t = tokens.getTokens().get(i);

      if (t.getType() == Token.EOF) {
        continue;
      }

      int start = t.getStartIndex();
      int end = t.getStopIndex() + 1;

      // Annotationen: @ + IDENTIFIER
      if (t.getType() == MiniJavaLexer.AT) {
        result.add(new HighlightRegion(start, start + 1, MiniJavaColours.ANNOTATION_COLOUR));

        if (i + 1 < tokens.size()) {
          Token next = tokens.get(i + 1);
          if (next.getType() == MiniJavaLexer.IDENTIFIER) {
            int nStart = next.getStartIndex();
            int nEnd = next.getStopIndex() + 1;
            result.add(new HighlightRegion(nStart, nEnd, MiniJavaColours.ANNOTATION_COLOUR));
            i++; // wir haben das nächste Token verarbeitet
          }
        }
        continue;
      }

      // Normale Token einfärben
      Color colour = null;

      switch (t.getType()) {

        // KEYWORDS
        case MiniJavaLexer.CLASS:
        case MiniJavaLexer.PUBLIC:
        case MiniJavaLexer.PRIVATE:
        case MiniJavaLexer.PACKAGE:
        case MiniJavaLexer.IMPORT:
        case MiniJavaLexer.FINAL:
        case MiniJavaLexer.NULL:
        case MiniJavaLexer.NEW:
        case MiniJavaLexer.IF:
        case MiniJavaLexer.ELSE:
        case MiniJavaLexer.WHILE:
        case MiniJavaLexer.RETURN:
        case MiniJavaLexer.EXTENDS:
        case MiniJavaLexer.IMPLEMENTS:
          colour = MiniJavaColours.KEYWORD_COLOUR;
          break;

        // STRING / CHAR
        case MiniJavaLexer.STRING_LITERAL:
          colour = MiniJavaColours.STRING_LITERAL_COLOUR;
          break;

        case MiniJavaLexer.CHAR_LITERAL:
          colour = MiniJavaColours.CHAR_LITERAL_COLOUR;
          break;

        // KOMMENTARE
        case MiniJavaLexer.LINE_COMMENT:
          colour = MiniJavaColours.LINE_COMMENT_COLOUR;
          break;

        case MiniJavaLexer.BLOCK_COMMENT:
          colour = MiniJavaColours.BLOCK_COMMENT_COLOUR;
          break;

        case MiniJavaLexer.JAVADOC_COMMENT:
          colour = MiniJavaColours.JAVADOC_COMMENT_COLOUR;
          break;

        default:
          // alles andere nicht einfärben
      }

      if (colour != null) {
        result.add(new HighlightRegion(start, end, colour));
      }
    }

    return result;
  }
}
