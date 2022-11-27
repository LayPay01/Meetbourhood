package com.example.meethbourhood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    TextView app_name;
    EditText CorreoLogin, PasswordLogin;
    Button INGRESARGOOGLE;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Login");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        app_name = findViewById(R.id.app_name);

        CorreoLogin = findViewById(R.id.CorreoLogin);
        PasswordLogin = findViewById(R.id.PasswordLogin);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(Login.this);
        dialog = new Dialog(Login.this);

        cambioletra();

        //crearSolicitud();   ESTAS DOS LINEAS SON DEL MÉTODO DEL LOG IN CON GOOGLE

        //INGRESARGOOGLE.setOnClickListener(view -> signIn());
    }

    private void cambioletra(){
        /*FUENTE DE LETRA*/
        String ubicacion = "fuentes/sans_medio.ttf";
        Typeface Tf = Typeface.createFromAsset(Login.this.getAssets(),ubicacion);
        /*FUENTE DE LETRA*/
        app_name.setTypeface(Tf);
        CorreoLogin.setTypeface(Tf);
        PasswordLogin.setTypeface(Tf);
    }

    private void crearSolicitud(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
    }

    private void signIn(){
        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AutenticacionFireBase(account);
            }catch (ApiException e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void AutenticacionFireBase(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            if(task.getResult().getAdditionalUserInfo().isNewUser()){
                                assert user != null;
                                String uid = user.getUid();
                                String correo = user.getEmail();
                                String nombre = user.getDisplayName();

                                HashMap<Object,String> DatosUsuario = new HashMap<>();

                                DatosUsuario.put("uid", uid);
                                DatosUsuario.put("correo", correo);
                                //DatosUsuario.put("pass", pass);
                                DatosUsuario.put("nombre", nombre);
                                //DatosUsuario.put("apellido", apellido);
                                DatosUsuario.put("edad","");
                                DatosUsuario.put("telefono","");
                                DatosUsuario.put("direccion","");
                                //La imagen de momento estará vacía
                                DatosUsuario.put("imagen","");

                                //Inicializamos la instancia de la base de datos de firebase
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                //Creamos la base de datos
                                DatabaseReference reference = database.getReference("USUARIOS_DE_APP");
                                //Nombre de la BD "USUARIOS_DE_APP"
                                reference.child(uid).setValue(DatosUsuario);
                            }

                            startActivity(new Intent(Login.this, Inicio.class));
                        } else{
                            Dialog_No_Inicio();
                        }
                    }
                });
    }

    public void ingresa(View view){
        String correo = CorreoLogin.getText().toString();
        String pass = PasswordLogin.getText().toString();

        if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            CorreoLogin.setError("Correo inválido");
            CorreoLogin.setFocusable(true);
        }else if(pass.length()<6){
            PasswordLogin.setError("La contraseña debe ser mayor o igual a 6 digitos.");
            PasswordLogin.setFocusable(true);
        }else{
            LOGINUSUARIO(correo,pass);
        }
    }

    //Método para logear al usuario.
    private void LOGINUSUARIO(String correo, String pass) {
        progressDialog.setTitle("Iniciando sesión");
        progressDialog.setMessage("Espere por favor...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(correo,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            //Cuando iniciamos sesión nos manda a la act. de inicio.
                            startActivity(new Intent(Login.this, Inicio.class));
                            assert user != null;
                            Toast.makeText(Login.this, "¡Bienvenido(a)! "+user.getEmail(), Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(Login.this, "Algo ha salido mal.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener((e) -> {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    //Creación del diálogo personalizado
    private void Dialog_No_Inicio(){
        Button ok_no_inicio;

        dialog.setContentView(R.layout.no_sesion);//Hacemos la conexión con la vista creada.

        ok_no_inicio = dialog.findViewById(R.id.ok_no_inicio);

        ok_no_inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false); //Al pulsar fuera del recuadro, seguirá mostrandose.
        dialog.show();
    }

    //Habilitamos la opción para retroceder una ventana
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}