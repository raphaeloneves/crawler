package br.com.raphaelneves.crawler;

public class CrawlerEntryPoint {

    /*
    * Ponto de lançamento do processo de crawler. Cria uma instância de CrawlerService para
    * executar a chamada HTTP e parsear o objeto de resposta
    * @param url - URL para conexão e consulta
    * */
    public void pesquisar(String url){
            CrawlerService crawlerService = new CrawlerService();
            crawlerService.crawl(url);
    }
}
