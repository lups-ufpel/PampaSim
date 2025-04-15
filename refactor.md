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
~~Entities are just FSMs that get driven by events happening *to* them (alternatively,
you could phrase it like them receiving an event).~~

Entities are not always representable as FSMs! Some of them are, but only a select few.
New plan: entities are objects, in the classic "behavior and state" view, only with the added
complication that some of them actively query other entities for performing their tasks, and
that communication is currently done out-of-band of the events message system. Messy.

We can make it so the actual work the entities do only happens when they receive a globally
broadcast `Clock` event, that way a queue of events can be accumulated for each entity to
process all at once. This processing may, and usually does involve dispatching more events,
and those get queued for processing in the next `Clock` dispatch.

### Model quirks
Making every entity capable of broadcasting an arbitrary amount of events every clock cycle
requires a pretty well-defined event handling sequence, so we don't end up modelling our way into
a deeper hole than the one we started with. This abstraction's supposed to simplify the control
flow, after all.

As such, the stages of execution for a single `Clock` event:
1. Start
2. `Clock` event dispatch
3. every entity runs, in an arbitrary order
    Entities:
    1. Pop next event, if the queue is not empty
    2. Process the event, possibly dispatching more events
    3. Loop to step 1 until queue is empty
4. parallel to entity 
10. Simulation constraints are validated (deadlocks, resource exhaustion)
11. End