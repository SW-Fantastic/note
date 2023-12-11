package org.swdc.note.ui.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.swdc.fx.view.Toast;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleType;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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

    public static TreeItem<ArticleType> findTypeItem(TreeItem<ArticleType> typeNode, ArticleType type) {
        if (typeNode.getValue() != null && typeNode.getValue().getId().equals(type.getId())) {
            return typeNode;
        }
        if (typeNode.getChildren().size() > 0) {
            for (TreeItem<ArticleType> item: typeNode.getChildren()) {
                if (item.getValue().getId().equals(type.getId())) {
                    return item;
                } else if (item.getChildren().size() > 0){
                    TreeItem<ArticleType> nested = findTypeItem(item,type);
                    if (nested != null) {
                        return nested;
                    }
                }
            }
        }
        return null;
    }

    public static TreeItem<ArticleType> createTypeTree(ArticleType type) {
        TreeItem<ArticleType> item = new TreeItem<>(type);
        if (type.getChildren() != null && type.getChildren().size() > 0) {
            for (ArticleType subType: type.getChildren()) {
                TreeItem<ArticleType> subItem = createTypeTree(subType);
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
                    } else {
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
