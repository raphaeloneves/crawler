package br.com.raphaelneves.crawler;

import java.util.List;

public class Captcha {

    private List<Integer> operadores;
    private String simbolo;

    public Captcha(List<Integer> operadores, String simbolo) {
        this.operadores = operadores;
        this.simbolo = simbolo;
    }

    public Integer resolverCaptcha(){
        Integer valor = 0;
        switch (simbolo){
            case "+" :
                valor = operadores.get(0) + operadores.get(1);
                break;
            case "-" :
                valor = operadores.get(0) - operadores.get(1);
                break;
            case "*" :
                valor = operadores.get(0) * operadores.get(1);
                break;
            case "/" :
                valor = operadores.get(0) / operadores.get(1);
                break;
            default: break;
        }
        return valor;
    }
}
