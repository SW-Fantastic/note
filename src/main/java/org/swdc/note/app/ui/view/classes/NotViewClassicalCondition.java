package org.swdc.note.app.ui.view.classes;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.swdc.note.app.util.UIUtil;

/**
 * Created by lenovo on 2018/10/27.
 */
public class NotViewClassicalCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return !UIUtil.isClassical();
    }
}
