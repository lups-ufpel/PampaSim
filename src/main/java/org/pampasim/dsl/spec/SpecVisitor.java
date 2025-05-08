package org.pampasim.dsl.spec;

import javafx.scene.paint.Color;
import org.antlr.v4.runtime.ParserRuleContext;
import org.pampasim.SimCore.EventType;
import org.pampasim.SimCore.PampaSimEvent;
import org.pampasim.SimCore.PampaSimEventID;
import org.pampasim.dsl.SpecFileBaseVisitor;
import org.pampasim.dsl.SpecFileParser;
import org.pampasim.SimResources.Process;

import java.util.ArrayList;
import java.util.Optional;

public class SpecVisitor extends SpecFileBaseVisitor<Spec> {
    private Spec spec = new Spec();
    @Override
    public Spec visitConfigCommand(SpecFileParser.ConfigCommandContext ctx) {
        switch (ctx.start.getType()) {
            case SpecFileParser.SCHEDULER_CMD: {
                var schInfo = ctx.schedulerInfo();
                Optional<Integer> quantum
                        = Optional.ofNullable(
                        (schInfo.quantum != null)?
                            Integer.parseInt(schInfo.quantum.getText())
                            : null
                        );
                spec.setSchedulerInfo( schInfo.className.getText(), quantum );
                }
                break;
            case SpecFileParser.PROCESSOR_CMD: {
                var cores = new ArrayList<Integer>();
                var pInfo = ctx.processorInfo();
                for (int bound = 0; bound < 256; bound++) { // magic number, just to keep bounded
                    if (pInfo == null) break;
                    var mips = Integer.parseInt(pInfo.mips.getText());
                    for (int i = 0; i < Integer.parseInt(pInfo.count.getText()); i++) {
                        cores.add(mips);
                    }
                    pInfo = pInfo.nextInfo;
                }
                spec.getProcessors().add(new Spec.ProcessorInfo(cores));
            } break;
            case SpecFileParser.PROCESSMANAGER_CMD:
                spec.setHasProcManager(true);
        }
        return this.spec;
    }
    @Override
    public Spec visitCommand(SpecFileParser.CommandContext ctx) {
        assert(ctx.getChild(0).getText().equalsIgnoreCase("proc"));
        int start    = Integer.parseInt(ctx.getChild(1).getChild(2*1 - 1).getText());
        int duration = Integer.parseInt(ctx.getChild(1).getChild(2*2 - 1).getText());
        int priority = Integer.parseInt(ctx.getChild(1).getChild(2*3 - 1).getText());
        Process p = new Process(priority, duration, start, spec.getPidAlloc().assignPid());
        try {
            String clr = ctx.getChild(1).getChild(2*4 - 1).getText();
            spec.getColorMap().put(p, Color.web(clr));
        } catch (Exception ignored) {}
        spec.addProcessArrival(p);
        return this.spec;
    }

    @Override
    public Spec visitSpecFile(SpecFileParser.SpecFileContext ctx) {
        for (var cmd : ctx.confCmds) {
            System.out.println(cmd);
            visitConfigCommand(cmd);
        }
        System.out.println("---");
        for (var cmd : ctx.cmds) {
            System.out.println(cmd);
            visitCommand(cmd);
            System.out.println("got proc " + cmd);
        }
        System.out.println("the jeezeless spec = " + spec);
        return this.spec;
    }
}
