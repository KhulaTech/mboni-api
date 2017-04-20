package com.khulatech.mboni.api.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.khulatech.mboni.api.R;
import com.khulatech.mboni.api.R2;
import com.khulatech.mboni.api.utils.CodeMBoniResult;
import com.khulatech.mboni.api.utils.MBoniApiConfiguration;
import com.khulatech.mboni.api.utils.Utils;
import com.khulatech.mboni.api.webservice.WebServiceUtils;
import com.khulatech.mboni.api.webservice.Webservice;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment permettant de saisir un code ou d'en selectionner depuis l'application MBoni
 */

public class EnterCodeFragment extends Fragment {

    private View rootView;
    private OnEnterCodeFragmentInteractionListener mListener;

    private String toDisplayCode;
    private String apiKey;

    private int CODEMBONI_CARACTS_COUNT;
    private final int CODE_LENGTH_REACHED_VIBRATE_TIME = 100;
    private static final String CONFIG = "comfig";

    @BindView(R2.id.help_imageView)
    ImageView helpImageView;
    @BindView(R2.id.code_editText)
    EditText codeEditText;
    @BindView(R2.id.progressBar)
    ProgressBar progressBar;

    @BindView(R2.id.explanation_textView)
    TextView explanationTextView;

    @BindView(R2.id.choose_code_from_mboni_app_button)
    TextView chooseCodeFromMboniAppButton;
    private MBoniApiConfiguration mConfig;
    private String explanationText;


    public static EnterCodeFragment newInstance(MBoniApiConfiguration config) {
        EnterCodeFragment fragment = new EnterCodeFragment();
        Bundle args = new Bundle();
        args.putParcelable(CONFIG, config);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEnterCodeFragmentInteractionListener) {
            mListener = (OnEnterCodeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAddLocationAudioDescFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mConfig = getArguments().getParcelable(CONFIG);
            if (mConfig != null) {
                toDisplayCode = mConfig.getToDisplayCode();
                explanationText = mConfig.getEnterCodeExplanationText();
                apiKey = mConfig.getApiKey();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_enter_code, null, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // on change le text d'explication
        if(Utils.notEmptyString(explanationText)){
            explanationTextView.setText(explanationText);
        }
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        mListener.setToolBarTitle(getString(R.string.app_name));
    }

    private void init() {
        // on recupere la valeur du nombre maxi de caractere d'un code Mboni
        CODEMBONI_CARACTS_COUNT = getResources().getInteger(R.integer.code_mboni_max_caracteres_count);

        // onclick sur le bouton help afficher le dialog de help
        helpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHelpDialog();
            }
        });

        // ecouter la saisi de text, des que le nombre de caracteres est atteint, lancer la verification du code
        codeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable!=null && !editable.toString().isEmpty()
                        && editable.toString().length()==CODEMBONI_CARACTS_COUNT){
                    // des que le nombre de caracteres d'un code MBoni est atteint,
                    // vibrer
                    vibrate(CODE_LENGTH_REACHED_VIBRATE_TIME);
                    // on hide le keayboard
                    hideSoftKeyboard(codeEditText);
                    // lancer la verification du code
                    getCodeMBoniInfo(editable.toString());
                }
            }
        });


        // lancer l'app MBoni quand il clique sur la deuxieme option
        chooseCodeFromMboniAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.getCodeFromMboniApp();
            }
        });

        // si un code est à charger
        if (Utils.notEmptyString(toDisplayCode)){
            codeEditText.setText(toDisplayCode);
        }

    }


    /**
     * Méthode pour afficher un dialog qui explique ce qu'est MBoni
     */
    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.mboni_api_whats_is_mboni);
        builder.setPositiveButton(R.string.mboni_api_ok_compris, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setMessage(getString(R.string.mboni_api_whats_is_mboni_explanation));
        dialog.show();
    }

    private void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void vibrate(long milliseconds){
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(milliseconds);
    }



    void showLoading(){
        progressBar.setVisibility(View.VISIBLE);
    }

    void hideLoading(){
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Méthode pour vérifier si un code MBoni est valide
     * @param codeMboni
     */
    private void getCodeMBoniInfo(final String codeMboni) {
        // afficher le loadingBar
        showLoading();
        // TODO appel WS pr avoir infos code
        Webservice ws = WebServiceUtils.getWebserviceManager();
        Call<CodeMBoniResult> call = ws.getCodeInfo(apiKey, codeMboni);
        call.enqueue(new Callback<CodeMBoniResult>() {
            @Override
            public void onResponse(Call<CodeMBoniResult> call, Response<CodeMBoniResult> response) {
                hideLoading();
                CodeMBoniResult location = response.body();
                if (location != null && location.getUniqueCode() != null ) {
                    // on reinitilise le toDisplayCode
                    toDisplayCode = null;
                    // on reinitialise le champ de saisie
                    codeEditText.setText("");
                    mListener.locationFounded(location);
                } else {
                    // si code non retrouver
                    locationNotFounded();
                }
            }

            @Override
            public void onFailure(Call<CodeMBoniResult> call, Throwable t) {
                hideLoading();
                // afficher un snackBar permettant de relancer l'operation
                Snackbar snackBar = Snackbar.make(codeEditText, R.string.mboni_api_erreur_serveur, Snackbar.LENGTH_INDEFINITE);
                snackBar.setAction(R.string.mboni_api_reassayer, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getCodeMBoniInfo(codeMboni);
                    }
                });
                snackBar.show();
            }
        });

    }


    /**
     * Methode appele quand le code saisi n'est pas valide
     */
    private void locationNotFounded() {
        Snackbar snackBar = Snackbar.make(codeEditText, R.string.mboni_api_code_not_founded, Snackbar.LENGTH_SHORT);
        snackBar.show();
        // on ramene
        codeEditText.requestFocus();
    }

    public interface OnEnterCodeFragmentInteractionListener {
        void locationFounded(CodeMBoniResult codeR);
        void onLocationChoosed(CodeMBoniResult codeR);
        void setToolBarTitle(String title);
        void getCodeFromMboniApp();
    }
}
