package com.sxt.lucene;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2016/1/16.
 */
public class HtmlBeanUtil {

    public static HtmlBean parseHtml(File file){

        try {
            Source sc = new Source(file);
            Element title = sc.getFirstElement(HTMLElementName.TITLE);
            String content = sc.getTextExtractor().toString();
            HtmlBean hb = new HtmlBean();
            hb.setContent(content);
            hb.setTitle(title.getTextExtractor().toString());
            String path = file.getAbsolutePath();
            hb.setUrl("http://"+path.substring(3));
            return hb;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
