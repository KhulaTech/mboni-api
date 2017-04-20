package com.khulatech.mboni.api.ui.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.khulatech.mboni.api.R;
import com.khulatech.mboni.api.R2;
import com.khulatech.mboni.api.ui.fragments.runtime_permissions.WriteStoragePermissionHelper;
import com.khulatech.mboni.api.utils.BannerItem;
import com.khulatech.mboni.api.utils.CodeMBoniResult;
import com.khulatech.mboni.api.utils.RetrofitProgressDownloader;
import com.khulatech.mboni.api.utils.SimpleImageBanner;
import com.khulatech.mboni.api.utils.Utils;
import com.khulatech.mboni.api.webservice.Webservice;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationDetailFragment extends Fragment implements 
        WriteStoragePermissionHelper.WriteStoragePermissionCallback {

    private static final String ARG_LOCATION = "arg_location";
    private static final String TAG = "LocationDetailFragment";
    private static final int PROGRESS_MAX_VALUE = 100;

    @BindView(R2.id.sib_the_most_comlex_usage)
    SimpleImageBanner simpleImageBanner;
    @BindView(R2.id.code_unique_textView)
    TextView codeUniqueTextView;
    @BindView(R2.id.codeUniqueCardV)
    CardView codeUniqueCardV;
    @BindView(R2.id.player_control_imageView)
    ImageView playerControlImageView;
    @BindView(R2.id.player_seekBar)
    SeekBar playerSeekBar;
    @BindView(R2.id.player_audio_download_progressBar)
    ProgressBar playerAudioDownloadProgressBar;
    @BindView(R2.id.audioCardV)
    CardView audioCardV;
    @BindView(R2.id.geo_code_textView)
    TextView goeCodeTextView;
    @BindView(R2.id.description_textView)
    TextView descriptionTextView;
    @BindView(R2.id.descriptionCardV)
    CardView descriptionCardV;
    @BindView(R2.id.staticImgV)
    SimpleDraweeView staticImgV;
    @BindView(R2.id.staticImgVCardV)
    CardView staticImgVCardV;
    @BindView(R2.id.enlargeStaticImg_imgview)
    ImageView enlargeStaticImgImgview;
    @BindView(R2.id.download_imageView)
    ImageView downloadImageView;
    @BindView(R2.id.audio_time_textView)
    TextView audioTimeTextView;
    @BindView(R2.id.comfirm_code_button)
    Button comfirmCodeButton;

    private CodeMBoniResult currentLocation;


    private OnLocationDetailsFragmentInteractionListener mListener;
    private View rootView;
    private AlertDialog editLocationAlertDialog;
    private String audioFilePath;

    private Timer timer;
    private int playerSecondsElapsed;

    private MediaPlayer player;
    private Handler mPlayerSeekBarHandler = new Handler();
    private Runnable audioPlayerSeekBarUpdater;
    private int staticMapImgHeight;
    private int staticMapImgWidth;
    private final int seekBarRefreshDelayMillis = 1;
    private WriteStoragePermissionHelper mPermCheckFrag;

    public LocationDetailFragment() {
        setHasOptionsMenu(true);
    }

    public static LocationDetailFragment newInstance(Parcelable parcelable) {
        LocationDetailFragment locationDetailFragment = new LocationDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOCATION, parcelable);
        locationDetailFragment.setArguments(args);
        return locationDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View getPictureView() {
        return simpleImageBanner;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_location_detail, container, false);

        ButterKnife.bind(this, rootView);

        mListener.setToolBarTitle(currentLocation.getNom());

        final ArrayList<BannerItem> bannerItems = new ArrayList<>();
        if (Utils.notEmptyString(currentLocation.getPhotoSecondaire())) {
            bannerItems.add(new BannerItem(currentLocation.getPhotoDevanture(),
                    getString(R.string.mboni_api_photo_devanture)));
        }
        if (Utils.notEmptyString(currentLocation.getPhotoSecondaire())) {
            bannerItems.add(new BannerItem(currentLocation.getPhotoSecondaire(),
                    getString(R.string.mboni_api_photo_secondaire)));
        }
        if (bannerItems.size()>0) {

            simpleImageBanner.setSource(bannerItems)
                    .setOnImageClickCallback(new SimpleImageBanner.OnImageClickCallback() {
                        @Override
                        public void onImageClick(int position) {
                            showImageInBig(bannerItems, position);
                        }
                    })
                    .startScroll();
        }

        codeUniqueTextView.setText(currentLocation.getUniqueCode());

        goeCodeTextView.setText(
                currentLocation.getStreet()
                        +", "+
                        currentLocation.getTown()
                        +", "+
                        currentLocation.getCountry()
        );

        if (Utils.notEmptyString(currentLocation.getDescription())) {
            descriptionCardV.setVisibility(View.VISIBLE);
            descriptionTextView.setText(currentLocation.getDescription().trim());
        } else {
            descriptionCardV.setVisibility(View.GONE);
        }


        ViewTreeObserver vto = staticImgV.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                staticImgV.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                staticMapImgWidth  = staticImgV.getMeasuredWidth();
                staticMapImgHeight = staticImgV.getMeasuredHeight();
                initStaticMapView();
            }
        });


        downloadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLocation.getAudio() != null && !currentLocation.getAudio().isEmpty()) {
                    startAudioDownloadAsked();
                }else {
                    Toast.makeText(getContext(), getString(R.string.mboni_api_no_audio_to_download), Toast.LENGTH_SHORT).show();
                }

            }
        });

        // par defaut la seekBar est désactivée
        playerSeekBar.setEnabled(false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        comfirmCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onLocationChoosed(currentLocation);
            }
        });
    }

    /**
     * Méthode pour initialiser l'image static google Map
     */
    private void initStaticMapView(){
        int markerColor = getResources().getColor(R.color.accent);
        // affichage de l'image static google map
        String staticMapImgUrl = Webservice.BASE_URL_STATIC_MAP_IMG
                + "Latitude=" + currentLocation.getLatitude()
                + "&"
                + "Longitude=" + currentLocation.getLongitude()
                + "&"
                + "Height=" + staticMapImgHeight
                + "&"
                + "Width=" + staticMapImgWidth
                + "&"
                + "MarkerColor=" + markerColor;
//        Picasso.with(getContext())
//                .load(staticMapImgUrl)
//                .into(staticImgV);
        staticImgV.setImageURI(Uri.parse(staticMapImgUrl));

        View.OnClickListener onMapImageClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchMBoniApp(currentLocation);
            }
        };
        staticImgV.setOnClickListener(onMapImageClickListener);
        enlargeStaticImgImgview.setOnClickListener(onMapImageClickListener);
    }

    private void launchMBoniApp(CodeMBoniResult currentLocation) {
        // TODO lancer l'application MBONI pour permettre au user de voir le map
    }



    private void startAudioDownloadAsked() {
        /**
         * Ici on check si l'app dispose des permissions necessaire
         * Des methodes de callBack sont mise en place à travers l'implementation de
         * {@Link WriteStoragePermissionCallback} pour agir en fonction de l'etat de la permission
         */
        mPermCheckFrag = (WriteStoragePermissionHelper) getChildFragmentManager()
                .findFragmentByTag(WriteStoragePermissionHelper.TAG);
        if (mPermCheckFrag == null) {
            mPermCheckFrag = WriteStoragePermissionHelper.newInstance(this, getContext());
            getChildFragmentManager().beginTransaction()
                    .add(mPermCheckFrag, WriteStoragePermissionHelper.TAG)
                    .commit();
        }else {
            mPermCheckFrag.checkPermissions();
        }
    }


    @Override
    public void onWriteStoragePermissionGranted() {
        startAudioDownload();
    }

    @Override
    public void onWriteStoragePermissionDenied() {
        onAudioDownLoadComplete(false);
    }
    
    private void startAudioDownload() {
        // Afficher le loadingBar
        playerAudioDownloadProgressBar.setVisibility(View.VISIBLE);
        // on prepare le dossier d'enregistrement
        String dirPath = Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name_condensed);
        Utils.createDirIfNotExist(dirPath);
        audioFilePath = dirPath + "/audio_" + System.currentTimeMillis() + ".aac";

        // on création et lancement du downloader
        RetrofitProgressDownloader.ProgressListener progressListener = new RetrofitProgressDownloader.ProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                // modifer l'état du progressBar
                playerAudioDownloadProgressBar.setProgress((int) ((PROGRESS_MAX_VALUE * bytesRead) / contentLength));
            }
        };

        new RetrofitProgressDownloader(currentLocation.getAudio(), audioFilePath, progressListener) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                downloadImageView.setVisibility(View.GONE);
                playerAudioDownloadProgressBar.setIndeterminate(false);
                playerAudioDownloadProgressBar.setMax(PROGRESS_MAX_VALUE);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                onAudioDownLoadComplete(aBoolean);
            }
        }.execute();

    }

    private void onAudioDownLoadComplete(boolean success) {
        if (success) {
            // success du download

            // cacher le progressBar
            playerAudioDownloadProgressBar.setVisibility(View.GONE);
            // afficher le bouton play
            playerControlImageView.setVisibility(View.VISIBLE);
            playerControlImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    togglePlaying();
                }
            });

        } else {
            // echec du download
            Snackbar.make(rootView, R.string.mboni_api_audio_download_failed, Snackbar.LENGTH_SHORT).show();
            downloadImageView.setVisibility(View.VISIBLE);
            playerAudioDownloadProgressBar.setVisibility(View.INVISIBLE);
        }
    }


    public void togglePlaying() {
        Utils.wait(100, new Runnable() {
            @Override
            public void run() {
                if (isPlaying()) {
                    stopPlaying();
                } else {
                    startPlaying();
                }
            }
        });
    }

    /**
     * Méthode pour lancer la lecture de l'audio
     */
    private void startPlaying() {
        try {
            player = new MediaPlayer();
            player.setDataSource(audioFilePath);
            player.prepare();
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopPlaying();
                }
            });

            audioTimeTextView.setText("00:00:00");
            playerControlImageView.setImageResource(R.drawable.aar_ic_stop);

            playerSecondsElapsed = 0;
            startTimer();

            // activer la seekBar
            playerSeekBar.setEnabled(true);
            playerSeekBar.setMax(PROGRESS_MAX_VALUE);

            audioPlayerSeekBarUpdater = new Runnable() {
                @Override
                public void run() {
                    if(isPlaying()) {
                        playerSeekBar.setProgress((PROGRESS_MAX_VALUE*player.getCurrentPosition())/player.getDuration());
                        mPlayerSeekBarHandler.postDelayed(this, seekBarRefreshDelayMillis); //
                    }
                }
            };
            getActivity().runOnUiThread(audioPlayerSeekBarUpdater);

            playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(player != null && fromUser){
                        player.seekTo( (progress * player.getDuration())/PROGRESS_MAX_VALUE);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This méthode Stop playing the audio file
     */
    private void stopPlaying() {
        playerControlImageView.setImageResource(R.drawable.aar_ic_play);
        playerSeekBar.setEnabled(false);

        if (player != null) {
            try {
                player.stop();
                player.reset();
            } catch (Exception e) {
            }
        }

        stopTimer();
    }

    private boolean isPlaying() {
        try {
            return player != null && player.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    private void startTimer() {
        stopTimer();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTimer();
            }
        }, 0, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    private void updateTimer() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isPlaying()) {
                    playerSecondsElapsed++;
                    audioTimeTextView.setText(Utils.formatSeconds(playerSecondsElapsed));
                }
            }
        });
    }

