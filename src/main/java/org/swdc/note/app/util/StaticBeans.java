package org.swdc.note.app.util;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profiles.pegdown.Extensions;
import com.vladsch.flexmark.profiles.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.options.DataHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by lenovo on 2018/9/16.
 */
@Configuration
public class StaticBeans {

    private static final DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(
            true,
            Extensions.ALL
    );

    @Bean
    public Parser parser(){
        Parser parser = Parser.builder(OPTIONS).build();
        return parser;
    }

    @Bean
    public HtmlRenderer renderer(){
        return HtmlRenderer.builder(OPTIONS).build();
    }

}
