package com.khulatech.testmboniapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.khulatech.mboni.api.ui.MBoniAPIActivity;
import com.khulatech.mboni.api.utils.CodeMBoniResult;
import com.khulatech.mboni.api.utils.MBoniApiConfiguration;

public class MainActivity extends AppCompatActivity {

    private static final int APP_REQUEST_CODE = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getLocation(View view) {
        // lance l'actictivity MBoniAPI
        Intent intent = new Intent(this, MBoniAPIActivity.class);
        MBoniApiConfiguration configuration = new MBoniApiConfiguration("Veuillez saisir le code MBoni de votre lieu de livraison", "HELLOK", "YOUR_API_KEY");
        intent.putExtra(MBoniAPIActivity.EXTRA_CONFIG, configuration);
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) {// confirm that this response matches your request
            if (data != null) {
                CodeMBoniResult codeSelected = data.getParcelableExtra(CodeMBoniResult.RESULT_KEY);
                if (codeSelected != null && codeSelected.getUniqueCode() != null && !codeSelected.getUniqueCode().isEmpty()) {
                    // si code mboni recu
                    onCodeMBoniSuccess(codeSelected);
                } else {
                    // si non
                    onCodeMBoniCancel();
                }
            }else{
                onCodeMBoniCancel();
            }

        }
    }

    private void onCodeMBoniSuccess(CodeMBoniResult codeMBoniResult){
        // si non
        Toast.makeText(this, "Code recu " + codeMBoniResult.getUniqueCode(), Toast.LENGTH_SHORT).show();
    }
    private void onCodeMBoniCancel(){
        // si non
        Toast.makeText(this, "Aucun Code recu", Toast.LENGTH_SHORT).show();
    }
}
