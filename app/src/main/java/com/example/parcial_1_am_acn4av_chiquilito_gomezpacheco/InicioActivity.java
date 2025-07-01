package com.example.parcial_1_am_acn4av_chiquilito_gomezpacheco;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Para ver mensajes en Logcat
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // ¡IMPORTANTE: Nueva importación!
import android.widget.TextView; // ¡IMPORTANTE: Nueva importación!
import android.widget.Toast; // Para mostrar mensajes emergentes al usuario

import androidx.annotation.NonNull; // Para anotaciones de no nulidad
import androidx.appcompat.app.AppCompatActivity;

// ¡IMPORTANTE: Nuevas importaciones de Firebase!
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InicioActivity extends AppCompatActivity {

    // Declarar variables para los elementos de UI (EditTexts, Button, TextView)
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button btnIngresar;
    private TextView textViewMessage; // Para mostrar mensajes de error/éxito al usuario

   //Declarar la instancia de FirebaseAuth)
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Inicializar la instancia de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Conectar las variables declaradas con los elementos del layout
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnIngresar = findViewById(R.id.btnIngresar);
        textViewMessage = findViewById(R.id.textViewMessage);

        // Ahora cuando se hace clic, intenta loguear al usuario
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el texto que el usuario ingresó en los campos
                String email = editTextEmail.getText().toString().trim(); // .trim() quita espacios en blanco al inicio/final
                String password = editTextPassword.getText().toString().trim();

                // Validaciones básicas de los campos antes de intentar el login
                if (email.isEmpty()) {
                    editTextEmail.setError("El email es requerido");
                    editTextEmail.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    editTextPassword.setError("La contraseña es requerida");
                    editTextPassword.requestFocus();
                    return;
                }
                //verificar pass largo
                if (password.length() < 6) {
                    editTextPassword.setError("La contraseña debe tener al menos 6 caracteres");
                    editTextPassword.requestFocus();
                    return;
                }

                // llamar al método para iniciar sesión con Firebase
                signInUser(email, password);
            }
        });

        // Botón separado para registrarse (no solo para login) (habilitar después)
        // Button btnRegister = findViewById(R.id.btnRegister);
        // if (btnRegister != null) {
        //     btnRegister.setOnClickListener(new View.OnClickListener() {
        //         @Override
        //         public void onClick(View v) {
        //             // llamar a createAccount(email, password) o abrir una nueva actividad de registro
        //             // createAccount(editTextEmail.getText().toString().trim(), editTextPassword.getText().toString().trim());
        //         }
        //     });
        // }
    }

    // Método principal para inicio de sesión con Firebase
    private void signInUser(String email, String password) {
        // mensaje temporal mientras se procesa la solicitud
        Toast.makeText(InicioActivity.this, "Iniciando sesión...", Toast.LENGTH_SHORT).show();
        textViewMessage.setText("Iniciando sesión...");
        textViewMessage.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Si el inicio de sesión es exitoso
                            Log.d("Firebase", "signInWithEmail:success"); // Mensaje para Logcat
                            FirebaseUser user = mAuth.getCurrentUser(); // Obtiene el usuario actual logueado

                            Toast.makeText(InicioActivity.this, "¡Bienvenido, " + user.getEmail() + "!",
                                    Toast.LENGTH_LONG).show(); //
                            textViewMessage.setText("Inicio de sesión exitoso.");

                            textViewMessage.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

                            // Navegar a la MainActivity
                            Intent intent = new Intent(InicioActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.w("Firebase", "signInWithEmail:failure", task.getException()); // Mensaje de error para Logcat

                            String errorMessage = "Error de autenticación.";
                            if (task.getException() != null) {

                                errorMessage += " " + task.getException().getMessage();
                            }
                            Toast.makeText(InicioActivity.this, errorMessage,
                                    Toast.LENGTH_LONG).show();
                            textViewMessage.setText(errorMessage);

                            textViewMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                            textViewMessage.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    // Opcional: Método para crear una nueva cuenta (si necesitas un flujo de registro aquí mismo)
    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // La cuenta se creó exitosamente
                            Log.d("Firebase", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(InicioActivity.this, "Cuenta creada exitosamente para " + user.getEmail(),
                                    Toast.LENGTH_LONG).show();
                            textViewMessage.setText("Cuenta creada exitosamente.");
                            textViewMessage.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

                        } else {
                            // Si falla la creación de la cuenta
                            Log.w("Firebase", "createUserWithEmail:failure", task.getException());
                            String errorMessage = "Fallo al crear cuenta.";
                            if (task.getException() != null) {
                                errorMessage += " " + task.getException().getMessage();
                            }
                            Toast.makeText(InicioActivity.this, errorMessage,
                                    Toast.LENGTH_LONG).show();
                            textViewMessage.setText(errorMessage);
                            textViewMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        }
                    }
                });
    }

    // 9. Método para verificar si el usuario ya está logueado cuando la actividad se inicia

    @Override
    public void onStart() {
        super.onStart();
        // Obtiene el usuario actualmente logueado. Va a ser null si nadie inició sesión.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

            Log.d("Firebase", "Usuario ya logueado: " + currentUser.getEmail());
            Intent intent = new Intent(InicioActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}