package org.pampasim.dsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.xpath.XPath;
import org.pampasim.dsl.metadata.*;
import org.pampasim.dsl.EntityDSLParser.*;

public class DSLVisitor extends EntityDSLBaseVisitor<CodeGenData> {
    @Override
    protected CodeGenData aggregateResult(CodeGenData aggregate, CodeGenData nextResult) {
        return aggregate.merge(nextResult);
    }

    @Override
    public CodeGenData visitEntity(EntityDSLParser.EntityContext ctx) {
        var name = ctx.ID().getText();
        var r = new CodeGenData();
        var e = new Entity();
        e.setName(name);
        r.entities.add(e);
        return r;
    }

    @Override
    public CodeGenData visitEventHandler(EntityDSLParser.EventHandlerContext ctx) {
        var eventName = ctx.getParent().getParent().getParent();
        return super.visitEventHandler(ctx);
    }

    @Override
    public CodeGenData visitHandlerTransition(EntityDSLParser.HandlerTransitionContext ctx) {
        return super.visitHandlerTransition(ctx);
    }

    @Override
    public CodeGenData visitTransition(EntityDSLParser.TransitionContext ctx) {
        return super.visitTransition(ctx);
    }
}
