Klecs
---

Klecs is a Entity Component System library (like [Ashley](https://github.com/libgdx/ashley)).

Features
----

Basic implementation of Entity, which is just a thin, useful interface.

Node (family) of components.

The goal is to have a nice interface for working with entities, but also to support
systems which are handle rendering (with various frame rate locking/unlocking schemes).
A secondary goal is to have a networking system built-in.

Roadmap
----

1. Entity
2. Engine
3. Node
4. Get node of components from Entity.
5. Networking built in