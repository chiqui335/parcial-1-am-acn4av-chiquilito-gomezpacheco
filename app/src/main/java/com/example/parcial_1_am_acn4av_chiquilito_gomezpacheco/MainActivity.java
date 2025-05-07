package com.example.parcial_1_am_acn4av_chiquilito_gomezpacheco;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.SecureRandom;

public class MainActivity extends AppCompatActivity {
    TextView txtPassword;
    Button btnGenerar;
    Button btnCopiar;
    Button btnCancelar;
    Button btnToggleEspeciales;
    SeekBar seekBarLongitud;
    TextView txtLongitud;
    private int longitudPassword = 12;
    private boolean usarCaracteresEspeciales = true; // Control de caracteres especiales

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Iniciar los elementos del layout
        txtPassword = findViewById(R.id.txtPassword);
        btnGenerar = findViewById(R.id.btnGenerar);
        btnCopiar = findViewById(R.id.btnCopiar);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnToggleEspeciales = findViewById(R.id.btnToggleEspeciales);
        seekBarLongitud = findViewById(R.id.seekBarLongitud);
        txtLongitud = findViewById(R.id.txtLongitud);

        // SeekBar
        seekBarLongitud.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                longitudPassword = 8 + progress; // Rango 8-20 (8 + 0 a 8 + 12)
                txtLongitud.setText(longitudPassword + " caracteres");
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Botón de caracteres especiales
        btnToggleEspeciales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usarCaracteresEspeciales = !usarCaracteresEspeciales;
                btnToggleEspeciales.setText(usarCaracteresEspeciales ?
                        "Desactivar especiales" : "Activar especiales");
            }
        });

        // Generar contraseña
        btnGenerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = generarContrasenaSegura(longitudPassword);
                txtPassword.setText(password);
            }
        });

        // Copiar la contraseña al portapapeles
        btnCopiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = txtPassword.getText().toString();
                if (!password.isEmpty() && !password.equals("Aquí aparecerá la contraseña")) {
                    // Copiar la contraseña al portapapeles
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Contraseña", password);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(MainActivity.this, "Contraseña copiada al portapapeles", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Primero genera una contraseña", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cierra la actividad actual y regresa a la anterior
                finish();
            }
        });
    }

    private String generarContrasenaSegura(int longitud) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        if(usarCaracteresEspeciales) {
            chars += "!@#$%^&*";
        }

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < longitud; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }
}