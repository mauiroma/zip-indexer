package it.mauiroma.indexer;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import org.eclipse.microprofile.config.ConfigProvider;
import org.elasticsearch.client.RestClient;

import javax.net.ssl.SSLContext;
import java.util.List;

public class ElasticClientWrapper {

    String elasticIndexName;
    String elasticEndPoint;
    String elasticApiKey;

    ElasticsearchClient esClient;

    public ElasticClientWrapper(){
        this.elasticIndexName = ConfigProvider.getConfig().getValue("elastic.index.name", String.class);
        this.elasticEndPoint = ConfigProvider.getConfig().getValue("elastic.endpoint", String.class);
        this.elasticApiKey = ConfigProvider.getConfig().getValue("elastic.api.key", String.class);
        try {
            final SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, (x509Certificates, s) -> true).build();
            RestClient restClient = RestClient
                    .builder(HttpHost.create(elasticEndPoint))
                    .setDefaultHeaders(new Header[]{
                            new BasicHeader("Authorization", "ApiKey " + elasticApiKey)
                    })
                    .setHttpClientConfigCallback(httpAsyncClientBuilder ->
                            httpAsyncClientBuilder.setSSLContext(sslContext))
                    .build();

            ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
            esClient = new ElasticsearchClient(transport);
        }catch (Exception ex){}
    }

    public void indexToElastic(PdfFile pdfFile){
        try {
            IndexResponse response = esClient.index(i -> i
                    .index(elasticIndexName)
                    .document(pdfFile)
            );
            System.out.println("Document Stored id["+response.id()+"] title ["+pdfFile.getTitle()+"]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void retrieveFromElastic(){
        try {
            SearchResponse<PdfFile> response = esClient.search(s -> s.index(elasticIndexName),PdfFile.class);
            List<Hit<PdfFile>> hits = response.hits().hits();
            for (Hit<PdfFile> hit: hits) {
                PdfFile pdfFile = hit.source();
                System.out.println("Document From Elastic ["+pdfFile.getTitle()+"]");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
