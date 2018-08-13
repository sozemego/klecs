package com.soze.klecs.system;

import com.soze.klecs.engine.Engine;

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

  /**
   * Engine this system is in. It's up to the user to
   * pass the Engine to classes which implement this interface.
   *
   * @return
   */
  public Engine getEngine();

  public boolean isRenderer();

  //methods to implement
  //specify node/nodes

}
