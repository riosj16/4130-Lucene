/*
  This program illustrates term and Boolean queries

  Requires the path to an existing Lucene index
  through a command line argument
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class BasicSearch {

    static final int MAX_DOCS = 10;

    public static void main(String[] args){
        // did the user provide correct number of command line arguments?
        // if not, print message and exit
        if (args.length != 1){
            System.err.println("Number of command line arguments must be 1");
            System.err.println("You have provided " + args.length + " command line arguments");
            System.err.println("Incorrect usage. Program terminated");
            System.err.println("Correct usage: java BasicSearch <path-to-lucene-index>");
            System.exit(1);
        }

        // extract directory name where the Lucene index is stored
        String indexDirName = args[0];
        System.out.println("Lucene index directory: " + indexDirName);

        // term query
        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDirName)));
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();

            String field = "contents";
            Term term = new Term(field, "leaves");
            Query query = new TermQuery(term);
            TopDocs topDocs = searcher.search(query, MAX_DOCS);
            System.out.println("\nTotal matches: " + topDocs.totalHits);
            ScoreDoc[] resultSet = topDocs.scoreDocs;

            int resultSetSize = Math.min(MAX_DOCS, Math.toIntExact(topDocs.totalHits));
            System.out.println("\nResult set size (Term query): " + resultSetSize);

            for (int i = 0; i < resultSetSize; i++){
                System.out.println("Document = " + resultSet[i].doc + "\t" + " Score=" + resultSet[i].score);
            }

            reader.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }


        // Boolean query
        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDirName)));
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();

            String field = "contents";
            QueryParser qParser = new QueryParser(field, analyzer);

            // parse() method requires exception catch
            try{
                // alice AND rabbit, but NOT contemporary
                Query query = qParser.parse("+alice +rabbit -contemporary");
                TopDocs topDocs = searcher.search(query, MAX_DOCS);
                System.out.println("\nTotal matches: " + topDocs.totalHits);
                ScoreDoc[] resultSet = topDocs.scoreDocs;

                int resultSetSize = Math.min(MAX_DOCS, Math.toIntExact(topDocs.totalHits));
                System.out.println("\nResult set size (Boolean query - v1): " + resultSetSize);

                for (int i = 0; i < resultSetSize; i++){
                    System.out.println("Document = " + resultSet[i].doc + "\t" + " Score = " + resultSet[i].score);
                }

                // alice OR wonderland
                query = qParser.parse("+alice OR wonderland");
                topDocs = searcher.search(query, 10);
                System.out.println("\nTotal matches: " + topDocs.totalHits);
                resultSet = topDocs.scoreDocs;

                resultSetSize = Math.min(MAX_DOCS, Math.toIntExact(topDocs.totalHits));
                System.out.println("\nResult set size (Boolean query - v2): " + resultSetSize);

                for (int i = 0; i < resultSetSize; i++){
                    System.out.println("Document = " + resultSet[i].doc + "\t" + " Score=" + resultSet[i].score);
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}