package it.mauiroma.indexer;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.zipfile.ZipSplitter;

public class Routes extends RouteBuilder {


    @Override
    public void configure() throws Exception {
        from("file:/tmp/zipfile/?fileName=ElasticPDF.zip&noop=true")
                .split(new ZipSplitter())
                .streaming().process(new PDFProcessor())
                .end();
    }
}
