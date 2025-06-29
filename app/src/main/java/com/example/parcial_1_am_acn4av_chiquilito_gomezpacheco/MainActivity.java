package com.example.parcial_1_am_acn4av_chiquilito_gomezpacheco;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends AppCompatActivity {


    Button btnGuardar;
    EditText etNombreClave;
    TextView txtPassword;
    Button btnGenerar;
    Button btnCopiar;
    Button btnCancelar;
    CheckBox checkEspeciales;
    SeekBar seekBarLongitud;
    TextView txtLongitud;
    private int longitudPassword = 12;
    private boolean usarCaracteresEspeciales = true; // Control de caracteres especiales

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Iniciar los elementos del layout

        Button btnVerClaves = findViewById(R.id.btnVerClaves);

        btnGuardar = findViewById(R.id.btnGuardar);
        etNombreClave = findViewById(R.id.etNombreClave);

        txtPassword = findViewById(R.id.txtPassword);
        btnGenerar = findViewById(R.id.btnGenerar);
        btnCopiar = findViewById(R.id.btnCopiar);
        btnCancelar = findViewById(R.id.btnCancelar);
        checkEspeciales = findViewById(R.id.checkEspeciales);
        seekBarLongitud = findViewById(R.id.seekBarLongitud);
        txtLongitud = findViewById(R.id.txtLongitud);

        // SeekBar
        seekBarLongitud.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                longitudPassword = 8 + progress; // Rango 8-20 (8 + 0 a 8 + 12)
                txtLongitud.setText(longitudPassword + "caracteres");
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
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

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = txtPassword.getText().toString().trim();
                String nombre = etNombreClave.getText().toString().trim();

                if (password.isEmpty() || password.equals("Aquí aparecerá la contraseña")) {
                    Toast.makeText(MainActivity.this, "Primero genera una contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (nombre.isEmpty()) {
                    nombre = "default";
                }

                String fecha = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date());
                Clave nuevaClave = new Clave(nombre, password, fecha);
                guardarClave(nuevaClave);
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cierra la actividad actual y regresa a la anterior
                finish();
            }
        });

        btnVerClaves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VerClaves.class);
                startActivity(intent);
            }
        });
    }

    private String generarContrasenaSegura(int longitud) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        // Verificar si el CheckBox está marcado
        CheckBox checkEspeciales = findViewById(R.id.checkEspeciales);
        boolean usarCaracteresEspeciales = checkEspeciales.isChecked(); // obtener el estado del CheckBox

        // Agregar caracteres especiales si está tildado
        if (usarCaracteresEspeciales) {
            chars += "!@#$%^&*";
        }

        // Generación de la contraseña
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < longitud; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    private void guardarClave(Clave nuevaClave) {
        String fileName = "claves.json";
        File file = new File(getFilesDir(), fileName);
        List<Clave> listaClaves = new ArrayList<>();

        Gson gson = new Gson();

        // Leer archivo existente
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String linea;
                while ((linea = reader.readLine()) != null) {
                    sb.append(linea);
                }
                reader.close();

                Type tipoLista = new TypeToken<List<Clave>>() {}.getType();
                listaClaves = gson.fromJson(sb.toString(), tipoLista);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al leer el archivo", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Agregar nueva clave
        listaClaves.add(nuevaClave);

        // Escribir archivo actualizado
        try {
            String jsonActualizado = gson.toJson(listaClaves);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(jsonActualizado.getBytes());
            fos.close();

            Toast.makeText(this, "Contraseña guardada con éxito", Toast.LENGTH_SHORT).show();
            // Mostrar ruta si querés:
            // Toast.makeText(this, "Guardado en: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la contraseña", Toast.LENGTH_SHORT).show();
        }
    }


    private String leerClaves() {
        File file = new File(getFilesDir(), "claves.json");

        if (!file.exists()) {
            return "";
        }

        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                sb.append(linea);
            }
            reader.close();

            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<Clave>>() {}.getType();
            List<Clave> listaClaves = gson.fromJson(sb.toString(), tipoLista);

            StringBuilder resultado = new StringBuilder();
            for (Clave c : listaClaves) {
                resultado.append("🔐 Nombre: ").append(c.nombre)
                        .append("\n🔑 Clave: ").append(c.clave)
                        .append("\n📅 Fecha: ").append(c.fecha)
                        .append("\n\n");
            }
            return resultado.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return "Error al leer el archivo.";
        }
    }





}