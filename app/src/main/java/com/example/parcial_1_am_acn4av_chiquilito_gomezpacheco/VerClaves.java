package com.example.parcial_1_am_acn4av_chiquilito_gomezpacheco;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class VerClaves extends AppCompatActivity {

    LinearLayout layoutClavesContainer;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_claves);

        layoutClavesContainer = findViewById(R.id.layoutClavesContainer);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        cargarClavesDesdeFirestore();

        Button btnVolverAtras = findViewById(R.id.btnVolverAtras);
        btnVolverAtras.setOnClickListener(v -> {
            finish(); // Vuelve a MainActivity
        });

    }

    private void cargarClavesDesdeFirestore() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Inicia sesión para ver tus claves.", Toast.LENGTH_LONG).show();
            return;
        }

        String userId = currentUser.getUid();
        db.collection("usuarios")
                .document(userId)
                .collection("claves")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        layoutClavesContainer.removeAllViews(); // Limpiar

                        List<Clave> listaClaves = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Clave clave = document.toObject(Clave.class);

                            String documentId = document.getId(); // Guarda el ID

                            listaClaves.add(clave);

                            // Mostrar la clave
                            TextView txt = new TextView(this);
                            txt.setText("🔐 Nombre: " + clave.getNombre()
                                    + "\n🔑 Clave: " + clave.getClave()
                                    + "\n📅 Fecha: " + clave.getFecha());
                            txt.setTextColor(Color.WHITE);
                            txt.setTextSize(16);
                            txt.setPadding(0, 0, 0, 8);

                            // Layout de botones
                            LinearLayout btnLayout = new LinearLayout(this);
                            btnLayout.setOrientation(LinearLayout.HORIZONTAL);
                            btnLayout.setWeightSum(3);

                            // Botón COPIAR
                            Button btnCopiar = new Button(this);
                            btnCopiar.setText("Copiar");
                            btnCopiar.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                            btnCopiar.setOnClickListener(v -> {
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("clave", clave.getClave());
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(this, "Clave copiada", Toast.LENGTH_SHORT).show();
                            });

                            // Botón EDITAR (a implementar)
                            Button btnEditar = new Button(this);
                            btnEditar.setText("Editar");
                            btnEditar.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                            btnEditar.setOnClickListener(v -> {
                                Intent intent = new Intent(this, EditarClaveActivity.class);
                                intent.putExtra("documentId", documentId);
                                intent.putExtra("nombre", clave.getNombre());
                                intent.putExtra("clave", clave.getClave());
                                startActivity(intent);
                            });


                            // Botón BORRAR
                            Button btnBorrar = new Button(this);
                            btnBorrar.setText("Borrar");
                            btnBorrar.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                            btnBorrar.setOnClickListener(v -> borrarClave(documentId));


                            // Agregar botones al layout
                            btnLayout.addView(btnCopiar);
                            btnLayout.addView(btnEditar);
                            btnLayout.addView(btnBorrar);

                            btnBorrar.setOnClickListener(v -> borrarClave(documentId));

                            // Agregar texto + botones al contenedor principal
                            layoutClavesContainer.addView(txt);
                            layoutClavesContainer.addView(btnLayout);
                        }

                        if (listaClaves.isEmpty()) {
                            TextView txtVacio = new TextView(this);
                            txtVacio.setText("No hay claves guardadas.");
                            txtVacio.setTextColor(Color.WHITE);
                            txtVacio.setTextSize(16);
                            layoutClavesContainer.addView(txtVacio);
                        }

                    } else {
                        Toast.makeText(this, "Error al cargar claves.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void borrarClave(String documentoId) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        db.collection("usuarios")
                .document(userId)
                .collection("claves")
                .document(documentoId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Clave eliminada correctamente", Toast.LENGTH_SHORT).show();
                    cargarClavesDesdeFirestore(); // recargar la lista
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al borrar la clave: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
