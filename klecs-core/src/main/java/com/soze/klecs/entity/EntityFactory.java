package com.soze.klecs.entity;

import com.soze.klecs.engine.ComponentContainer;
import com.soze.klecs.engine.Engine;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * To simplify Entity creation and enable greater control over entity creation (for the library),
 * entities should be created through this factory.
 * Each entity factory instance belongs to a {@link Engine}. Since this ECS can be used both client-side
 * and server-side, we need to be able to create many engines (for game rooms for example), each with their unique
 * infrastructure.
 */
public class EntityFactory {

  /**
   * Generates ids for new entities.
   */
  private final AtomicLong idGenerator = new AtomicLong(0);

  /**
   * This factory's engine.
   */
  private final Engine engine;

  /**
   * Container for components and their retrieval, shared among all entities created by this EngineFactory.
   */
  private final ComponentContainer componentContainer;

  public EntityFactory(final Engine engine, final ComponentContainer componentContainer) {
    this.engine = Objects.requireNonNull(engine);
    this.componentContainer = Objects.requireNonNull(componentContainer);
  }

  /**
   * This method will create an {@link Entity} and return it to you.
   * You need to manually insert this entity to the engine.
   */
  public Entity createEntity() {
    return new Entity(idGenerator.incrementAndGet(), componentContainer);
  }

  /**
   * This method will create an {@link Entity} and return it to you.
   * This entity will be added to the engine which spawned this EntityFactory instance.
   */
  public Entity createEntityAndAddToEngine() {
    Entity entity = createEntity();
    this.engine.addEntity(entity);
    return entity;
  }

}
