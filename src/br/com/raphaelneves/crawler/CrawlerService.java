package br.com.raphaelneves.crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrawlerService {

    private Document documentoHtml;
    private Connection con;
    private String token = "";
    private Connection.Response response;
    private static final int PAGINA_DESEJADA = 1000;
    private int paginasVisitadas = 0;
    private Map<String, String> cookies;

    /*
    * Inicia a operação do serviço de crawler até que a página desejada seja localizada.
    * @param url - url do seed de conexão
    * */
    public void crawl(String url){
        boolean sucesso = false;
        try{
            con = Jsoup.connect(url);
            while(paginasVisitadas <= PAGINA_DESEJADA) {
                if(paginasVisitadas == 0){
                    response = con.execute();
                    atualizarCookies(response);
                    documentoHtml = response.parse();
                    response = con.cookies(cookies)
                            .data(recuperarTokenAutenticacao())
                            .method(Connection.Method.POST)
                            .execute();
                    documentoHtml = response.parse();
                    atualizarCookies(response);
                    paginasVisitadas++;
                }else{
                    localizarToken();
                    proximaPagina();
                    paginasVisitadas++;
                }
            }
        }catch(Exception e){
            throw new RuntimeException("Erro ao executar o crawler", e);
        }
    }

    /*
    * Resolve o desafio do captcha entre as requisições da página e chama o carregamento da
    * página seguinte
    * */
    private void proximaPagina() {
        resolverCaptcha();
        try {
            carregarProximaPagina();
        } catch (IOException ioe) {
            throw new RuntimeException("Erro ao executar o crawler", ioe);
        }
        System.out.println("Token da página " + paginasVisitadas + " é: " + token);
    }

    /*
    * Requisição do tipo POST para a página seguinte, garantindo a atualização dos dados
    * no redirecionamento da página seguinte.
    * */
    private void carregarProximaPagina() throws IOException {
        Elements forms = documentoHtml.getElementsByTag("form");
        FormElement form = (FormElement) forms.get(0);
        response = form.submit()
                .data(recuperarTokenAutenticacao())
                .followRedirects(true)
                .cookies(cookies)
                .method(Connection.Method.POST).execute();
        atualizarCookies(response);
        documentoHtml = response.parse();
    }

    /*
    * Atualiza os cookies da sessão
    * @param Connection.Response novaResponse
    * */
    private void atualizarCookies(Connection.Response novaResponse){
        cookies = novaResponse.cookies();
    }

    private void resolverCaptcha(){
        Elements ps = documentoHtml.getElementsByTag("p");
        List<Integer> operadores = new ArrayList<>();
        String simbolo = "";
        for (Element p : ps) {
            for (Element span : p.children()){
                if (span.hasClass("operador")) {
                    operadores.add(Integer.parseInt(span.text()));
                }
                if (span.hasClass("simbolo")) {
                    simbolo = span.text();
                }
            }
        }
        Captcha captcha = new Captcha(operadores, simbolo);
        Integer valorCaptcha = captcha.resolverCaptcha();
        documentoHtml.getElementsByClass("captcha").val(valorCaptcha.toString());
        Elements divs = documentoHtml.getElementsByTag("div");
        Element div = divs.get(1);
        Elements inputs = div.children();
        Element elCaptcha = inputs.get(0).val(valorCaptcha.toString());
    }

    /*
    * Localiza o token da página carregada
    * */
    public void localizarToken(){
        System.out.println("Pesquisando Token...");
        Elements inputs = documentoHtml.body().getElementsByTag("input");
        for(Element input : inputs){
            if(input.val().length() == 64){
                token = input.val();
                break;
            }
        }
    }

    /*
    * Pega o valor do campo authenticityToken, necessário para requerir nova página
    * por meio do método POST
    * @return Map<String, String>
    * */
    private Map<String, String> recuperarTokenAutenticacao(){
        Map<String, String> map = new HashMap<>();
        Elements inputs = documentoHtml.getElementsByTag("input");
        for(Element input : inputs){
            if(input.attr("name").equals("authenticityToken")){
                map.put("authenticityToken", input.val());
            }
        }
        return map;
    }
}
