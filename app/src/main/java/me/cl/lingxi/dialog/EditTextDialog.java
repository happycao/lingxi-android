package me.cl.lingxi.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import me.cl.lingxi.R;
import me.cl.lingxi.databinding.EditDialogBinding;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/08/02
 * desc   : 文本编辑Dialog
 * version: 1.0
 */
public class EditTextDialog extends DialogFragment {

    private EditDialogBinding mBinding;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mBinding = EditDialogBinding.inflate(LayoutInflater.from(requireContext()));
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        EditText editText = mBinding.textInputLayout.getEditText();
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
        builder.setTitle(mTitle);
        builder.setView(mBinding.getRoot());
        builder.setNegativeButton(R.string.action_negative, null);
        builder.setPositiveButton(R.string.action_positive, (dialog, which) -> {
            if (mPositiveListener != null) {
                mPositiveListener.Positive(mBinding.textInputLayout.getEditText().getText().toString().trim());
            }
        });
        return builder.create();
    }
}
