package com.grupoib3.schmidt.app_motorista.View;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.grupoib3.schmidt.app_motorista.Config.Config;
import com.grupoib3.schmidt.app_motorista.Models.Usuario;
import com.grupoib3.schmidt.app_motorista.R;
import com.grupoib3.schmidt.app_motorista.Utils.BancoController;
import com.grupoib3.schmidt.app_motorista.Utils.CriaBanco;
import com.grupoib3.schmidt.app_motorista.Utils.HttpServices;
import com.grupoib3.schmidt.app_motorista.Utils.TransformaDados;
import com.grupoib3.schmidt.app_motorista.Utils.UsuarioServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

public class MySettingsActivity extends PreferenceActivity {

    static Usuario user;
    static Cursor filial;
    static String _filial;
    static BancoController bd;
    static String Versao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my_settings);
        try {
            user = UsuarioServices.LoginMotorista(this);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        bd = new BancoController(this);
        filial = bd.carregaURLFilialByStatus();
        if(filial.moveToFirst())
            _filial = filial.getString(filial.getColumnIndexOrThrow(CriaBanco.LOCAL_FILIAL));
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            Versao = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        CheckBoxPreference update;
        String JSON_STRING;
        ProgressDialog progressDialog;
        EditTextPreference ultAtt;

        @TargetApi(Build.VERSION_CODES.M)
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            EditTextPreference editUser = (EditTextPreference) findPreference("Edit_preference_User");
            editUser.setText(TransformaDados.PrimeiraLetraMaius(user.getUser()));
            EditTextPreference editFilial = (EditTextPreference) findPreference("edit_text_Filial");
            editFilial.setText(_filial);
            EditTextPreference editVsApp = (EditTextPreference) findPreference("edit_text_vsapp");
            editVsApp.setText(Versao);
            editVsApp.setSummary(Versao);
            EditTextPreference editVsBd = (EditTextPreference) findPreference("edit_text_vsbanco");
            editVsBd.setText(String.valueOf(CriaBanco.VERSAO));
            editVsBd.setSummary(String.valueOf(CriaBanco.VERSAO));
            ultAtt = (EditTextPreference) findPreference("edit_text_ult_att");

            editUser.setSummary(TransformaDados.PrimeiraLetraMaius(user.getUser()));
            editFilial.setSummary(_filial);
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));

            editFilial.setSelectable(false);
            editUser.setSelectable(false);
            editVsApp.setSelectable(false);
            editVsBd.setSelectable(false);
            ultAtt.setSelectable(false);

            ultAtt.setSummary(ultAtt.getText());

            update = (CheckBoxPreference) findPreference("update_automatico");
            update.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    CheckBoxPreference cheque = (CheckBoxPreference) preference;
                    if(!cheque.isChecked()){
                        getJson();
                    }

                    return true;
                }
            });
            if(update.isChecked()){
                Log.d("update ", "selecionado");
                getJson();
            }
        }

        //metodo que busca as filiais
        public void getJson(){
            class GetJson extends AsyncTask<Void, Void, String> {


                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog =
                            ProgressDialog.show(getContext(),"Consultando filiais",
                                    "Aguarde...",true, false);
                }

                @Override
                protected String doInBackground(Void... params) {
                    try{
                        HttpServices rh = new HttpServices();

                        String url = filial.getString(filial.getColumnIndexOrThrow(CriaBanco.URL_FILIAL)) + Config.URL_FILIAIS;

                        String s = rh.getJSONFromAPI(url, "", "GET", user.getAccessToken());
                        return s;
                    }catch (Exception ex){
                        throw ex;
                    }
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    JSON_STRING = s;
                    showUser();
                }
            }
            GetJson gj = new GetJson();
            gj.execute();
        }

        private void showUser(){
            JSONObject jsonObject;
            try{
                jsonObject = new JSONObject(JSON_STRING);
                JSONArray filiais = jsonObject.optJSONArray(Config.TAG_FILIAIS);

                for(int i = 0; i < filiais.length(); i++){
                    JSONObject j  = filiais.optJSONObject(i);
                    ContentValues filial = new ContentValues();
                    filial.put(CriaBanco.ATIVO_FILIAL, j.optBoolean("ativo_url"));
                    filial.put(CriaBanco.COD_FILIAL, j.optString("cod_filial"));
                    filial.put(CriaBanco.NOME_FILIAL, j.optString("nome_filial"));
                    filial.put(CriaBanco.LOCAL_FILIAL, j.optString("local_filial"));
                    filial.put(CriaBanco.URL_FILIAL, j.optString("url_filial"));

                    bd.InsereFilial(filial);
                }

                String data = new Date().toString();
                ultAtt.setText(data);
                ultAtt.setSummary(data);

                progressDialog.dismiss();
            }catch (Exception e){
                progressDialog.dismiss();
                e.printStackTrace();
            }


        }
    }

}
