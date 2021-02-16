package br.com.kevinlucas.luizabarcelos.agtr.model;

/**
 * Classe que modela os dados a serem salvos dos colaboradores.
 */

public class Employee {

    private String id; // identificador único do colaborador
    private String name; // nome do colaborador
    private String date; // data da refeição do colaborador
    private String time; // hora da refeição do colaborador

    public Employee(String id, String name, String date, String time) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}

