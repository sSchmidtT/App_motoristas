package com.grupoib3.schmidt.app_motorista.View;

import android.annotation.SuppressLint;
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
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grupoib3.schmidt.app_motorista.Models.Usuario;
import com.grupoib3.schmidt.app_motorista.R;
import com.grupoib3.schmidt.app_motorista.Utils.BancoController;
import com.grupoib3.schmidt.app_motorista.Utils.UsuarioServices;

import java.text.ParseException;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BancoController db;
    private TextView badge;
    private FrameLayout redCircle;
    private Toast toast;
    private long lastBackPressTime = 0;
    private int userId;
    private int userIdFilial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userId = (int) bundle.getSerializable("userId");
        userIdFilial = (int) bundle.getSerializable("userIdFilial");

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
            // Inflate the menu; this adds items to the action bar if it is present.
            super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.menu_principal, menu);
            return true;
        }catch (Exception ex){
            throw ex;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem notifi = menu.findItem(R.id.action_notifi);
        FrameLayout rootView = (FrameLayout) notifi.getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        badge = (TextView) redCircle.findViewById(R.id.badge_notification_txt);

        int qntNotifi = db.contaNotificacoesAtivas(userId, userIdFilial);

        if(qntNotifi > 0){
            badge.setText(String.valueOf(qntNotifi));
            badge.setVisibility(View.VISIBLE);
        }
        else
        if(redCircle.getVisibility() == View.VISIBLE)
            redCircle.setVisibility(View.INVISIBLE);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(notifi);
            }
        });
        return super.onPrepareOptionsMenu(menu);
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
                Intent intent = new Intent(this.getBaseContext(), NotificationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("userId", userId);
                bundle.putSerializable("userIdFilial", userIdFilial);
                intent.putExtras(bundle);
                this.startActivity(intent);
                finish();
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

    @SuppressLint("WrongConstant")
    @Override
    public void onBackPressed(){
        if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
            toast = Toast.makeText(this, "Pressione o botão voltar novamente para fechar este App.", 4000);
            toast.show();
            this.lastBackPressTime = System.currentTimeMillis();
        } else {
            if (toast != null) {
                toast.cancel();
            }
            finish();
        }
    }

    public void DeslogarUsuario() throws ParseException {
        boolean deslogar = UsuarioServices.DeslogarUser(this);
        if(deslogar){
            finish();
        }
    }
}
