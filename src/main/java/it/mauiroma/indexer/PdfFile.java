package it.mauiroma.indexer;

public class PdfFile {
    private String body;
    private String title;

    public PdfFile() {
    }

    public PdfFile(String body, String title) {
        this.setBody(body);
        this.setTitle(title);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
