package com.mm.gui;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 6/26/25
 * <br>Time: 3:45 AM
 * <br>@author Miguel Muñoz</p>
 */
@SuppressWarnings({"unused", "unchecked"})
public class JXTabbedPane<T extends Component> extends JTabbedPane {
  private final Class<T> componentType;
  public JXTabbedPane(Class<T> componentType) {
    super();
    this.componentType = componentType;
  }

  public JXTabbedPane(int tabPlacement, Class<T> componentType) {
    super(tabPlacement);
    this.componentType = componentType;
  }

  public JXTabbedPane(int tabPlacement, int tabLayoutPolicy, Class<T> componentType) {
    super(tabPlacement, tabLayoutPolicy);
    this.componentType = componentType;
  }

  @Override
  public T getSelectedComponent() {
    return (T) super.getSelectedComponent();
  }

  @Override
  public void insertTab(String title, Icon icon, Component component, String tip, int index) {
    testForBadType(component);
    super.insertTab(title, icon, component, tip, index);
  }

  @Override
  public void addTab(String title, Icon icon, Component component, String tip) {
    testForBadType(component);
    super.addTab(title, icon, component, tip);
  }

  @Override
  public void addTab(String title, Icon icon, Component component) {
    testForBadType(component);
    super.addTab(title, icon, component);
  }

  @Override
  public void addTab(String title, Component component) {
    testForBadType(component);
    super.addTab(title, component);
  }

  @Override
  public void setSelectedComponent(Component c) {
    testForBadType(c);
    super.setSelectedComponent(c);
  }

  @Override
  public Component add(String title, Component component) {
    testForBadType(component);
    return super.add(title, component);
  }

  @Override
  public Component add(Component component, int index) {
    testForBadType(component);
    return super.add(component, index);
  }

  @Override
  public void add(Component component, Object constraints) {
    testForBadType(component);
    super.add(component, constraints);
  }

  @Override
  public void add(Component component, Object constraints, int index) {
    testForBadType(component);
    super.add(component, constraints, index);
  }

  @Override
  public void remove(Component component) {
    testForBadType(component);
    super.remove(component);
  }

  @Override
  public T getComponentAt(int index) {
    return (T) super.getComponentAt(index);
  }

  @Override
  public void setComponentAt(int index, Component component) {
    testForBadType(component);
    super.setComponentAt(index, component);
  }

  @Override
  public int indexOfComponent(Component component) {
    testForBadType(component);
    return super.indexOfComponent(component);
  }

  @Override
  public void setTabComponentAt(int index, Component component) {
    testForBadType(component);
    super.setTabComponentAt(index, component);
  }

  @Override
  public T getTabComponentAt(int index) {
    return (T) super.getTabComponentAt(index);
  }

  @Override
  public int indexOfTabComponent(Component tabComponent) {
    testForBadType(tabComponent);
    return super.indexOfTabComponent(tabComponent);
  }
  
  private void testForBadType(Component component) {
    if (!(componentType.isAssignableFrom(component.getClass()))) {
      throw new IllegalArgumentException(component.getClass().getSimpleName() + " is not assignable from " + componentType.getName());
    }
  }
}
