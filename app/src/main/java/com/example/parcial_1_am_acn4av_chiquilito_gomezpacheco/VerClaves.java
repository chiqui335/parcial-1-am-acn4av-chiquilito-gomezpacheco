package com.example.parcial_1_am_acn4av_chiquilito_gomezpacheco;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class VerClaves extends AppCompatActivity {

    TextView txtListadoClaves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_claves);

        txtListadoClaves = findViewById(R.id.txtListadoClaves);
        mostrarClaves();
    }

    private void mostrarClaves() {
        File file = new File(getFilesDir(), "claves.json");
        if (!file.exists()) {
            txtListadoClaves.setText("No hay claves guardadas.");
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            StringBuilder jsonBuilder = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                jsonBuilder.append(linea);
            }
            reader.close();

            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<Clave>>() {}.getType();
            List<Clave> listaClaves = gson.fromJson(jsonBuilder.toString(), tipoLista);

            StringBuilder resultado = new StringBuilder();
            for (Clave c : listaClaves) {
                resultado.append("🔐 Nombre: ").append(c.nombre)
                        .append("\n🔑 Clave: ").append(c.clave)
                        .append("\n📅 Fecha: ").append(c.fecha)
                        .append("\n\n");
            }

            txtListadoClaves.setText(resultado.toString());

        } catch (IOException e) {
            txtListadoClaves.setText("Error al leer el archivo.");
            e.printStackTrace();
        }
    }
}
