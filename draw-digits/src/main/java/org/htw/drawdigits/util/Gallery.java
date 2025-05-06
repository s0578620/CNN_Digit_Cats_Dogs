package org.htw.drawdigits.util;

import java.util.LinkedList;
import java.util.List;

public class Gallery<T> {

  private final LinkedList<GalleryItem<T>> list;
  private GalleryItem<T> presentItem;
  private int presentIndex;

  public static <T> Gallery<T> of(String initName, T init) {
    return new Gallery<>(initName, init);
  }

  private Gallery(String initName, T init) {
    GalleryItem<T> e = new GalleryItem<>(initName, init);
    list = new LinkedList<>(List.of(e));
    presentItem = e;
    presentIndex = 0;
  }

  public void addItem(String name, T item) {
    GalleryItem<T> e = new GalleryItem<>(name, item);
    list.add(e);
    presentItem = e;
    presentIndex = list.size() - 1;
  }

  public boolean removePresent() {
    if (list.size() == 1) {
      return false;
    }
    list.remove(presentIndex);
    moveLeft();
    return true;
  }

  public T moveRight() {
    try {
      presentItem = list.get(++presentIndex);
    } catch (IndexOutOfBoundsException ignored) {
      presentIndex--;
    }
    return presentItem.item;
  }

  public T moveLeft() {
    try {
      presentItem = list.get(--presentIndex);
    } catch (IndexOutOfBoundsException ignored) {
      presentIndex++;
    }
    return presentItem.item;
  }

  public T getPresentItem() {
    return presentItem.item;
  }

  public String getPresentName() {
    return presentItem.name;
  }

  public int getPresentIndex() {
    return presentIndex + 1;
  }

  public int size() {
    return list.size();
  }

  private static class GalleryItem<I> {

    final private String name;
    final private I item;

    private GalleryItem(String name, I item) {
      this.name = name;
      this.item = item;
    }
  }
}