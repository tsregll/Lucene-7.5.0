package lucene.docValues;

import io.FileOperation;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.StringHelper;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Random;

/**
 * @author Lu Xugang
 * @date 2019-02-18 15:03
 */
public class SortedDocValuesTest {
  private Directory directory;

  {
    try {
      FileOperation.deleteFile("./data");
      directory = new MMapDirectory(Paths.get("./data"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Analyzer analyzer = new WhitespaceAnalyzer();
  private IndexWriterConfig conf = new IndexWriterConfig(analyzer);
  private IndexWriter indexWriter;

  public void doSearch() throws Exception {
    conf.setUseCompoundFile(false);
    indexWriter = new IndexWriter(directory, conf);

    String groupField = "superStart";
    Document doc ;
//    // 0
//      doc = new Document();
//    doc.add(new SortedDocValuesField(groupField, new BytesRef("aa")));
//    indexWriter.addDocument(doc);
//
//    // 1
//    doc = new Document();
//    doc.add(new SortedDocValuesField(groupField, new BytesRef("cc")));
//    indexWriter.addDocument(doc);
//
//    // 2
//    doc = new Document();
//    doc.add(new SortedDocValuesField(groupField, new BytesRef("bb")));
//    indexWriter.addDocument(doc);
//
//    // 3
//    doc = new Document();
//    doc.add(new SortedDocValuesField(groupField, new BytesRef("ff")));
//    indexWriter.addDocument(doc);
//
    // 4
    doc = new Document();
    doc.add(new SortedDocValuesField(groupField, new BytesRef("aa")));
    doc.add(new TextField("abc", "luxugang", Field.Store.YES));
    indexWriter.addDocument(doc);
//
//    // 5
    doc = new Document();
    doc.add(new TextField("abc", "liudehua", Field.Store.YES));
    doc.add(new SortedDocValuesField(groupField, new BytesRef("a")));
    indexWriter.addDocument(doc);

    int count = 0;
    while (count++ < 2090){
      doc = new Document();
      doc.add(new SortedDocValuesField(groupField, new BytesRef(getSamePrefixRandomValue("ab"))));
    doc.add(new TextField("abc", "value", Field.Store.YES));
      indexWriter.addDocument(doc);
    }

    indexWriter.commit();

    IndexReader reader = DirectoryReader.open(indexWriter);
    IndexSearcher searcher = new IndexSearcher(reader);


//    Sort sort = new Sort(new SortedNumericSortField("age", SortField.Type.LONG), new SortedNumericSortField("price", SortField.Type.LONG));
    Sort sort = new Sort(new SortField(groupField, SortField.Type.STRING));

    TopDocs docs = searcher.search(new MatchAllDocsQuery(), 3, sort);

    for (ScoreDoc scoreDoc: docs.scoreDocs){
      Document document = searcher.doc(scoreDoc.doc);
      System.out.println("name is "+ document.get("abc")+"");
    }

  }




  public static String getSamePrefixRandomValue(String prefix){
    String str="abcdefghijklmnopqrstuvwxyz";
    Random random=new Random();
    StringBuffer sb=new StringBuffer();
    int length = getLength();
    for(int i=0;i<length;i++){
      int number=random.nextInt(25);
      sb.append(prefix);
      sb.append(str.charAt(number));
    }
    return sb.toString();
  }

  public static int getLength(){
    Random random = new Random();
    int length = random.nextInt(5);
    if (length < 3){
      length = length + 3;
    }
    return length;
  }

  public static void main(String[] args) throws Exception{
//    BytesRef ref1 = new BytesRef("abcdef");
//    BytesRef ref2 = new BytesRef("abdefd");
//    int ssortKeyLength = StringHelper.sortKeyLength(ref1, ref2);
//    System.out.println(ssortKeyLength);
    SortedDocValuesTest SortedDocValuesTest = new SortedDocValuesTest();
    SortedDocValuesTest.doSearch();
  }
}