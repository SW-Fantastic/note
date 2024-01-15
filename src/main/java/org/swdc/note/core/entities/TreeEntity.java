package org.swdc.note.core.entities;

import java.util.List;

/**
 * 树结构接口。
 *
 * 所有树结构的Entity都需要实现本接口，以便于
 * 通过UIUtils生成GUI的树结构。
 *
 * @param <T>
 */
public interface TreeEntity<T> {

    List<T> getChildren();

}
