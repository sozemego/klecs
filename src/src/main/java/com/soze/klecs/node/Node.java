package com.soze.klecs.node;

import java.util.*;

/**
 * Node is a family of components.
 * The nodes are user defined groups of components.
 * They are not neccessary to have a working ECS, but they help modularize code.
 */
public class Node {

  /**
   * Classes which comprise this node.
   * This collection should be unmodifiable.
   */
  private final Set<Class<?>> componentClasses;

  private Node(final Collection<Class<?>> componentClasses) {
    this.componentClasses = Collections.unmodifiableSet(new HashSet<>(componentClasses));
  }

  /**
   * Returns an unmodifiable set of the component classes.
   * Keep in mind that this set will throw an exception if
   * user tries to modify it.
   */
  public Set<Class<?>> getComponentClasses() {
    return componentClasses;
  }

  //STATIC METHODS

  public static Node of(Collection<Class<?>> classes) {
    return new Node(checkNulls(classes));
  }

  private static Collection<Class<?>> checkNulls(Collection<Class<?>> classes) {
    classes.forEach(Objects::requireNonNull);
    return classes;
  }

}
