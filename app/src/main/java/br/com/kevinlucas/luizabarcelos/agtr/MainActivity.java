package br.com.kevinlucas.luizabarcelos.agtr;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import br.com.kevinlucas.luizabarcelos.agtr.repositiory.EmployeeRepository;
import br.com.kevinlucas.luizabarcelos.agtr.repositiory.VisitantRepository;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

/**
 * Classe que realiza a leitura do QRCode que contém o nome do colaboradores ou dos visitantes.
 */

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1; // Código da câmera
    private ZXingScannerView scannerView; // Biblioteca para realizar a leitura
    private EmployeeRepository employeeRepository; // Referência do colaborador
    private VisitantRepository visitantRepository; // Referência do visitante

    // Método que controi a tela para realizar a leitura
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recuparação das referencias do layout
        setContentView(R.layout.activity_main);
        scannerView = new ZXingScannerView(this);
        scannerView = findViewById(R.id.z_xing_scanner);

        // Verifica qual a versão do SO do dispositivo e verifica as permissões
        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.M) {
            if (!checkPermission()) {
                requestPermission();
            }
        }
    }

    // Verifica se existe a permisão de uso da câmera
    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    // Solicita as permisões para uso da câmera
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    // Gerencia o estado do leitor de QRCode de acordo com os estados da atividade
    @Override
    public void onResume() {
        super.onResume();

        // Verifica a instantcio do leitor e inicia a camera
        if (scannerView == null) {
            scannerView = new ZXingScannerView(this);
            setContentView(scannerView);
            scannerView.setResultHandler(this);
            scannerView.startCamera();
        } else {
            scannerView.setResultHandler(this);
            scannerView.startCamera();
        }
    }

    // Para os recursos da câmera
    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    // Solicita as permisões necessárias para execução do sistema
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        Toast.makeText(getApplicationContext(), R.string.mensage_permission_denied, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.mensage_permission_denied, Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel(getString(R.string.mensage_permission_solicitation),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    // Exibe a mensagem da permissão
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton(R.string.message_ok, okListener)
                .setNegativeButton(R.string.message_cancel, null)
                .create()
                .show();
    }

    // Método do leitor que retorna o resultado da leitura realizada do QRCode
    @Override
    public void handleResult(Result result) {
        // Armazena o resultado obtido pelo leitor
        String myResult = result.getText();

        // Verifica o resultado obtido
        if (!myResult.startsWith("Visitante:")) {
            // Instancia uma colaborador caso o resutlado NÃO começe com "Visitante"
            employeeRepository = new EmployeeRepository();
            // Passa as informações necessárias para o metodo que salva as informações da refeição
            employeeRepository.saveAll(myResult, MainActivity.this);
        } else {
            // Instancia uma visitante caso o resutlado COMEÇE com "Visitante"
            visitantRepository = new VisitantRepository();
            // Passa as informações necessárias para o metodo que salva as informações da refeição
            visitantRepository.saveAll(myResult, MainActivity.this);
        }

        // Reinicia o leitor após a leitura das informações
        scannerView.resumeCameraPreview(MainActivity.this);
    }
}

