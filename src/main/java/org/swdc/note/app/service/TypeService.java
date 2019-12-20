package org.swdc.note.app.service;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swdc.note.app.entity.ArticleType;
import org.swdc.note.app.repository.ArticleTypeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 分类服务，提供和分类相关的操作
 */
@Service
public class TypeService {

    @Autowired
    private ArticleTypeRepository typeRepository;

    @Transactional(readOnly = true)
    public TreeItem<ArticleType> getTypes(){
        List<ArticleType> types = typeRepository.getTopLevelType();
        TreeItem<ArticleType> root = new TreeItem<>();
        types.forEach(item->{
            TreeItem<ArticleType> typeItem = new TreeItem<>();
            typeItem.setValue(item);
            typeItem.setExpanded(false);
            childItem(typeItem);
            root.getChildren().add(typeItem);
        });
        return root;
    }

    public TreeItem<ArticleType> getExternalTypes(ArticleType articleType) {
        return externalChildItem(articleType);
    }

    private TreeItem<ArticleType> externalChildItem(ArticleType item) {
        TreeItem<ArticleType> result = new TreeItem<>();

        Set<ArticleType> type = item.getChildType();
        if (type != null && type.size() > 0) {
            List<TreeItem<ArticleType>> typeList = new ArrayList<>();
            for (ArticleType typeItem : type) {
                typeList.add(externalChildItem(typeItem));
            }
            result.setValue(item);
            result.getChildren().addAll(typeList);
            return result;
        }
        result.setValue(item);
        return result;
    }

    /**
     * 构建树结构的递归方法
     * @param item 文档的树节点
     */
    private void childItem(TreeItem<ArticleType> item){
        if(item.getValue()==null){
            return;
        }
        ArticleType type = typeRepository.getOne(item.getValue().getId());
        if(type.getChildType() != null ){
            type.getChildType().forEach(typeItem->item.getChildren().add(new TreeItem<>(typeItem)));
            item.getChildren().forEach(this::childItem);
        }
    }

    /**
     * 添加类型。
     * 注意这里。Transaction是在方法return之后才会提交，
     * 因此如果在方法内部发布事件，那么列表刷新后依然没
     * 有添加的新类型，因为此时尚未提交。
     *
     * 事件发布放在了Aspect里面进行
     * @param type 文档类型
     * @return 操作是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addType(ArticleType type){
        // 如果是已有类型就直接修改
        if(type.getId() != null){
            typeRepository.save(type);
            return true;
        }
        ArticleType parentType = type.getParentType();
        if(parentType != null){
            parentType = typeRepository.getOne(parentType.getId());
        }
        type.setParentType(parentType);
        boolean valid = typeValid(type);
        if(valid){
            typeRepository.save(type);
        }
        return valid;
    }

    @Transactional(rollbackFor = Exception.class)
    public ArticleType saveType(ArticleType type) {
        // 如果是已有类型就直接修改
        if(type.getId() != null){
            return typeRepository.save(type);
        }
        ArticleType parentType = type.getParentType();
        if(parentType != null){
            parentType = typeRepository.getOne(parentType.getId());
        }
        type.setParentType(parentType);
        boolean valid = typeValid(type);
        if(valid){
            return typeRepository.save(type);
        }
        return null;
    }

    /**
     * 删除分类
     * @param type 被删除分类
     * @param direct 是否强制删除
     * @return 成功或失败
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean delType(ArticleType type, boolean direct){
        type = typeRepository.getOne(type.getId());
        if(!direct && (type.getChildType() != null && type.getChildType().size() > 0
                || type.getArticles() != null && type.getArticles().size() > 0)){
            return false;
        }else{
            if(type.getParentType()!=null){
                ArticleType parent = type.getParentType();
                type.getChildType().remove(type);
                typeRepository.save(parent);
            }
            typeRepository.delete(type);
            return true;
        }
    }

    /**
     * 验证分类
     * 同一父分类下子分类不能重名
     * @param type 类型
     * @return 分类是否重复
     */
    @Transactional(readOnly = true)
    protected boolean typeValid(ArticleType type){
        ArticleType parentType = type.getParentType();
        boolean repeated = false;
        if(parentType == null){
            return true;
        }
        // 读取持久化对象
        parentType = typeRepository.getOne(parentType.getId());
        for (ArticleType typeItem : parentType.getChildType()){
            if(typeItem.getName().equals(type.getName())){
                repeated = true;
            }
        }
        return !repeated;
    }
}
