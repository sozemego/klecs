package com.soze.klecs.system;

/**
 * System deals with updating a node (but can be multiple nodes).
 */
public interface EntitySystem {

  /**
   * Given a time passed since the last update, returns whether this system should update
   * during this tick.
   */
  default public boolean shouldUpdate(float delta) {
    return true;
  }

  public void update(float delta);

  //methods to implement
  //specify node/nodes

}
