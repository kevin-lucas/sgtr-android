package br.com.kevinlucas.luizabarcelos.agtr.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Classe responsável por retornar uma referência com banco de dados firebase google.
 */

public class FirebaseConf {

    public static DatabaseReference databaseReference;

    // Retorna uma referência do banco de dados
    public static DatabaseReference getDatabaseReference() {
        // Verifica se existe uma referência instânciada
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
        return databaseReference;
    }
}
