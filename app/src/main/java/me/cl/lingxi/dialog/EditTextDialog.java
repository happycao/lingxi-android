package me.cl.lingxi.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.textfield.TextInputLayout;

import me.cl.lingxi.R;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/08/02
 * desc   : 文本编辑Dialog
 * version: 1.0
 */
public class EditTextDialog extends DialogFragment {

    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String LENGTH = "length";

    private String mTitle;
    private String mContent;
    private int mLength;

    public interface PositiveListener {
        void Positive(String value);
    }

    private PositiveListener mPositiveListener;

    public void setPositiveListener(PositiveListener listener) {
        mPositiveListener = listener;
    }

    public static EditTextDialog newInstance(String title, String content, int length) {
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(CONTENT, content);
        args.putInt(LENGTH, length);
        EditTextDialog fragment = new EditTextDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString(TITLE);
            mContent = bundle.getString(CONTENT);
            mLength = bundle.getInt(LENGTH, 0);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.edit_dialog, null);
        final TextInputLayout textInputLayout = view.findViewById(R.id.text_input_layout);
        EditText editText = textInputLayout.getEditText();
        if (editText != null) {
            if (mLength > 0) {
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mLength)});
            }
            if (!TextUtils.isEmpty(mContent)) {
                editText.setText(mContent);
                editText.setSelection(mContent.length());
            }
            editText.setHint(mTitle);
        }
        builder.setMessage(mTitle);
        builder.setView(view);
        builder.setNegativeButton(R.string.action_negative, null);
        builder.setPositiveButton(R.string.action_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mPositiveListener != null) {
                    mPositiveListener.Positive(textInputLayout.getEditText().getText().toString().trim());
                }
            }
        });
        return builder.create();
    }
}
