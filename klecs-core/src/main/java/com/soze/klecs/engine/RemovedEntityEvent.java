package com.soze.klecs.engine;

import com.soze.klecs.entity.Entity;

public class RemovedEntityEvent extends EntityEvent {

  public RemovedEntityEvent(final Entity entity) {
    super(entity);
  }
}
