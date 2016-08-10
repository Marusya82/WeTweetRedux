package com.codepath.apps.restclienttemplate.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class ComposeDialogFragment extends DialogFragment {

    @BindView(R.id.etTweetBody) EditText etTweetBody;
    @BindView(R.id.btnTweet) Button btnTweet;
    @BindView(R.id.textView) TextView tvCount;
    @BindView(R.id.ivDialogProfileImage) ImageView imDialogProfileImage;
    @BindView(R.id.ivCloseImage) ImageView imCloseImage;

    private Unbinder unbinder;
    private String inReplyToUserName;
    private String inReplyToStatusId;
    private String profileUrl;

    public ComposeDialogFragment() {}

    public static ComposeDialogFragment newInstance(String title) {
        ComposeDialogFragment frag = new ComposeDialogFragment();
        return frag;
    }

    // Defines the listener interface with a method passing back data result
    public interface ComposeDialogListener {
        void onFinishTweetDialog(String str, String num);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null && !bundle.isEmpty()) {
            inReplyToUserName = bundle.getString("in_reply_to", "");
            inReplyToStatusId = bundle.getString("status_id", "");
            profileUrl = bundle.getString("profile_url", "");
        }
        View view = inflater.inflate(R.layout.fragment_compose_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Glide.with(getContext()).load(profileUrl).bitmapTransform(new RoundedCornersTransformation(getContext(), 5, 0)).into(imDialogProfileImage);
        imCloseImage.setImageResource(0);
        imCloseImage.setImageResource(R.drawable.close_button);
        imCloseImage.setOnClickListener(v -> dismiss());

        if (inReplyToUserName != null && !inReplyToUserName.isEmpty()) etTweetBody.setText(String.format("@%s ", inReplyToUserName));
        etTweetBody.requestFocus();
        // Show soft keyboard automatically and request focus to field
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        btnTweet.setOnClickListener(v -> {
            ComposeDialogListener listener = (ComposeDialogListener) getTargetFragment();
            String passIt = etTweetBody.getText().toString();
            listener.onFinishTweetDialog(passIt, inReplyToStatusId);
            dismiss();
        });

        etTweetBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft) {}

            @Override
            public void afterTextChanged(Editable s) {
                // this will show characters remaining
                int num = 140 - s.toString().length();
                tvCount.setText(num + "");
                if (num <= 19) tvCount.setTextColor(Color.RED);
                else tvCount.setTextColor(Color.LTGRAY);
                boolean limit = etTweetBody.getText().length() > 0 && etTweetBody.getText().length() <= 140;
                btnTweet.setEnabled(limit);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
