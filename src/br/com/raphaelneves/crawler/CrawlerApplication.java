package br.com.raphaelneves.crawler;

public class CrawlerApplication {

    public static void main(String... args){
        CrawlerEntryPoint crawlerEntryPoint = new CrawlerEntryPoint();
        crawlerEntryPoint.pesquisar("https://agsoft.herokuapp.com");
    }
}
