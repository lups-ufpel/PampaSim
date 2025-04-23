# Dumping ground for qualms

## a814cc37
to create process arrival events, one must already have an instance of process, which in turn needs a PID allocated
that feels weird, having to allocate a PID before the rest of the system even knows of its existence
feels weird because the simulation internals are being poked and prodded even before we start the sim. we advance the
PID allocator, which is inside the simulation, all this during the setup phase(?)
i'd like to keep the invariant that processes are always in a sane state (and that state is easy to work with) but
this one seems hard to tackle without introducing some kind of process lifetime
it could be beneficial for other reasons too, having an idea of which stage a given process is at a glance

god all my DSLs would probably work better as S-expressions but in for a penny in for a pound ig

figured out a good way to do plugins with [classgraph](https://github.com/classgraph/classgraph)
it should be able to autodetect classes implementing some interface (like specific entities' interfaces)
just a matter of writing a good query