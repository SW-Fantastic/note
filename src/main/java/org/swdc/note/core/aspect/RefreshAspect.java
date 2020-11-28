package org.swdc.note.core.aspect;

import org.swdc.fx.aop.Advisor;
import org.swdc.fx.aop.ExecutablePoint;
import org.swdc.fx.aop.anno.Around;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.ui.events.RefreshEvent;
import org.swdc.note.ui.events.RefreshType;

public class RefreshAspect extends Advisor {

    @Around(pattern = "org.swdc.note.core.service.[\\S]+Service.create[\\S]+")
    public Object onCreate(ExecutablePoint point) {
        try {
            Object result = point.process();
            this.dispatchRefreshEvents(point);
            return result;
        } catch (Exception e) {
            logger.error("fail to process method: " + point.getMethod().getName(), e);
            return null;
        }
    }

    @Around(pattern = "org.swdc.note.core.service.[\\S]+Service.delete[\\S]+")
    public Object onDelete(ExecutablePoint point) {
        try {
            Object result = point.process();
            this.dispatchRefreshEvents(point);
            return result;
        } catch (Exception e) {
            logger.error("fail to process method: " + point.getMethod().getName(), e);
            return null;
        }
    }

    private void dispatchRefreshEvents(ExecutablePoint point) {

        String name = point.getMethod().getName();
        Article article = this.getParam(point, Article.class);
        if (article != null && article.getId() != null ) {
            if (name.contains("create")) {
                this.emit(new RefreshEvent(article,this, RefreshType.CREATION));
            } else if (name.contains("save")) {
                this.emit(new RefreshEvent(article,this,RefreshType.UPDATE));
            } else if (name.contains("delete")){
                this.emit(new RefreshEvent(article,this,RefreshType.DELETE));
            }
        }

        ArticleType type = this.getParam(point,ArticleType.class);
        if (type != null && type.getId() != null) {
            if (name.contains("create")) {
                this.emit(new RefreshEvent(type,this,RefreshType.CREATION));
            } else if (name.contains("save")) {
                this.emit(new RefreshEvent(type,this,RefreshType.UPDATE));
            } else if (name.contains("delete")) {
                this.emit(new RefreshEvent(type,this,RefreshType.DELETE));
            }
        }
    }

    private <T> T getParam(ExecutablePoint point, Class<T> paramType) {
        T result = null;
        for (Object arg: point.getParams()) {
            if (arg.getClass() == paramType) {
                result = (T) arg;
            }
        }
        return result;
    }

}
