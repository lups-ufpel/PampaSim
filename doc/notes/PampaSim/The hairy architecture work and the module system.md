PampaSim is supposed to be extensible. How does it achieve that? And what do we mean by an extension exactly?

I've been reading [Ted Kaminski's blog](https://www.tedinski.com/) and had an aha! moment of sorts. We've been wrangling an expression problem this whole time, deciding how to write the module abstractions and interfaces.

PampaSim modules are in essence plugins, and that comes with the [standard fare of problems around pluggable systems](https://www.tedinski.com/2018/01/30/the-one-ring-problem-abstraction-and-power.html), where the power the plugins hold over the host system is way too high. We can restrict the powers of the plugins, to make the challenge of arbitrary combinations of plugins more tractable while also keeping the module implementations as decoupled as possible. We came up with the hierarchical simulation trees for that.

PampaSim has these notions of simulations, entities and events: simulations and entities together compose trees, and events are to be treated as hard data to be passed around between the different nodes of these trees.

Entities are objects in Ted's parlance, where extensions are new variants implementing the same interface in the form of the events that the entity consumes and produces.

(stopped writing here, TODO)

I should look into how Fabric, Forge and the like deal with mod intercomms...