package com.example.meethbourhood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

public class Inicio2 extends AppCompatActivity {

    //TextView TextQR;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference BASE_DE_DATOS;

    ImageView foto_perfil;
    TextView uidPerfil, correoPerfil, nombresPerfil, apellidosPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio2);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Inicio");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //TextQR = findViewById(R.id.TextQR);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance(); //INSTANCIA
        BASE_DE_DATOS = firebaseDatabase.getReference("USUARIOS_DE_APP");

        foto_perfil = findViewById(R.id.foto_perfil);
        uidPerfil = findViewById(R.id.uidPerfil);
        correoPerfil = findViewById(R.id.correoPerfil);
        nombresPerfil = findViewById(R.id.nombresPerfil);
        apellidosPerfil = findViewById(R.id.apellidosPerfil);
    }

    @Override
    protected void onStart() {
        verificacionInicioSesion();
        super.onStart();
    }

    public void verificacionInicioSesion(){
        if(firebaseUser != null){
            CargarDatos();
            Toast.makeText(Inicio2.this, "Se ha iniciado sesi??n", Toast.LENGTH_SHORT).show();
        } else{
            startActivity(new Intent(Inicio2.this, MainActivity.class));
            finish();
        }
    }

    public void CerrarS(View view){
        firebaseAuth.signOut();
        Toast.makeText(Inicio2.this, "Ha cerrado sesi??n", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(Inicio2.this, MainActivity.class));
    }

    //M??TODO PARA RECUPERAR LOS DATOS DE FIREBASE, DEL USUARIO ACTUAL
   public void CargarDatos(){
        Query query = BASE_DE_DATOS.orderByChild("correo").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //RECORREMOS LOS USUARIOS REGISTRADOS EN LA BASE DE DATOS, HASTA ENCONTRAR EL USUARIO ACTUAL
                for (DataSnapshot ds : snapshot.getChildren()){
                    //OBTENEMOS LOS VALORES
                    String uid = ""+ds.child("uid").getValue();
                    String correo = ""+ds.child("correo").getValue();
                    String nombre = ""+ds.child("nombre").getValue();
                    String apellido = ""+ds.child("apellido").getValue();
                    String imagen = ""+ds.child("imagen").getValue();

                    //COLOCAMOS LOS DATOS EN NUESTRAS VISTAS
                    uidPerfil.setText(uid);
                    correoPerfil.setText(correo);
                    nombresPerfil.setText(nombre);
                    apellidosPerfil.setText(apellido);

                    //DECLARAMOS UN TRY CATCH, PARA GESTIONAR NUESTRA FOTO DE PERFIL
                    try {
                        //SI EXISTE UNA IMAGEN EN LA BASE DE DATOS, DEL USUARIO ACTUAL
                        Picasso.get().load(imagen).placeholder(R.drawable.fotoperfil).into(foto_perfil);

                    }catch (Exception e){
                        //SI EL USUARIO NO CUENTA CON UNA IMAGEN EN LA BASE DE DATOS
                        Picasso.get().load(R.drawable.fotoperfil).into(foto_perfil);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }





    /*public void LectorQR(View view){
        if(view.getId() == R.id.qr){
            new IntentIntegrator(this).initiateScan();
        }
    }

    //Llamar m??todo result.

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Llamar a la informaci??n.
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        //Obtener la informaci??n en un String
        String datos = result.getContents();
        TextQR.setText(datos);
    }*/
}