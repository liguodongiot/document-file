package com.sxt.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;

/**
 * Created by Administrator on 2016/1/16.
 */
@Controller
public class LuceneController {
    @Autowired
    private CreateIndex index;
    @RequestMapping("create")
    public String createIndex(){
        File file =new File(CreateIndex.indexDir);
        if(file.exists()){
            file.delete();
            file.mkdirs();
        }
        index.createIndex();
        return "create.jsp";
    }
}
