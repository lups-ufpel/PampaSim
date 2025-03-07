package org.pampasim;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import org.pampasim.dsl.EntityDSLLexer;
import org.pampasim.dsl.EntityDSLParser;

import java.io.IOException;

public class EntityGraphTool {
    public static void main(String[] args) {
        assert args.length > 1;
        String fileName = args[1];
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
        System.out.println(parser.getEvents());
        System.out.println(parser.getEntities());
    }
}
