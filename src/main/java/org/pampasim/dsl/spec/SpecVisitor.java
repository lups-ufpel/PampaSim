package org.pampasim.dsl.spec;

import javafx.scene.paint.Color;
import org.pampasim.SimCore.PampaSimEvent;
import org.pampasim.dsl.SpecFileBaseVisitor;
import org.pampasim.dsl.SpecFileParser;
import org.pampasim.SimResources.Process;

public class SpecVisitor extends SpecFileBaseVisitor<Spec> {
    private Spec spec;
    public final class ConfigCommandVisitor extends SpecFileBaseVisitor<Spec> {
        @Override
        public Spec visitConfigCommand(SpecFileParser.ConfigCommandContext ctx) {
            assert(ctx.getChild(0).getText().equalsIgnoreCase("scheduler"));
            return new Spec(ctx.schedulerInfo().getText());
        }
    }
    public final class CommandVisitor extends SpecFileBaseVisitor<PampaSimEvent> {
        @Override
        public PampaSimEvent visitCommand(SpecFileParser.CommandContext ctx) {
            assert(ctx.getChild(0).getText().equalsIgnoreCase("proc"));
            int start    = Integer.parseInt(ctx.getChild(1).getChild(2*1 - 1).getText());
            int duration = Integer.parseInt(ctx.getChild(1).getChild(2*2 - 1).getText());
            int priority = Integer.parseInt(ctx.getChild(1).getChild(2*3 - 1).getText());
            Process p = new Process(priority, duration, start, spec.getPidAlloc().assignPid());
            try {
                String clr = ctx.getChild(1).getChild(2*4 - 1).getText();
                spec.getColorMap().put(p, Color.web(clr));
            } catch (Exception ignored) {}
            return spec.addProcessArrival(p);
        }
    }

    @Override
    public Spec visitSpecFile(SpecFileParser.SpecFileContext ctx) {
        var configCommandV = new ConfigCommandVisitor();
        var commandV = new CommandVisitor();
        for (var cmd : ctx.confCmds) {
            System.out.println(cmd);
            this.spec = configCommandV.visit(cmd);
        }
        System.out.println("---");
        for (var cmd : ctx.cmds) {
            System.out.println(cmd);
            commandV.visit(cmd);
            System.out.println("got proc " + cmd);
        }
        return spec;
    }
}
