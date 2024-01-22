package org.swdc.note.core.aspect;

import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.swdc.dependency.EventEmitter;
import org.swdc.dependency.annotations.Aspect;
import org.swdc.dependency.annotations.Interceptor;
import org.swdc.dependency.event.AbstractEvent;
import org.swdc.dependency.event.Events;
import org.swdc.dependency.interceptor.AspectAt;
import org.swdc.dependency.interceptor.ProcessPoint;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.entities.CollectionArticle;
import org.swdc.note.core.entities.CollectionType;
import org.swdc.note.ui.events.RefreshEvent;
import org.swdc.note.ui.events.RefreshType;

@Interceptor
public class RefreshAspect implements EventEmitter {

    private Events events;

    @Inject
    private Logger logger;

    @Aspect(byNameRegex = "create[\\S]+",at = AspectAt.AROUND)
    public Object onCreate(ProcessPoint point) {
        try {
            Object result = point.process();
            this.dispatchRefreshEvents(point);
            return result;
        } catch (Throwable e) {
            logger.error("fail to process method: " + point.getMethod().getName(), e);
            return null;
        }
    }

    @Aspect(byNameRegex = "save[\\S]+",at = AspectAt.AROUND)
    public Object onUpdate(ProcessPoint point) {
        try {
            Object result = point.process();
            this.dispatchRefreshEvents(point);
            return result;
        } catch (Throwable e) {
            logger.error("fail to process method: " + point.getMethod().getName(), e);
            return null;
        }
    }

    //@Around(pattern = "org.swdc.note.core.service.[\\S]+Service.delete[\\S]+")
    @Aspect(byNameRegex = "delete[\\S]+",at = AspectAt.AROUND)
    public Object onDelete(ProcessPoint point) {
        try {
            Object result = point.process();
            this.dispatchRefreshEvents(point);
            return result;
        } catch (Throwable e) {
            logger.error("fail to process method: " + point.getMethod().getName(), e);
            return null;
        }
    }

    private void dispatchRefreshEvents(ProcessPoint point) {

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

        CollectionType collectionType = getParam(point,CollectionType.class);
        if (collectionType != null && collectionType.getId() != null) {
            if (name.contains("create")) {
                this.emit(new RefreshEvent(collectionType,this,RefreshType.CREATION));
            } else if (name.contains("save")) {
                this.emit(new RefreshEvent(collectionType,this,RefreshType.UPDATE));
            } else if (name.contains("delete")) {
                this.emit(new RefreshEvent(collectionType,this,RefreshType.DELETE));
            }
        }

        CollectionArticle collectionArticle = getParam(point,CollectionArticle.class);
        if (collectionArticle != null) {
            this.emit(new RefreshEvent(collectionArticle.getType(),this,RefreshType.UPDATE));
        }
    }

    private <T> T getParam(ProcessPoint point, Class<T> paramType) {
        T result = null;
        for (Object arg: point.getArgs()) {
            if (arg.getClass() == paramType) {
                result = (T) arg;
            }
        }
        return result;
    }

    @Override
    public <T extends AbstractEvent> void emit(T t) {
        events.dispatch(t);
    }

    @Override
    public void setEvents(Events events) {
        this.events = events;
    }
}
