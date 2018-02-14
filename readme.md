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

Basic usage
-----

First, you need to create an Engine. 

The Engine holds a list of Systems and a collection of Entities in the Engine.
Systems are the classes where actual game logic takes place.
System can get all or a subset of entities and operate on them.

```java
Engine engine = new Engine();
```

Now, create your own class which extends from EntitySystem.
This example shows an EntitySystem created inline, keep in mind however that this is not a good practice.

```java
EntitySystem system = new EntitySystem() {
  @Override
  public void update(float delta) {
    List<Entity> entities = getEngine().getEntitiesForNode(node);
    //process them
  }
};

engine.addSystem(system)
```

Now you have an Engine with one system, but no entities. First step here is to create the entity.
Each Engine you create has a method getEntityFactory(), which you can use to create Entities.

```java
EntityFactory factory = new EntityFactory();
Entity entity = factory.createEntity();
//add your components
engine.addEntity(entity);
```
The entity will be added to the engine only after the engine's update() method ends.
Remember, you can add and remove components to/from an Entity at any point.

You can also remove the system by it's class.

```java
engine.removeSystem(MySystem.class);
```

Components are objects added to entities. They are usually data holders, but you can do with them
what you want. 

```java
entity.addComponent(new PositionComponent(5, 5, 5));
```

```java
entity.removeComponent(PositionComponent.class);
```


Node is a group of components. For example, you can create a
Movable node, create a MovableSystem and have a system which operates on all movable entities.

```java
Node collisionNode = Node.of(PositionComponent.class, MassComponent.class);
```

First of all, you can get all components belonging to a node from an entity.

```java
Map<Class<?>, Object> components = entity.getNodeComponents(node);
```
If the entity has all the components, it will return a populated map (this collection is ready-only).
If the entity is missing at least one of the components, it will return an empty map.


Roadmap
----

1. Entity
2. Engine
3. Node
4. Get node of components from Entity.
5. Networking built in