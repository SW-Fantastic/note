package org.swdc.note.app.service;

import javafx.scene.control.TreeItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swdc.note.app.entity.ArtleType;
import org.swdc.note.app.repository.ArtleTypeRepository;

import java.util.List;

/**
 * 分类服务，提供和分类相关的操作
 */
@Service
public class TypeService {

    @Autowired
    private ArtleTypeRepository typeRepository;

    @Transactional(readOnly = true)
    public TreeItem<ArtleType> getTypes(){
        List<ArtleType> types = typeRepository.getTopLevelType();
        TreeItem<ArtleType> root = new TreeItem<>();
        types.forEach(item->{
            TreeItem<ArtleType> typeItem = new TreeItem<>();
            typeItem.setValue(item);
            typeItem.setExpanded(false);
            childItem(typeItem);
            root.getChildren().add(typeItem);
        });
        return root;
    }

    /**
     * 构建树结构的递归方法
     * @param item 文档的树节点
     */
    private void childItem(TreeItem<ArtleType> item){
        if(item.getValue()==null){
            return;
        }
        ArtleType type = typeRepository.getOne(item.getValue().getId());
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
    public boolean addType(ArtleType type){
        ArtleType parentType = type.getParentType();
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

    /**
     * 删除分类
     * @param type 被删除分类
     * @param direct 是否强制删除
     * @return 成功或失败
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean delType(ArtleType type,boolean direct){
        type = typeRepository.getOne(type.getId());
        if(!direct && (type.getChildType() != null && type.getChildType().size() > 0
                || type.getArtles() != null && type.getArtles().size() > 0)){
            return false;
        }else{
            if(type.getParentType()!=null){
                ArtleType parent = type.getParentType();
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
    private boolean typeValid(ArtleType type){
        ArtleType parentType = type.getParentType();
        boolean repeated = false;
        if(parentType == null){
            return true;
        }
        // 读取持久化对象
        parentType = typeRepository.getOne(parentType.getId());
        for (ArtleType typeItem : parentType.getChildType()){
            if(typeItem.getName().equals(type.getName())){
                repeated = true;
            }
        }
        return !repeated;
    }

}
