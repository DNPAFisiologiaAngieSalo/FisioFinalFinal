package com.example.fisiofinalfinal.Controlador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fisiofinalfinal.Vista.Opciones;
import com.example.fisiofinalfinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText usernames;
    private EditText passwE;
    private Button login;
    private Button register;

    private FirebaseAuth authUser;
    private FirebaseAuth.AuthStateListener FBListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        authUser = FirebaseAuth.getInstance();
        FBListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    //Intent intent = new Intent(MainActivity.this, interfazPrincipal.class);
                    Intent intent = new Intent(MainActivity.this, Opciones.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };
        //Metodo para invocar al registro
        RegistroApp();
        //Metodo para invocar el inicio de sesión
        IniciarSesion();

    }

    //Metodo encargado de registrar un usuario
    protected void RegistroApp() {
        usernames = (EditText) findViewById(R.id.editText3);
        passwE = (EditText) findViewById(R.id.editText2);
        login = (Button) findViewById(R.id.button);
        register = (Button) findViewById(R.id.button2);

        register.setOnClickListener(new View.OnClickListener() {
            //Cargamos los datos para subir a la base de datos
            @Override
            public void onClick(View v) {
                final String email = usernames.getText().toString();
                final String passwV = passwE.getText().toString();
                //Empieza el procedimiento para añadir dentro del apartado correspondiente en la base de dato
                authUser.createUserWithEmailAndPassword(email, passwV).addOnCompleteListener(MainActivity.this
                        , new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //Manejo de error
                                if (!task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, getString(R.string.errorRegistro), Toast.LENGTH_LONG).show();
                                } else {
                                    String user_id = authUser.getCurrentUser().getUid();
                                    //Añadimos el usuario al nodo correspondiente
                                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference()
                                            .child("Users").child("Clients").child(user_id);
                                    //Debemos asegurar que el registro se ha añadido
                                    current_user_db.setValue(true);
                                    Toast.makeText(MainActivity.this, getString(R.string.exitoRegistro), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    // Metodo encargado en el inicio de sesion
    protected void IniciarSesion() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = usernames.getText().toString();
                final String passw = passwE.getText().toString();

                authUser.signInWithEmailAndPassword(email, passw).addOnCompleteListener(MainActivity.this
                        , new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //Manejo de error
                                if (!task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this
                                            , getString(R.string.errorIngreso), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(MainActivity.this
                                            , getString(R.string.exitoIngreso), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    //Cuando la activity se inicia, tambien se iniciara el listener para la db
    @Override
    protected void onStart() {
        super.onStart();
        authUser.addAuthStateListener(FBListener);
    }

    //Cuando la activity pasa a segundo plano, el listener se desactiva
    @Override
    protected void onStop() {
        super.onStop();
        authUser.removeAuthStateListener(FBListener);
    }
}