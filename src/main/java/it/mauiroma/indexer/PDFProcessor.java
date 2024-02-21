package it.mauiroma.indexer;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import org.eclipse.microprofile.config.ConfigProvider;
import org.elasticsearch.client.RestClient;

import com.lowagie.text.exceptions.InvalidPdfException;
import com.lowagie.text.pdf.PdfReader;

import com.lowagie.text.pdf.parser.PdfTextExtractor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.converter.stream.InputStreamCache;

import javax.net.ssl.SSLContext;

@ApplicationScoped
public class PDFProcessor implements Processor {


    public void process(Exchange exchange){
        try {
            InputStreamCache isc = (InputStreamCache) exchange.getIn().getBody();
            String fileName = (String) exchange.getIn().getHeader("CamelFileName");
            String zipFileName = (String) exchange.getIn().getHeader("CamelFileNameConsumed");

            System.out.println("Camel Header ["+exchange.getIn().getHeaders()+"]");
            PdfReader reader = new PdfReader(isc.readAllBytes());

            int pages = reader.getNumberOfPages();
            StringBuilder text = new StringBuilder();

            PdfTextExtractor extractor = new PdfTextExtractor(reader);
            for (int page = 1; page <= pages; page++) {
                text.append(extractor.getTextFromPage(page));
            }
            reader.close();
            PdfFile pdfFile = new PdfFile(text.toString(),fileName);
            new ElasticClientWrapper().indexToElastic(pdfFile);
        }catch (InvalidPdfException invalidPdfEx){
            System.out.println("Not Valid PDF ["+ invalidPdfEx.getMessage()+"]");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
