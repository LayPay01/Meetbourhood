package com.example.meethbourhood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CambiarPassword extends AppCompatActivity {

    TextView MisCredencialesTXT, CorreoActualTXT, PassActualTXT, CorreoActual, PassActual;
    EditText ActualPassET, NuevoPassET;
    DatabaseReference USUARIOS_DE_APP;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_password);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Cambiar Contraseña");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        MisCredencialesTXT = findViewById(R.id.MisCredencialesTXT);
        CorreoActualTXT = findViewById(R.id.CorreoActualTXT);
        PassActualTXT = findViewById(R.id.PassActualTXT);
        CorreoActual = findViewById(R.id.CorreoActual);
        PassActual = findViewById(R.id.PassActual);
        ActualPassET = findViewById(R.id.ActualPassET);
        NuevoPassET = findViewById(R.id.NuevoPassET);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        USUARIOS_DE_APP = FirebaseDatabase.getInstance().getReference("USUARIOS_DE_APP");

        progressDialog = new ProgressDialog(CambiarPassword.this);

        //Creamos el evento para cambiar letra
        camioletra();

        //Consultamos el correo y la contraseña del usuario
        Query query = USUARIOS_DE_APP.orderByChild("correo").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){

                    //Obtenemos los valores
                    String correo = ""+ds.child("correo").getValue();
                    String pass = ""+ds.child("pass").getValue();

                    //Setemos los datos en los textview
                    CorreoActual.setText(correo);
                    PassActual.setText(pass);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //Creamos el evento para cambiar contraseña
        //cambia
    }

    private void camioletra(){
        /*FUENTE DE LETRA*/
        String ubicacion = "fuentes/sans_medio.ttf";
        Typeface Tf = Typeface.createFromAsset(CambiarPassword.this.getAssets(),ubicacion);
        /*FUENTE DE LETRA*/

        MisCredencialesTXT.setTypeface(Tf);
        CorreoActualTXT.setTypeface(Tf);
        PassActualTXT.setTypeface(Tf);
        CorreoActual.setTypeface(Tf);
        PassActual.setTypeface(Tf);
        ActualPassET.setTypeface(Tf);
        NuevoPassET.setTypeface(Tf);
    }


    public void cambia(View view){
        String PASS_ANTERIOR = ActualPassET.getText().toString().trim();
        String NUEVO_PASS = NuevoPassET.getText().toString().trim();

        //Condiciones
        if(TextUtils.isEmpty(PASS_ANTERIOR)){
            Toast.makeText(CambiarPassword.this, "El campo contraseña actual está vacío", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(NUEVO_PASS)){
            Toast.makeText(CambiarPassword.this, "El campo nueva contraseña está vacío", Toast.LENGTH_SHORT).show();
        }
        if(!NUEVO_PASS.equals("") && NUEVO_PASS.length() >= 6){
            //Se ejecuta el método para cambiar pass,, el cual recibe los dos parámetros
            Cambio_De_Password(PASS_ANTERIOR,NUEVO_PASS);
        }else{
            NuevoPassET.setError("La contraseña debe ser mayor de 6 digitos");
            NuevoPassET.setFocusable(true);
        }
    }

    //Método para cambiar la contraseña en la BD
    private void Cambio_De_Password(String pass_anterior, String nuevo_pass) {
        progressDialog.show();
        progressDialog.setTitle("Actualizando");
        progressDialog.setMessage("Espere por favor");
        user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(),pass_anterior);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        user.updatePassword(nuevo_pass)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        String value = NuevoPassET.getText().toString().trim();
                                        HashMap<String , Object> result = new HashMap<>();
                                        result.put("pass", value);
                                        //Actualizamos la nueva contraseña en la BD
                                        USUARIOS_DE_APP.child(user.getUid()).updateChildren(result)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(CambiarPassword.this, "Contraseña cambiada con éxito", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                            }
                                            });
                                        //Luego se cerrará la sesión
                                        firebaseAuth.signOut();
                                        startActivity(new Intent(CambiarPassword.this, Login.class));
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(CambiarPassword.this, "La contraseña actual no es la correcta", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Habilitamos la opción para retroceder una ventana
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}