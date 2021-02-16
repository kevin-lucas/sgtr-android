package br.com.kevinlucas.luizabarcelos.agtr.model;

/**
 * Classe que modela os dados a serem salvos dos visitantes.
 */

public class Visitant {

    private String id; // identificador único do visitante
    private String name; // nome do visitante
    private String date; // data da refeição do visitante
    private String time; // hora da refeição do visitante
    private String local; // local ou empresa do visitante


    public Visitant(String id, String name, String date, String time, String local) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.local = local;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocal() {
        return local;
    }
}


