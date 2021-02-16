package br.com.kevinlucas.luizabarcelos.agtr.repositiory;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.kevinlucas.luizabarcelos.agtr.R;
import br.com.kevinlucas.luizabarcelos.agtr.database.FirebaseConf;
import br.com.kevinlucas.luizabarcelos.agtr.model.Visitant;

/**
 * Classe responsável por realizar o cadastro das refeições dos colaboradores no banco de dados.
 */

public class VisitantRepository {

    // Recursos necessários
    private Visitant visitant;  // referência do visitante
    private DatabaseReference databaseReference; // referência do banco de dados
    private MediaPlayer mediaPlayer; // referência do recurso de execução de aúdio.
    private Vibrator rr; // referência do recurso para vibrar o dispositivo
    private long milliseconds = 160; // tempo da vibração em millesegundos

    /* Método que verifica se as informações da refeição do colaborador já foi cadastrado no dia
    verificado, caso já exista este registro no banco de dados o sistema não irá grava-ló novamente,
    caso não exista o sistema grava a nova refeição */
    public void saveAll(String result, final Context context) {

        /* Instância o colaborador passando as informações a serem gravadas, o nome é um parâmetro
        a ser passado na execução do método recuperado na leitura do QRCODE */
        visitant = new Visitant(getId(result), getName(result), getDate(), getTime(), getLocal(result));

        // Referência ao nó da data em que a refeição está sendo cadastrada
        databaseReference = FirebaseConf.getDatabaseReference()
                .child("visitants")
                .child(visitant.getId())
                .child("lunches")
                .child(visitant.getDate().replace("/", ""));

        // Verifica se existe a refeição cadastrada nesta data
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /* Caso seja nulo ou seja NÃO exista a refeição cadastrada e executado o metodo que
                 cadastra a nova refeição */
                if (dataSnapshot.getValue() == null) {
                    // Metodo que realiza o cadastro das informações
                    saveLanch(context, visitant.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Método que grava os dados da refeição.
    private void saveLanch(Context context, String msg) {
        // Método grava as informações, construindo os nós no banco de dados representando em JSON
        databaseReference.getRoot()
                .child("visitants")
                .child(visitant.getId())
                .child("name")
                .setValue(visitant.getName());
        databaseReference.getRoot()
                .child("visitants")
                .child(visitant.getId())
                .child("local")
                .setValue(visitant.getLocal());
        databaseReference
                .getRoot()
                .child("visitants")
                .child(visitant.getId())
                .child("lunches")
                .child(getDate().replace("/", ""))
                .child("date")
                .setValue(visitant.getDate());
        databaseReference
                .getRoot()
                .child("visitants")
                .child(visitant.getId())
                .child("lunches")
                .child(getDate().replace("/", ""))
                .child("time")
                .setValue(visitant.getTime());

        // Exibe uma mesagem na tela informado o sucesso da operação
        Toast.makeText(context, "Visitante: " + msg, Toast.LENGTH_SHORT).show();

        // Executa uma notificação sonora como forma de feedback para o sucesso da operação
        mediaPlayer = MediaPlayer.create(context, R.raw.definite);
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }

        // Executa uma vibração para melhorar o feedback do sucesso da operação
        rr = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        rr.vibrate(milliseconds);
    }

    // Retorna a data do dispositivo em forma de String para serem armazenadas no banco de dados
    private String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dataInstance = Calendar.getInstance().getTime();
        String date = simpleDateFormat.format(dataInstance);
        return date;
    }

    // Retorna a hora do dispositivo em forma de String para serem armazenadas no banco de dados
    private String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date dataInstance = Calendar.getInstance().getTime();
        String time = simpleDateFormat.format(dataInstance);
        return time;
    }

    // Retorna o id do visitante trasformado utilizando um codificador que utiliza o nome como parâmetro
    private String getId(String name) {
        return Base64.encodeToString(name.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");
    }

    // Retorna o nome do visitante extraindo do resultado do leitor de QRCODE
    private String getName(String result) {
        return result.substring(result.indexOf(":") + 1, result.lastIndexOf(","));
    }

    // Retorna o local ou a empresa do visitante extraindo do resultado do leitor de QRCODE
    private String getLocal(String result) {
        return result.substring(result.indexOf(",") + 1, result.length());
    }
}
