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
    componentContainer.addComponent(1, new HashMap<>());
    componentContainer.addComponent(1, Short.valueOf("5"));

    Node node = Node.of(
        String.class, Integer.class, Object.class, ArrayList.class,
        HashSet.class, Byte.class, Long.class, LinkedList.class,
        HashMap.class, Short.class
    );

    Node smallerNode = Node.of(String.class, LinkedList.class, Long.class);

    int iterations = 50000;

    //1. get entire node
    int loops = 100;
    long t = 0;
    for (int i = 0; i < loops; i++) {
      t += testNode(componentContainer, node, iterations);
    }

    t = 0;
    for (int i = 0; i < loops; i++) {
      t += testNode(componentContainer, node, iterations);
    }

    System.out.println("Took " + (t / 1e9) + " s to get node with " + 10 + " elements " + iterations + " times.");

    //2. get components one by one
    for (int i = 0; i < loops; i++) {
      t+= testNotNode(componentContainer, node, iterations);
    }
    t = 0;
    for (int i = 0; i < loops; i++) {
      t += testNotNode(componentContainer, node, iterations);
    }

    System.out.println("Took " + (t / 1e9) + " s to get " + 10 + " elements one by one " + iterations + " times.");

    //3. get node but remove components sometimes
    for (int i = 0; i < loops; i++) {
      t+= testRemove(componentContainer, node, iterations);
    }
    t = 0;
    for (int i = 0; i < loops; i++) {
      t += testRemove(componentContainer, node, iterations);
    }

    System.out.println("Took " + (t / 1e9) + " s to get node with " + 10 + " elements " + iterations + " times (25% chance to remove one component).");

    //4. get entire node
    for (int i = 0; i < loops; i++) {
      t+= testNode(componentContainer, smallerNode, iterations);
    }
    t = 0;
    for (int i = 0; i < loops; i++) {
      t += testNode(componentContainer, smallerNode, iterations);
    }

    System.out.println("Took " + (t / 1e9) + " s to get node with " + 3 + " elements " + iterations + " times.");

    //5. get components one by one
    for (int i = 0; i < loops; i++) {
      t += testNotNode(componentContainer, smallerNode, iterations);
    }
    t = 0;
    for (int i = 0; i < loops; i++) {
      t += testNotNode(componentContainer, smallerNode, iterations);
    }

    System.out.println("Took " + (t / 1e9) + " s to get " + 3 + " elements one by one " + iterations + " times.");

    //6. get node but remove components sometimes
    for (int i = 0; i < loops; i++) {
      t += testRemove(componentContainer, smallerNode, iterations);
    }
    t = 0;
    for (int i = 0; i < loops; i++) {
      t += testRemove(componentContainer, smallerNode, iterations);
    }

    System.out.println("Took " + (t / 1e9) + " s to get node with " + 3 + " elements " + iterations + " times (25% chance to remove one component).");



  }

  private static long testNode(ComponentContainer componentContainer, Node node, int iterations) {
    long t0 = System.nanoTime();
    for (int i = 0; i < iterations; i++) {
      consume(componentContainer.getNodeComponents(1, node));
    }

    return System.nanoTime() - t0;
  }

  private static long testNotNode(ComponentContainer componentContainer, Node node, int iterations) {
    long t0 = System.nanoTime();
    for (int i = 0; i < iterations; i++) {
      node.getComponentClasses().forEach(clazz -> consume(componentContainer.getComponent(1, clazz)));
    }

    return System.nanoTime() - t0;
  }

  private static long testRemove(ComponentContainer componentContainer, Node node, int iterations) {
    long t0 = System.nanoTime();
    for (int i = 0; i < iterations; i++) {
      boolean mess = Math.random() < 2;
      if (mess) {
        componentContainer.removeComponent(1, String.class);
      }
      consume(componentContainer.getNodeComponents(1, node));
      if (mess) {
        componentContainer.addComponent(1, "5");
      }
    }

    return System.nanoTime() - t0;
  }

  private static void consume(Object obj) {
//    if(Math.random() > 0.5) {
//      bh.add(obj);
//    } else {
//      bh.add(obj);
//    }
  }

}
