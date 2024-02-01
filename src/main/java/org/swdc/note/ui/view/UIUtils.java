package org.swdc.note.ui.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.swdc.fx.view.Toast;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.entities.CollectionType;
import org.swdc.note.core.entities.TreeEntity;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UIUtils {

    private static Map<KeyCode,Integer> nativeKeyMap = new HashMap<>();

    private static Map<Integer, KeyCode> keyCodeNativeMap = new HashMap<>();

    public static MenuItem createMenuItem(String name, EventHandler<ActionEvent> handler, KeyCombination combination) {
        MenuItem menuItem = new MenuItem(name);
        menuItem.setOnAction(handler);
        if (combination != null) {
            menuItem.setAccelerator(combination);
        }
        return menuItem;
    }

    public static void notification(String content) {
        Toast.showMessage(content);
    }

    public static <T> T fxViewByView(Node parent, Class<T> clazz) {
        if (parent.getUserData() == null) {
            return null;
        }
        return (T)clazz.cast(parent.getUserData());
    }

    /**
     * 在TreeView的Item中搜索特定的TreeItem
     * @param typeNode Tree的根节点
     * @param type 搜索的目标对象
     * @param extractor 特征函数，用于从对象里面提取可供对比的属性，通常可直接返回对象的Id。
     * @return 包含此对象的TreeItem。
     */
    public static <T extends TreeEntity<T>> TreeItem<T> findTypeItem(TreeItem<T> typeNode, T type, Function<T,Object> extractor) {
        if (typeNode.getValue() != null && extractor.apply(typeNode.getValue()).equals(extractor.apply(type))) {
            return typeNode;
        }
        if (typeNode.getChildren().size() > 0) {
            for (TreeItem<T> item: typeNode.getChildren()) {
                if (extractor.apply(item.getValue()).equals(extractor.apply(type))) {
                    return item;
                } else if (item.getChildren().size() > 0){
                    TreeItem<T> nested = findTypeItem(item,type,extractor);
                    if (nested != null) {
                        return nested;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 将一个树形的数据结构创建为一个GUI的Tree对象。
     * @param type 树形数据的根节点
     * @return 创建完毕的JavaFX-GUI树节点
     * @param <T> 树节点类型，必须继承TreeEntity
     */
    public static <T extends TreeEntity<T>> TreeItem<T> createTypeTree(T type) {
        TreeItem<T> item = new TreeItem<>(type);
        if (type.getChildren() != null && !type.getChildren().isEmpty()) {
            for (T subType: type.getChildren()) {
                TreeItem<T> subItem = createTypeTree(subType);
                item.getChildren().add(subItem);
            }
        }
        return item;
    }

    /**
     * 创建TypeTree，同时带着里面的Article
     * @param type
     * @return
     */
    public static TreeItem<Object> createTypeTreeExternal(ArticleType type) {
        TreeItem item = new TreeItem<>(type);
        Set<Article> articles = type.getArticles();
        for (Article article:articles) {
            item.getChildren().add(new TreeItem<>(article));
        }
        if (type.getChildren().size() > 0) {
            for (ArticleType subType: type.getChildren()) {
                TreeItem<ArticleType> subItem = createTypeTree(subType);
                item.getChildren().add(subItem);
            }
        }
        return item;
    }


    public static Integer[] stringToKeyCode(String data) {
        return Stream.of(data.split(","))
                .map(Integer::valueOf)
                .collect(Collectors.toList())
                .toArray(new Integer[0]);
    }

    public static String keyCodeToString(Integer[] codes) {
        return Stream.of(codes)
                .map(cd->cd + "")
                .reduce((cdA, cdB) -> cdA + "," + cdB)
                .orElse("");
    }

    private static void initKeyMaps() {
        if (nativeKeyMap.isEmpty()) {
            for (KeyCode theCode : KeyCode.values()) {
                String nativeField = "VC_" + theCode.getName().toUpperCase().replace(" ", "_");
                if (theCode.isDigitKey()) {
                    String name = theCode.getName().toUpperCase();
                    if (name.startsWith("NUMPAD")) {
                        KeyCode target = KeyCode.getKeyCode(
                                name.replace("NUMPAD ","")
                        );
                        nativeField = "VC_" + target.getName().toUpperCase().replace(" ", "_");
                    }
                } else if (theCode.isModifierKey()) {
                    if (theCode == KeyCode.ALT_GRAPH) {
                        nativeField = "VC_ALT";
                    } else if (theCode == KeyCode.COMMAND || theCode == KeyCode.META) {
                        nativeField = "VC_META";
                    }else {
                        nativeField = "VC_" + theCode.name();
                    }
                }

                try {
                    Field field = NativeKeyEvent.class.getField(nativeField);
                    int nativeKey = field.getInt(null);
                    nativeKeyMap.put(theCode,nativeKey);
                    keyCodeNativeMap.put(nativeKey,theCode);
                } catch (Exception e) {
                }
            }
        }
    }

    public static int getNativeKeyCode(KeyCode code) {
        if (nativeKeyMap.isEmpty() || keyCodeNativeMap.isEmpty()) {
            initKeyMaps();
        }
        return nativeKeyMap.get(code);
    }

    public static KeyCode getKeyCodeFromNative(int nativeCode) {
        if (nativeKeyMap.isEmpty() || keyCodeNativeMap.isEmpty()) {
            initKeyMaps();
        }
        return keyCodeNativeMap.get(nativeCode);
    }


}
