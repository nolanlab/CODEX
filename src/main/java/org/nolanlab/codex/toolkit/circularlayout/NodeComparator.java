package org.nolanlab.codex.toolkit.circularlayout;

import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NodeComparator
  implements Comparator<Object>
{
  private Node[] collection;
  private String methodName;
  private String attribute;
  private boolean sortAsc;
  private Graph graph;
  private CompareType enumcompare;
  public static final int EQUAL = 0;
  public static final int LESS_THAN = -1;
  public static final int GREATER_THAN = 1;
  
  public static enum CompareType
  {
    NODEID,  METHOD,  ATTRIBUTE,  LAYOUTDATA;
    
    private CompareType() {}
  }
  
  public NodeComparator(Graph graph, Node[] collection, CompareType enumcompare, String field, boolean sortAsc)
  {
    this.graph = graph;
    this.collection = collection;
    this.enumcompare = enumcompare;
    switch (enumcompare)
    {
    case NODEID: 
      break;
    case LAYOUTDATA: 
    case ATTRIBUTE: 
      this.attribute = field;
      break;
    case METHOD: 
      this.methodName = buildMethod(field);
    }
    this.sortAsc = sortAsc;
  }
  
  public int compare(Object o1, Object o2)
  {
    int rv = 0;
    
    Object result1 = null;
    Object result2 = null;
    Node node1 = (Node)o1;
    Node node2 = (Node)o2;
    
    Class<?> c = null;
    try
    {
      switch (this.enumcompare)
      {
      case NODEID: 
        result1 = node1.getId();
        result2 = node2.getId();
        break;
      case ATTRIBUTE: 
        result1 = node1.getAttribute(this.attribute);
        result2 = node2.getAttribute(this.attribute);
        break;
      case LAYOUTDATA: 
        Object NodeLayoutData1 = node1.getLayoutData();
        Object NodeLayoutData2 = node2.getLayoutData();
        Field field1 = NodeLayoutData1.getClass().getField(this.attribute);
        result1 = field1.get(NodeLayoutData1);
        Field field2 = NodeLayoutData2.getClass().getField(this.attribute);
        result2 = field2.get(NodeLayoutData2);
        break;
      case METHOD: 
        Method method = this.graph.getClass().getMethod(this.methodName, new Class[] { Node.class });
        c = method.getReturnType();
        result1 = method.invoke(this.graph, new Object[] { o1 });
        result2 = method.invoke(this.graph, new Object[] { o2 });
      }
      if ((result1 == null) && (result2 == null)) {
        return 0;
      }
      if ((result1 != null) && (result2 == null)) {
        return -1;
      }
      if ((result1 == null) && (result2 != null)) {
        return 1;
      }
      if (this.enumcompare != CompareType.METHOD) {
        c = result1.getClass();
      }
      if (c.isAssignableFrom(Class.forName("java.util.Comparator")))
      {
        Comparator c1 = (Comparator)result1;
        Comparator c2 = (Comparator)result2;
        rv = c1.compare(c1, c2);
      }
      else if (Class.forName("java.lang.Comparable").isAssignableFrom(c))
      {
        Comparable c1 = (Comparable)result1;
        Comparable c2 = (Comparable)result2;
        rv = c1.compareTo(c2);
      }
      else if (c.isPrimitive())
      {
        long f1 = ((Number)result1).longValue();
        long f2 = ((Number)result2).longValue();
        if (f1 == f2) {
          rv = 0;
        } else if (f1 < f2) {
          rv = -1;
        } else if (f1 > f2) {
          rv = 1;
        }
      }
      else
      {
        throw new RuntimeException("NodeComparator does not currently support ''" + c.getName() + "''!");
      }
    }
    catch (Exception e)
    {
      Logger.getLogger(NodeComparator.class.getName()).log(Level.SEVERE, null, e);
    }
    return rv * getSortOrder();
  }
  
  private int getSortOrder()
  {
    return this.sortAsc ? 1 : -1;
  }
  
  private String buildMethod(String field)
  {
    return "get" + (field);
  }
}
