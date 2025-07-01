package com.example.parcial_1_am_acn4av_chiquilito_gomezpacheco;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast; // Para mensajes emergentes
import androidx.annotation.NonNull; // Para anotaciones de no nulo
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log; // Para depuración

// >>> Importaciones de Firebase Firestore <<<
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList; // Necesario para la lista de Claves
import java.util.List;    // Necesario para la lista de Claves

public class VerClaves extends AppCompatActivity {

    TextView txtListadoClaves;

    // Declaraciones de Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_claves);

        txtListadoClaves = findViewById(R.id.txtListadoClaves);

        // Inicialización de Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Cargar y mostrar las claves desde Firestore
        cargarClavesDesdeFirestore();
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
                                // Aquí es donde usamos el POJO Clave para mapear el documento
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
                                    // ✅ Usamos los getters públicos aquí
                                    resultado.append("🔐 Nombre: ").append(c.getNombre())
                                            .append("\n🔑 Clave: ").append(c.getClave())
                                            .append("\n📅 Fecha: ").append(c.getFecha())
                                            .append("\n\n");
                                }
                                txtListadoClaves.setText(resultado.toString());
                            }
                        } else {
                            // Manejo de errores si la carga falla
                            Toast.makeText(VerClaves.this, "Error al cargar claves: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Log.w("VerClaves", "Error al obtener documentos: ", task.getException());
                            txtListadoClaves.setText("Error al cargar las claves.");
                        }
                    }
                });
    }
}