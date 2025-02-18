# Objectives
  - accept textual scenario descriptions
  - simplify and fully specify the execution stages for the OS simulation
  - specify all the event flows for every simulation entity
    - scheduler
    - process manager
    - processor
    - network
    - storage
    - memory
    - io

# Draft
## Ideas
Entities are just FSMs that get driven by events happening *to* them (alternatively,
you could phrase it like them receiving an event).
If we make it so the actual work the entity does only happen upon it
receiving the globally broadcast `Clock` event, then the entity-specific events only
need to handle the transitions between states.

### Model quirks
Making every entity capable of broadcasting an arbitrary amount of events every clock cycle
requires a pretty well-defined event handling sequence, so we don't end up modelling our way into
a deeper hole than the one we started with. This abstraction's supposed to simplify the control
flow, after all.

As such, the stages of execution for a single `Clock` event:
1. Start
2. `Clock` event dispatch
3. every entity runs, in an arbitrary order
4. outbound events are collected into their respective entities outbound event queues
5. Event priorities are assigned
6. Existing resource locks are checked
7. Events are collected into a simulation queue respecting priorities
8. New resource locks are handed out, if applicable
9. All the simple transition events are applied by priority
10. Simulation constraints are validated (deadlocks, resource exhaustion)
11. End

#### On notation
I found myself writing an adhoc DSL for describing the maps below, so a small description
is warranted:
Each entities behavior can be described by a map from an input set
(event E, state S) to a set (action A, result-events R)
where
    E is any event at all,
    S is one of the states of the entity being described,
    A is either a new state S for this entity, or a description of what work gets performed (for the special `Clock` event)
    R is an [algebraic data type](https://en.wikipedia.org/wiki/Algebraic_data_type) describing the events that will be dispatched by the transition, if any.

Additionally:
  - `_` is the wildcard pattern for events and states, and is used to
explicitly handle all the events or states that are not matched by the other entries in the map.

  - `None` represents the absence of resulting events
  - `?` when used in the resulting events ADTs marks that term as optional (`X?` is the same as `(X | None)`)
  - `*` when used in the resulting events ADTs represents a whole family of events by name prefix, e.g. all Processor events would be `Processor*`
  - Identifiers for states are written in ALLCAPS, and in PascalCase for events.

So, for each entity, their map could be:
### Processor
- `Clock`
   - `IDLE` / does nothing / `None`
   - `BLOCKED` / waits for some event / `None`
   - `EXEC` / drives a process / `(Processor* | Memory* | IO*)?`
   - `CTXSWITCH` / changes to a different process / `ProcessorContextSwitch`
   - `ERROR` / fatal error! / `ProcessorError`
- `ProcessorExec`
  - `IDLE` / `CTXSWITCH`
  - `BLOCKED` / `CTXSWITCH`
  - `_` / `ERROR`

### Memory
- `Clock`
    - `IDLE` / does nothing / `None`
    - `ALLOC` / allocates a page for a given process / `MemoryAllocResult?`
    - `FREE` / frees a page from a given process / `MemoryFreeResult?`
    - `READ` / drives a page read operation / `MemoryReadResult?`
    - `WRITE` / drives a page write operation / `MemoryWriteResult?`
    - `ERROR` / fatal error! / `MemoryError`
- `MemoryAlloc`
    - `IDLE` / `ALLOC`
    - `_` / `ERROR`
- `MemoryFree`
    - `IDLE` / `FREE`
    - `_` / `ERROR`
- `MemoryRead`
  - `IDLE` / `READ`
  - `_` / `ERROR`
- `MemoryReadResult`
  - `READ` / `IDLE`
  - `_` / `ERROR`
- `MemoryWrite`
  - `IDLE` / `WRITE`
  - `_` / `ERROR`
- `MemoryWriteResult`
  - `WRITE` / `IDLE`
  - `_` / `ERROR`
