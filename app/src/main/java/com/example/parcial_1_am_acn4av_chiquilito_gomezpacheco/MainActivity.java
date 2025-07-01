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
import android.util.Log; // Necesario para Log.d y Log.e

import androidx.annotation.NonNull; //
import androidx.appcompat.app.AppCompatActivity;

// >>> INICIO DE IMPORTACIONES DE FIREBASE <<<
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
// >>> FIN DE IMPORTACIONES DE FIREBASE <<<

// Importaciones necesarias para la lógica
import java.security.SecureRandom;
import java.text.SimpleDateFormat; // Para formatear la fecha
import java.util.Date;             // Para obtener la fecha actual
import java.util.HashMap;          // Necesario para Map
import java.util.Locale;           // Para SimpleDateFormat
import java.util.Map;              // Necesario para Map


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
    Button btnCerrarSesion;
    private int longitudPassword = 12;

    // Declaraciones de Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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

        // Conexión y Listener de botón Cerrar Sesión
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth.signOut(); // Cierra la sesión de Firebase
                    Toast.makeText(MainActivity.this, "Sesión cerrada.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, InicioActivity.class);
                    startActivity(intent);
                    finish(); // Finalizar MainActivity
                }
            });
        }

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
                    nombre = "Sin nombre";
                }

                // Formatear la fecha
                String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                Clave nuevaClave = new Clave(nombre, password, fecha);

                // Llamar al método de guardar en Firestore
                guardarClaveEnFirestore(nuevaClave);
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Cierra la actividad actual
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
        boolean usarCaracteresEspeciales = checkEspeciales.isChecked();

        if (usarCaracteresEspeciales) {
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

    private void guardarClaveEnFirestore(Clave nuevaClave) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error: No hay usuario autenticado.", Toast.LENGTH_SHORT).show();
            Log.e("Firestore", "No hay usuario autenticado al intentar guardar clave.");
            return;
        }

        String userId = currentUser.getUid();

        // Guardar el objeto Clave
        db.collection("usuarios")
                .document(userId)
                .collection("claves")
                .add(nuevaClave)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity.this, "Contraseña guardada en la nube con éxito", Toast.LENGTH_SHORT).show();
                        Log.d("Firestore", "Documento añadido con ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error al guardar contraseña en la nube", Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Error al añadir documento", e);
                    }
                });
    }
}