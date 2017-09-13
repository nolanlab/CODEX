/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akoya.codex.toolkit.circularlayout;

import org.gephi.graph.api.GraphModel;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutUI;
import org.openide.util.NbBundle;

import javax.swing.*;

public class CircleLayoutBuilder
  implements LayoutBuilder
{
  private CircleLayoutUI ui;
    GraphModel gm;
  
  public CircleLayoutBuilder(GraphModel gm)
  {
    this.gm = gm;
  }
  
  public String getName()
  {
    return NbBundle.getMessage(CircleLayoutBuilder.class, "CircleLayout.name");
  }
  
  @Override
  public Layout buildLayout()
  {
    return new CircleLayout(this, 500.0D, false);
  }
  
  public LayoutUI getUI()
  {
    return this.ui;
  }
  
  private static class CircleLayoutUI
    implements LayoutUI
  {
    public String getDescription()
    {
      return NbBundle.getMessage(CircleLayoutBuilder.class, "CircleLayout.description");
    }
    
    public Icon getIcon()
    {
      return null;
    }
    
    public JPanel getSimplePanel(Layout layout)
    {
      return null;
    }
    
    public int getQualityRank()
    {
      return -1;
    }
    
    public int getSpeedRank()
    {
      return -1;
    }
  }
}