//    private void loadLocationOnMap() {
//        // TODO charger le fragment de map
//        MapDialogFragment mapDialog = MapDialogFragment.newInstance(currentLocation);
//        mapDialog.show(getFragmentManager(), "mapDialog");
//    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLocationDetailsFragmentInteractionListener) {
            mListener = (OnLocationDetailsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAddLocationAudioDescFragmentInteractionListener");
        }

        Bundle args = getArguments();
        if (args != null) {
            currentLocation = args.getParcelable(ARG_LOCATION);
        }
    }

    /**
     * Méthode permettant d'afficher en grand la photo choisie
     * @param bannerItems
     * @param position
     */
    public void showImageInBig(ArrayList<BannerItem> bannerItems, int position) {

        if (bannerItems != null) {
            ArrayList<String> urls = new ArrayList<>();
            for (int i = 0; i < bannerItems.size(); i++) {
                urls.add(bannerItems.get(i).imgUrl);
            }
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View dialogueOverlayView =  inflater.inflate(R.layout.dialogue_fullscreen_image_overlay, null);
            View backArrow = dialogueOverlayView.findViewById(R.id.image_to_show);

            final ImageViewer imgViewDialog = new ImageViewer.Builder(getContext(), urls)
                    .setStartPosition(position)
//                    .setBackgroundColorRes(R.color.blue_transparent)
                    //.setBackgroundColor(color)
                    .setOverlayView(dialogueOverlayView)
                    .show();

            if (backArrow != null) {
                backArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        imgViewDialog.onDismiss();
                    }
                });
            }
        }
    }



    public interface OnLocationDetailsFragmentInteractionListener {
        void setToolBarTitle(String title);
        void onLocationChoosed(CodeMBoniResult codeR);
    }
}
