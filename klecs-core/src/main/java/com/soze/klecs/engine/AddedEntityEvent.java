package com.soze.klecs.engine;

import com.soze.klecs.entity.Entity;

public class AddedEntityEvent extends EntityEvent {

  public AddedEntityEvent(final Entity entity) {
    super(entity);
  }

}
