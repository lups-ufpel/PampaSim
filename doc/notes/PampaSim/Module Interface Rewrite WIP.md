Modules need to have a well defined life-cycle, so I propose a rough draft:

Module instances can be in one of these states at any given time:
- Invalid -> at least one module invariant is not upheld
- Uninitialized -> instance is not yet ready for use
- Unbound -> not associated with any simulation tree
- Bound -> associated with a valid simulation tree
- Stale binding -> the associated simulation tree was dropped

As of <commit-hash>, the `org.pampasim.PampaSimModule` interface from the `sim-datamodel` package defines these states and the module API which manages the transitions between these states.

# Life-cycle
- Modules start off Uninitialized;
- `PampaSimViewModel` loads the module which becomes Unbound;
- Module binding happens whenever a new simulation tree is constructed, like when `PampaSimViewModel` invokes the simulationFactory of its `SimulatedScenario` in response to a `Spec` mutation calling `resetToSpec()`;
- When a simulation tree is discarded, all of its bound modules are marked as having Stale bindings. Since we don't get a good callback for simulation tree discards, this transition is not timed all that well, happening well after the old tree is discarded: there is a list of "loaded module" instances stored within `PampaSimViewModel` which gets iterated over inside `setupModules()`, so there is a shared pool of module instances that are unbound from the old simulation when a suitable replacement simulation is constructed, and rebound to the new tree as soon as the Stale binding state is set. Be aware of this implementation quirk when handling the Bound and Stale binding states!

# Issues
- [ ] Statistics are not updated properly it would seem
- [ ] Entities need to be instantiated by the spec entity section loader, like the scheduler entity