package com.soze.klecs;

import com.soze.klecs.component.ComponentContainer;
import com.soze.klecs.node.Node;

import java.util.*;

public class TempMain {

  static List<Object> bh = new ArrayList<>();

  public static void main(String[] args) {
    ComponentContainer componentContainer = new ComponentContainer();

    componentContainer.addComponent(1, "String");
    componentContainer.addComponent(1, 5);
    componentContainer.addComponent(1, new Object());
    componentContainer.addComponent(1, new ArrayList<>());
    componentContainer.addComponent(1, new HashSet<>());
    componentContainer.addComponent(1, Byte.valueOf("5"));
    componentContainer.addComponent(1, Long.valueOf("5"));
    componentContainer.addComponent(1, new LinkedList<>());

    Node node = Node.of(
      String.class, Integer.class, Object.class, ArrayList.class,
      HashSet.class, Byte.class, Long.class, LinkedList.class
    );

    //1. get entire node
    int iterations = 5000000;
    long t0 = System.nanoTime();
    bh = new ArrayList<>(iterations);
    for(int i = 0; i < iterations; i++) {
      consume(componentContainer.getNodeComponents(1, node));
    }

    double time = (System.nanoTime() - t0) / 1e9;
    System.out.println("Took " + time + " s to get node with " + 8 + " elements " + iterations + " times.");

    //1. get components one by one
    t0 = System.nanoTime();
    bh = new ArrayList<>(iterations);
    for(int i = 0; i < iterations; i++) {
      consume(componentContainer.getComponent(1, String.class));
      consume(componentContainer.getComponent(1, Integer.class));
      consume(componentContainer.getComponent(1, Object.class));
      consume(componentContainer.getComponent(1, ArrayList.class));
      consume(componentContainer.getComponent(1, HashSet.class));
      consume(componentContainer.getComponent(1, Byte.class));
      consume(componentContainer.getComponent(1, Long.class));
      consume(componentContainer.getComponent(1, LinkedList.class));
    }

    time = (System.nanoTime() - t0) / 1e9;
    System.out.println("Took " + time + " s to get " + 8 + " components " + iterations + " times.");
  }

  private static void consume(Object obj) {
//    if(Math.random() > 0.5) {
//      bh.add(obj);
//    } else {
//      bh.add(obj);
//    }
  }

}
