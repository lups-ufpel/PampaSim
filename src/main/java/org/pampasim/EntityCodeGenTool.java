package org.pampasim;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.pampasim.dsl.EntityDSLLexer;
import org.pampasim.dsl.EntityDSLParser;
import org.pampasim.dsl.metadata.Entity;
import org.pampasim.dsl.metadata.Event;
import org.pampasim.dsl.metadata.Handler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/// Generates a specialized EventManager for a given
/// description file, along with the Entities used within,
/// using accompanying EntityImpl classes
public class EntityCodeGenTool {
    public record GlobalSections (String eventElements) {};
    public record EntitySections(GlobalSections globals, String stateElements, String eventRegistration, String processEvent) {
        public static EntitySections of(String classCode, GlobalSections globals, Entity entity) {
            Matcher simulationNameRe = Pattern.compile("Simulation\\s+(\\w+)").matcher(classCode);
            var m = simulationNameRe.find();
            assert m;
            String simulationVarName = simulationNameRe.group(1);

            return new EntitySections(
                    globals,
                    generateStateElements(entity),
                    generateEventRegistrations(simulationVarName, entity),
                    generateProcessEvent(simulationVarName, entity)
            );
        }
        public String patchClass(String classCode) {
            String acc = classCode;
            acc = acc.replaceAll("//\\s*codegen\\s+events *\\R", globals.eventElements());
            acc = acc.replaceAll("//\\s*codegen\\s+states *\\R", stateElements);
            acc = acc.replaceAll("//\\s*codegen\\s+register\\s+handlers\\s*\\R", eventRegistration);
            acc = acc.replaceAll("//\\s*codegen\\s+processEvent\\s*\\R", processEvent);
            return acc;
        }
        public static String generateStateElements(Entity e) {
            StringBuilder acc = new StringBuilder();
            for (String state : e.allStateNames()) {
                acc.append(state).append(",\n");
            };
            return acc.toString();
        }
        public static String generateEventRegistrations(String simulationVarName, Entity e) {
            StringBuilder acc = new StringBuilder();
            for (Event event : e.allAcceptedEvents()) {
                acc.append(simulationVarName)
                        .append(".getEventManager().addEventHandler(")
                        .append("EventType.")
                        .append(event.name())
                        .append(");\n");
            };
            return acc.toString();
        }
        public static String generateProcessEvent(String simulationVarName, Entity e) {
            StringBuilder acc = new StringBuilder("""
                    @Override
                    public void processEvent(PampaSimEvent event) {
                    }
                    """);
            return acc.toString();
        }
    };

    protected static final String implCodePath = "src/main/java/org/pampasim/SimEntityImpls";
    protected static final String destCodePath = "src/main/java/org/pampasim/SimEntityGen";

    public static void main(String[] args) throws IOException {
        String fileName = args[0];
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
        System.out.println(parser.getEvents());
        System.out.println(parser.getEntities());

        StringBuilder eventElements = new StringBuilder();
        for (String evName : parser.getEvents().stream().map(Event::name).toList()) {
            eventElements.append(evName).append(",\n");
        }
        GlobalSections globals = new GlobalSections(eventElements.toString());

        for (Entity e : parser.getEntities().values()) {
            // FIXME: use the Path API
            FileInputStream srcFile = new FileInputStream(implCodePath + "/" + e.getName() + ".java");
            FileOutputStream dstFile = new FileOutputStream(destCodePath + "/" + e.getName() + ".java");
            System.out.println("Processing " + e.getName() + ": " + srcFile + " -> " + dstFile);
            String classCode = new String(srcFile.readAllBytes());
            System.out.println(classCode);
            EntitySections genSections = EntitySections.of(classCode, globals, e);
            dstFile.write(genSections.patchClass(classCode).getBytes(StandardCharsets.UTF_8));
        }
    }


}
