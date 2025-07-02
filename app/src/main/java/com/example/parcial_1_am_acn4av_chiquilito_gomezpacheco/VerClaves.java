package com.example.parcial_1_am_acn4av_chiquilito_gomezpacheco;

import android.os.Bundle;
import android.view.View;
import android.widget.Button; // <-- Importar Button
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class VerClaves extends AppCompatActivity {

    TextView txtListadoClaves;
    Button btnVolver; // <-- Declarar el botón aquí

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_claves); // Asumiendo que este es el layout con el logo, el TextView y el botón "Volver"

        txtListadoClaves = findViewById(R.id.txtListadoClaves);
        btnVolver = findViewById(R.id.btnVolver); // <-- Enlazar el botón aquí

        // Inicialización de Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Cargar y mostrar las claves desde Firestore
        cargarClavesDesdeFirestore();

        // >>> Lógica para el botón "Volver" <<<
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Esto cierra la Activity actual y regresa a la anterior
            }
        });
    }

    private void cargarClavesDesdeFirestore() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            txtListadoClaves.setText("No hay usuario autenticado.");
            Toast.makeText(this, "Inicia sesión para ver tus claves.", Toast.LENGTH_LONG).show();
            Log.e("VerClaves", "No hay usuario autenticado para cargar claves.");
            return;
        }

        String userId = currentUser.getUid();
        Log.d("VerClaves", "Cargando claves para el usuario: " + userId);

        db.collection("usuarios")
                .document(userId)
                .collection("claves")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Clave> listaClaves = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    Clave clave = document.toObject(Clave.class);
                                    listaClaves.add(clave);
                                    Log.d("VerClaves", document.getId() + " => " + document.getData());
                                } catch (Exception e) {
                                    Log.e("VerClaves", "Error mapeando documento a Clave: " + document.getId(), e);
                                    Toast.makeText(VerClaves.this, "Error procesando una clave.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            if (listaClaves.isEmpty()) {
                                txtListadoClaves.setText("No hay claves guardadas para este usuario.");
                            } else {
                                StringBuilder resultado = new StringBuilder();
                                for (Clave c : listaClaves) {
                                    resultado.append("🔐 Nombre: ").append(c.getNombre())
                                            .append("\n🔑 Clave: ").append(c.getClave())
                                            .append("\n📅 Fecha: ").append(c.getFecha())
                                            .append("\n\n");
                                }
                                txtListadoClaves.setText(resultado.toString());
                            }
                        } else {
                            Toast.makeText(VerClaves.this, "Error al cargar claves: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Log.w("VerClaves", "Error al obtener documentos: ", task.getException());
                            txtListadoClaves.setText("Error al cargar las claves.");
                        }
                    }
                });
    }

}