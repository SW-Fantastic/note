package org.swdc.note.app.util;

import com.overzealous.remark.Options;
import com.overzealous.remark.Remark;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.BrowserContextParams;
import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profiles.pegdown.Extensions;
import com.vladsch.flexmark.profiles.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.DataSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by lenovo on 2018/9/16.
 */
@Configuration
public class StaticBeans {

    private static DataHolder OPTIONS ;

    private static final BrowserContext renderContext = new BrowserContext(new BrowserContextParams("./configs/xRender"));

    static {
        OPTIONS = PegdownOptionsAdapter.flexmarkOptions(true,
                Extensions.ALL_WITH_OPTIONALS
        );
    }

    @Bean
    public Parser parser(){
        Parser parser = Parser.builder(OPTIONS).build();
        return parser;
    }

    @Bean
    public BrowserContext browserContext() {
        return renderContext;
    }

    @Bean
    public freemarker.template.Configuration markerConfig() {
        return new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_25);
    }

    @Bean
    public HtmlRenderer renderer(){
        return HtmlRenderer.builder(OPTIONS).build();
    }

    @Bean
    public Remark remark(){
        return new Remark(Options.markdown());
    }

}
