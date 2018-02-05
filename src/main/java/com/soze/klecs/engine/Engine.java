package com.soze.klecs.engine;

import com.soze.klecs.entity.EntityFactory;

/**
 * Represents an ECS engine, which handles updates to systems and stores entities.
 */
public class Engine {

  private final EntityFactory entityFactory = new EntityFactory(this);
  //METHODS TO IMPLEMENT
  //add entity
  //add system

  /**
   * Returns an EntityFactory for this engine. This method always returns the same instance
   * of the factory.
   */
  public EntityFactory getEntityFactory() {
    return entityFactory;
  }


}
