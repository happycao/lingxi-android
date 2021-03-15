package me.cl.lingxi.common.util;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.view.textview.ClickableImageSpan;
import me.cl.lingxi.view.textview.ClickableMovementMethod;
import me.cl.lingxi.view.textview.NoLineClickableSpan;
import me.cl.lingxi.module.member.UserActivity;

/**
 * @author : happyc
 * e-mail : bafs.jy@live.com
 * time   : 2018/05/27
 * desc   : SpannableString,话题等处理
 * 参考：https://blog.csdn.net/u014620028/article/details/78394157
 * version: 1.0
 */
public class FeedContentUtil {

    private static final String AT = "@[\\w\\p{InCJKUnifiedIdeographs}-]{1,26}";
    private static final String TOPIC = "#[\\p{Print}\\p{InCJKUnifiedIdeographs}&&[^#]]+#";
    private static final String URL = "http[s]{0,1}://[a-zA-Z0-9+&@#/%?=~_\\-|!:,\\.;]*[a-zA-Z0-9+&@#/%=~_|]";
    private static final String URL_STR = "点击查看链接>>";

    private static final String ALL = "(" + AT + ")" + "|" + "(" + TOPIC + ")" + "|" + "(" + URL_STR + ")";

    public static SpannableStringBuilder getFeedText(String str, final TextView textView) {
        String content = str;

        // 处理匹配的url
        List<String> urls = new ArrayList<>();
        int urlIndex = 0;
        Pattern p = Pattern.compile(URL);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String urlStr = m.group();
            if (urlStr.contains("http://") || urlStr.contains("https://")) {
                //如果末尾有英文逗号或者中文逗号等，就去掉
                while (urlStr.endsWith(",") || urlStr.endsWith("，") || urlStr.endsWith(".") || urlStr.endsWith("。") || urlStr.endsWith(";") || urlStr.endsWith("；") || urlStr.endsWith("！") || urlStr.endsWith("!") || urlStr.endsWith("?") || urlStr.endsWith("？")) {
                    urlStr = urlStr.substring(0, urlStr.length() - 1);
                }
                urls.add(urlStr);
                content = content.replace(urlStr, URL_STR);
            }
        }

        // 处理开始
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
        // 设置正则
        Pattern pattern = Pattern.compile(ALL);
        Matcher matcher = pattern.matcher(spannableStringBuilder);

        if (matcher.find()) {
            if (!(textView instanceof EditText)) {
                textView.setMovementMethod(ClickableMovementMethod.getInstance());
                textView.setFocusable(false);
                textView.setClickable(false);
                textView.setLongClickable(false);
            }
            matcher.reset();
        }

        while (matcher.find()) {
            final String at = matcher.group(1);
            final String topic = matcher.group(2);
            final String url = matcher.group(3);

            // 处理@
            if (at != null) {
                int start = matcher.start(1);
                int end = start + at.length();
                NoLineClickableSpan myClickableSpan = new NoLineClickableSpan(textView.getContext()) {
                    @Override
                    public void onClick(View widget) {
                        Intent intent = new Intent(textView.getContext(), UserActivity.class);
                        String username = at.substring(1);
                        intent.putExtra(Constants.PASSED_USER_NAME, username);
                        textView.getContext().startActivity(intent);
                    }
                };
                spannableStringBuilder.setSpan(myClickableSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }

            // 处理##
            if (topic != null) {
                int start = matcher.start(2);
                int end = start + topic.length();
                NoLineClickableSpan clickableSpan = new NoLineClickableSpan(textView.getContext()) {
                    @Override
                    public void onClick(View widget) {
                        Toast.makeText(textView.getContext(), "点击了话题：" + topic, Toast.LENGTH_LONG).show();
                    }
                };
                spannableStringBuilder.setSpan(clickableSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }

            // 处理url
            if (url != null) {
                int start = matcher.start(3);
                int end = start + url.length();
                // 多链接处理
                final String urlPath = urls.get(urlIndex);
                urlIndex++;
                NoLineClickableSpan clickableSpan = new NoLineClickableSpan(textView.getContext()) {
                    @Override
                    public void onClick(View widget) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlPath));
                        textView.getContext().startActivity(browserIntent);
                    }
                };
                // 图片替换链接，暂不设置
                Drawable drawable = textView.getContext().getResources().getDrawable(R.drawable.ic_accessory);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                ClickableImageSpan imageSpan = new ClickableImageSpan(drawable, ImageSpan.ALIGN_BOTTOM) {
                    @Override
                    public void onClick(View widget) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlPath));
                        textView.getContext().startActivity(browserIntent);
                    }
                };
                spannableStringBuilder.setSpan(clickableSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        return spannableStringBuilder;
    }
}
