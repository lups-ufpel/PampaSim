package org.pampasim.dsl;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.pampasim.dsl.*;

// FIXME: This is a test. It should live in the test module.
public class SanityChecks {
    final static String simpleEntitySrc =
"""
NothingDoer {
    on CLOCK {
        IDLE / do nothing / !;
    }
}
""";
    public static void main(String[] args) {
        simpleEntity();
    }
    public static void simpleEntity() {
        var lexer = new EntityDSLLexer(CharStreams.fromString(simpleEntitySrc));
        var tokenStream = new CommonTokenStream(lexer);
        var parser = new EntityDSLParser(tokenStream);
        parser.setBuildParseTree(true);
        EntityDSLParser.DescriptionFileContext tree
                = parser.descriptionFile();
        var treeStr = tree.toStringTree();
        System.out.println(treeStr);
    }
}
