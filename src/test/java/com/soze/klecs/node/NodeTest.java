package com.soze.klecs.node;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class NodeTest {

  @Test
  public void nodeBuilderShouldReturnANode() {
    Set<Class<?>> classes = new HashSet<>();
    classes.add(String.class);
    classes.add(List.class);
    Node node = Node.of(classes);
    assertTrue(node != null);
  }

  @Test(expected = NullPointerException.class)
  public void nodeBuilderShouldNotAcceptNullInCollection() {
    Set<Class<?>> classes = new HashSet<>();
    classes.add(String.class);
    classes.add(null);
    Node.of(classes);
  }

}