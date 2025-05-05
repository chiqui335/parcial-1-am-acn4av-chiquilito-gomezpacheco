package com.example.parcial_1_am_acn4av_chiquilito_gomezpacheco;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.SecureRandom;

public class MainActivity extends AppCompatActivity {
    TextView txtPassword;
    Button btnGenerar;
    Button btnCopiar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicia los elementos del layout
        txtPassword = findViewById(R.id.txtPassword);
        btnGenerar = findViewById(R.id.btnGenerar);
        btnCopiar = findViewById(R.id.btnCopiar);

        // Cuando se toca el botón "Generar", se genera una nueva contraseña
        btnGenerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = generarContrasenaSegura(12); // Generar contraseña
                txtPassword.setText(password); // Mostrar la contraseña generada
            }
        });

        // Se copia la contraseña al portapapeles
        btnCopiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = txtPassword.getText().toString();
                if (!password.isEmpty()) {
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
    }

    // Metodo que genera contraseña
    private String generarContrasenaSegura(int longitud) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < longitud; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString(); // Devuelve la contraseña generada
    }
}
