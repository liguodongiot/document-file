package com.sxt.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by Administrator on 2016/1/16.
 */
@Service
public class CreateIndex {
    public static final String indexDir="G:/index";
    public static final String dataDir="G:/www.bjsxt.com";
    @Test
    public void createIndex(){
        try {
            Directory dir = FSDirectory.open(new File(indexDir));
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9,analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            IndexWriter writer = new IndexWriter(dir,config);
            File file = new File(dataDir);
            Collection<File> files = FileUtils.listFilesAndDirs(file, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
            for(File f:files){
                HtmlBean htmlBean = HtmlBeanUtil.parseHtml(f);
                Document doc = new Document();
                doc.add(new StringField("title", htmlBean.getTitle(), Field.Store.YES));
                doc.add(new StringField("content", htmlBean.getContent(), Field.Store.YES));
                doc.add(new StringField("url", htmlBean.getUrl(), Field.Store.YES));
                writer.addDocument(doc);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
