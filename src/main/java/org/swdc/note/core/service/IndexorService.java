package org.swdc.note.core.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSLockFactory;
import org.apache.lucene.store.NIOFSDirectory;
import org.slf4j.Logger;
import org.swdc.dependency.annotations.With;
import org.swdc.note.core.aspect.RefreshAspect;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Map;

@With(aspectBy = RefreshAspect.class)
public class IndexorService {

    private IndexReader reader = null;

    private IndexWriter writer = null;

    private FileSystem indexFs = null;

    @Inject
    private Logger logger;

    @PostConstruct
    public void initialize() {
        try{
            logger.info("lucene initializing..");
            File fileFs = new File("data/indexes/indexed.zip");
            indexFs = FileSystems.newFileSystem(fileFs.toPath(), Map.of("create", "true"));
            if(!DirectoryReader.indexExists(getIndexDir())) {
                IndexWriter writer = getLuceneWriter();
                writer.commit();
                writer.flush();
            }
            logger.info("index loaded.");
        } catch (Exception e){
            logger.error("fail to init index folder",e);
        }
    }

    private IndexWriter getLuceneWriter() throws IOException {
        if (writer != null) {
            return writer;
        } else {
            Directory directory = getIndexDir();
            Analyzer analyzer = new IKAnalyzer(true);
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            config.setCommitOnClose(true);
            this.writer = new IndexWriter(directory,config);
            return writer;
        }
    }

    public void createIndex(Article article, ArticleContent content) {
        try {
            Document document = findByArticleId(article.getId());
            if (document != null) {
                this.updateIndex(article,content);
                return;
            }
            IndexWriter writer = getLuceneWriter();
            document = new Document();

            TextField sourceField = new TextField("content",content.getSource(), Field.Store.NO);
            TextField titleField = new TextField("title",article.getTitle(), Field.Store.YES);
            StringField idField = new StringField("id", article.getId(), Field.Store.YES);
            StringField typeField = new StringField("typeId",article.getType().getId(), Field.Store.YES);

            document.add(idField);
            document.add(titleField);
            document.add(sourceField);
            document.add(typeField);

            writer.addDocument(document);
            writer.commit();
            writer.flush();
        } catch (Exception e) {
            logger.error("failed to write index",e);
        }
    }

    public void updateIndex(Article article, ArticleContent content) {
        try {
            IndexWriter writer = getLuceneWriter();
            Document document = findByArticleId(article.getId());
            if (document == null) {
                createIndex(article,content);
                return;
            }

            document.removeField("id");
            document.removeField("title");
            document.removeField("content");
            document.removeField("typeId");

            TextField sourceField = new TextField("content",content.getSource(), Field.Store.NO);
            TextField titleField = new TextField("title",article.getTitle(), Field.Store.YES);
            StringField idField = new StringField("id", article.getId(), Field.Store.YES);
            StringField typeField = new StringField("typeId",article.getType().getId(), Field.Store.YES);

            document.add(idField);
            document.add(titleField);
            document.add(sourceField);
            document.add(typeField);

            writer.updateDocument(new Term("id"),document);
            writer.commit();
            writer.flush();

        } catch (Exception e) {
            logger.error("failed to write document",e);
        }
    }

    public void removeIndex(String typeId) {
        try {
            IndexReader reader = getLuceneReader();
            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = new TermQuery(new Term("typeId",typeId));
            TopDocs docs = searcher.search(query,Integer.MAX_VALUE);
            if(docs.totalHits.value == 0) {
                return;
            }
            IndexWriter writer = getLuceneWriter();
            writer.deleteDocuments(query);
            writer.commit();
            writer.flush();
        } catch (Exception e) {
            logger.error("fail to remove index",e);
        }
    }

    public void removeIndex(Article article) {
        try {
            IndexReader reader = getLuceneReader();
            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = new TermQuery(new Term("id",article.getId().toString()));
            TopDocs docs = searcher.search(query,1);
            if(docs.totalHits.value == 0) {
                return;
            }
            IndexWriter writer = getLuceneWriter();
            writer.deleteDocuments(query);
            writer.commit();
            writer.flush();
        } catch (Exception e) {
            logger.error("fail to remove index",e);
        }
    }

    private Document findByArticleId(String id) {
        try {
            IndexReader reader = getLuceneReader();
            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = new TermQuery(new Term("id",id));
            TopDocs docs = searcher.search(query,1);
            if(docs.totalHits.value == 0) {
                return null;
            }
            return searcher.doc(docs.scoreDocs[0].doc);
        } catch (Exception e) {
            return null;
        }
    }


    private Directory getIndexDir() throws IOException {
        //return FSDirectory.open(Paths.get("data/index"));
        return new NIOFSDirectory(indexFs.getPath("lucene_index"), FSLockFactory.getDefault());
    }

    private IndexReader getLuceneReader() throws IOException {
        if (reader != null) {
            IndexReader reader = DirectoryReader.openIfChanged((DirectoryReader)this.reader);
            if (reader != null) {
                this.reader = reader;
            }
            return this.reader;
        } else {
            Directory directory = getIndexDir();
            reader = DirectoryReader.open(directory);
            return reader;
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            logger.info("saving index..");
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (indexFs != null) {
                indexFs.close();
            }
            logger.info("lucene has shutdown.");
        } catch (Exception e) {
            logger.error("fail to close index", e);
        }
    }

}
