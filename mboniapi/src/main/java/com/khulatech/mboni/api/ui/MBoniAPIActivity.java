package com.khulatech.mboni.api.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.khulatech.mboni.api.R;
import com.khulatech.mboni.api.ui.fragments.EnterCodeFragment;
import com.khulatech.mboni.api.ui.fragments.LocationDetailFragment;
import com.khulatech.mboni.api.utils.CodeMBoniResult;
import com.khulatech.mboni.api.utils.MBoniApiConfiguration;
import com.khulatech.mboni.api.utils.Utils;


/**
 * Activity vous permettant de lancer l'interface de selection de code MBoni
 *      - si un extra #EXTRA_TO_DISPLAY_CODE est passé à l'intent de cet activity alors
 *          le code Mboni passé en valeur de l'extra est directement affiché
 *      - si non la page permettant de saisir un code mboni ou d'en selectionner
 *          depuis l'app MBoni est alors affiché
 */
public class MBoniAPIActivity extends AppCompatActivity implements
        EnterCodeFragment.OnEnterCodeFragmentInteractionListener,
        LocationDetailFragment.OnLocationDetailsFragmentInteractionListener {

    public static final String EXTRA_CONFIG = "config";

    private static final int REQUESTCODE_LAUNCH_MBONI_APP = 33;

    private ActionBar toolbar;
    private boolean codechoosed;
    private MBoniApiConfiguration mConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_with_frame);

        Fresco.initialize(this);

        toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setDisplayHomeAsUpEnabled(true);
        }

        // on recupere le #Location passer en argument si il y en a
        if(getIntent().getExtras()!=null){
            mConfig = getIntent().getExtras().getParcelable(EXTRA_CONFIG);
        }

        loadFragment(EnterCodeFragment.newInstance(mConfig), false);
    }



    /**
     * Methode permettant de charger les fragments
     * @param fragment
     * @param addToBackStack
     */
    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, fragment);
        if(addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!codechoosed) {
            sendCancelResult();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void locationFounded(CodeMBoniResult location) {
        // on charge les infos de la localisation fragmentlocation detail
        loadFragment(LocationDetailFragment.newInstance(location), true);
    }

    @Override
    public void setToolBarTitle(String title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    @Override
    public void getCodeFromMboniApp() {
        Intent intent = new Intent("com.khulatech.mboni.selectcode");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            startActivityForResult(intent, REQUESTCODE_LAUNCH_MBONI_APP);
        } catch (android.content.ActivityNotFoundException ex) {
            try {
                Intent linkIntent = new Intent();
                linkIntent.setData(Uri.parse(getString(R.string.mboni_api_code_select_app_link)));
                startActivityForResult(linkIntent, REQUESTCODE_LAUNCH_MBONI_APP);
            } catch (android.content.ActivityNotFoundException exe){
                // lancer le playStore pour installer l'application
                Utils.installMBoniApp(this);
            }
        }
    }

    @Override
    public void onLocationChoosed(CodeMBoniResult codeR) {
        sendCodeOkResult(codeR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_LAUNCH_MBONI_APP){
            if (data != null) {
                CodeMBoniResult codeSelected = null;
                String loginResultJson = data.getStringExtra(CodeMBoniResult.RESULT_KEY);
                try {
                    Gson gson = new GsonBuilder().create();
                    codeSelected = gson.fromJson(loginResultJson, CodeMBoniResult.class);
                }catch (Exception ex){
                    // TODO Notifier l'erreur au serveur
                    ex.printStackTrace();
                }
                onLocationChoosed(codeSelected);
            }
        }
    }

    private void sendCodeOkResult(CodeMBoniResult codeMBoniResult){
        this.sendResult(RESULT_OK, codeMBoniResult);
    }

    private void sendCancelResult(){
        sendResult(RESULT_CANCELED, null);
    }

    private void sendResult(int resultCode, CodeMBoniResult codeMboniResult) {
        Intent data = getIntent();
        data.putExtra(CodeMBoniResult.RESULT_KEY, codeMboniResult);
        this.setResult(resultCode, data);
        codechoosed = true;
        this.finish();
    }


}
