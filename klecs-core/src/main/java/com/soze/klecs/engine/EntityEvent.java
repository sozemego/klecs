package com.soze.klecs.engine;

import com.soze.klecs.entity.Entity;

import java.util.Objects;

public class EntityEvent {

  private final Entity entity;

  public EntityEvent(final Entity entity) {
    this.entity = Objects.requireNonNull(entity);
  }

  public Entity getEntity() {
    return entity;
  }

}
