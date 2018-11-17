package org.swdc.note.app.service.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.ArticleType;
import org.swdc.note.app.event.ArticleListRefreshEvent;
import org.swdc.note.app.event.ResetEvent;
import org.swdc.note.app.event.TypeRefreshEvent;

/**
 * 拦截特定方法，在方法完成后发布事件
 *
 * 由于@Transaction也是AOP实现，切点在@AfterReturning，
 * 而事件发布必须在Transaction提交时候进行，因此，我将
 * 执行顺序@Order设置为2，即可以在Transaction完成后执行。
 */
@Component
@Aspect
@Order(2)
public class RefresherAspect {

    @Autowired
    private ApplicationContext context;

    /**
     * 拦截以Type结尾的方法，使类型被修改后视图会及时更新
     * @param type 文档分类
     */
    @AfterReturning("execution(* org.swdc.note.app.service.TypeService.*Type(..))&&args(type,..)")
    public void afterType(ArticleType type) {
        context.publishEvent(new TypeRefreshEvent(type));
    }

    @AfterReturning("execution(* org.swdc.note.app.service.Article*.del*(..))")
    public void resetOnDel(){
        context.publishEvent(new ResetEvent(Article.class));
    }

    /**
     * 拦截artle结尾的方法，文章修改后可以及时刷新列表
     * @param article 文档对象
     */
    @AfterReturning("execution(* org.swdc.note.app.service.ArticleService.*Article(..))&&args(article,..)")
    public void afterArtleModify(Article article){
        context.publishEvent(new ArticleListRefreshEvent(article.getType()));
    }

}
