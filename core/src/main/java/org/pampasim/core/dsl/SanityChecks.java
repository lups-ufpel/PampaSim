package org.pampasim.core.dsl;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;

// FIXME: This is a test. It should live in the test module.
public class SanityChecks {
    final static String simpleEntitySrc =
"""
events {
    transmitting nothing {
        CLOCK;
    }
}
NothingDoer {
    on CLOCK do {
        IDLE then "do nothing";
    }
}
""";
    public static void main(String[] args) {
        simpleEntity();
        theWholeThing();
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
        System.out.println(parser.getEventGroups());
        System.out.println(parser.getEntities());
    }
    public static void theWholeThing() {
        String fileName = "refactor.ent";
        CharStream stream = null;
        try {
            stream = CharStreams.fromFileName(fileName);
        } catch (IOException e) {
            System.err.println(fileName + " not found!");
            assert (false);
        }
        var lexer = new EntityDSLLexer(stream);
        var tokenStream = new CommonTokenStream(lexer);
        var parser = new EntityDSLParser(tokenStream);
        parser.setBuildParseTree(true);
        EntityDSLParser.DescriptionFileContext tree
                = parser.descriptionFile();
        var treeStr = tree.toStringTree();
        System.out.println(treeStr);
        System.out.println(parser.getEventGroups());
        System.out.println(parser.getEntities());
    }
}
