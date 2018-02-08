package com.soze.klecs.entity;

import com.soze.klecs.engine.Engine;
import com.soze.klecs.node.Node;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EntityTest {

  private EntityFactory entityFactory;

  @Before
  public void setup() {
    entityFactory = new Engine().getEntityFactory();
  }


}