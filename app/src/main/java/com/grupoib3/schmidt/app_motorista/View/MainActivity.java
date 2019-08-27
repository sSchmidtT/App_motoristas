package com.grupoib3.schmidt.app_motorista.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.grupoib3.schmidt.app_motorista.Models.Usuario;
import com.grupoib3.schmidt.app_motorista.R;
import com.grupoib3.schmidt.app_motorista.Utils.BancoController;
import com.grupoib3.schmidt.app_motorista.Utils.UsuarioServices;

import java.text.ParseException;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private  AlertDialog alertDialog;
    BancoController db;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        userId = (int) intent.getSerializableExtra("userID");

        //loading the default fragment
        loadFragment(new MarcacaoFragment());

        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        db = new BancoController(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try{
            int qntNotifi = db.contaNotificacoesAtivas(userId);
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_principal, menu);
            MenuItem item = menu.getItem(2);
            if(qntNotifi > 0)
                item.setIcon(R.drawable.ic_stat_notifications_active);
            else
                item.setIcon(R.drawable.ic_stat_notifications_none);
            return true;
        }catch (Exception ex){
            throw ex;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try{
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                startActivity(
                        new Intent(
                                MainActivity.this, MySettingsActivity.class));
            }else if(id == R.id.action_deslogar){
                try {
                    boolean deslogar = UsuarioServices.DeslogarUser(this);
                    if(deslogar){
                        startActivity(
                                new Intent(
                                        MainActivity.this, LoginActivity.class));
                        finish();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else if(id == R.id.action_notifi){
                startActivity(
                        new Intent(
                                MainActivity.this, NotificationActivity.class));
            }

            return super.onOptionsItemSelected(item);
        }catch (Exception ex){
            throw ex;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.navigation_marc:
                fragment = new MarcacaoFragment();
                break;
            case R.id.navigation_hist:
                fragment = new MarcacoesFragment();
                break;
            /*case R.id.navigation_config:
                fragment = new ConfigFragment();
                break;*/
        }
        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mpark Motorista");
        builder.setMessage("Deseja fechar o programa?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void DeslogarUsuario() throws ParseException {
        boolean deslogar = UsuarioServices.DeslogarUser(this);
        if(deslogar){
            finish();
        }
    }
}
