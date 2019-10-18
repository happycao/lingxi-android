package me.cl.lingxi.module.main;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.library.base.BaseFragment;
import me.cl.library.photo.PhotoBrowser;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.common.glide.GlideApp;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.util.GsonUtil;
import me.cl.lingxi.common.util.NetworkUtil;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.module.search.SearchActivity;
import okhttp3.Call;

public class HomeFragment extends BaseFragment {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.random_image)
    ImageView mRandomImage;
    @BindView(R.id.image_source)
    TextView mImageSource;
    @BindView(R.id.hitokoto_info)
    TextView mHitokotoInfo;
    @BindView(R.id.hitokoto_author)
    TextView mHitokotoAuthor;
    @BindView(R.id.hitokoto_source)
    TextView mHitokotoSource;

    private static final String TYPE = "type";

    private String mType;
    private String mImageUrl;
    private boolean openTuPics;

    public HomeFragment() {

    }

    public static HomeFragment newInstance(String newsType) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(TYPE, newsType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getString(TYPE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        openTuPics = SPUtil.build().getBoolean("open_tu_pics");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        ToolbarUtil.init(mToolbar, getActivity())
                .setTitle(R.string.title_bar_home)
                .setTitleCenter()
                .setMenu(R.menu.search_menu, new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_search:
                                gotoSearch();
                                break;
                        }
                        return false;
                    }
                })
                .build();

        initView();
        initData();
    }

    private void initView() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtil.isWifiConnected(mSwipeRefreshLayout.getContext())) {
                    if (openTuPics) {
                        getTuPicsData();
                    } else {
                        getDefaultData();
                    }
                } else {
                    setError();
                }

            }
        });
        mHitokotoInfo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) mHitokotoInfo.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(null, mHitokotoInfo.getText().toString().trim());
                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(clipData);
                    showToast("已复制");
                }
                return false;
            }
        });
        mRandomImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> strings = new ArrayList<>();
                strings.add(mImageUrl);
                PhotoBrowser.builder()
                        .setPhotos(strings)
                        .start(Objects.requireNonNull(getActivity()));
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mSwipeRefreshLayout.setRefreshing(true);
        openTuPics = SPUtil.build().getBoolean("open_tu_pics");
        if (NetworkUtil.isWifiConnected(Objects.requireNonNull(getContext()))) {
            if (openTuPics) {
                getTuPicsData();
            } else {
                getDefaultData();
            }
        } else {
            loadCache();
        }
    }

    private void getTuPicsData() {
        // Tujian
		// old API https://api.dpic.dev/ | new API https://v2.api.dailypics.cn/
        OkUtil.get()
                .url("https://v2.api.dailypics.cn/random?op=mobile")
                .execute(new ResultCallback<ArrayList<RandomPicture>>() {
                    @Override
                    public void onSuccess(ArrayList<RandomPicture> response) {
                        if (response != null && !response.isEmpty()) {
                            setTuPicsDate(response.get(0));
                        } else {
                            setError();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        setError();
                    }
                });
    }

    private void loadCache() {
        if (openTuPics) {
            String json = SPUtil.build().getString(RandomPicture.class.getName());
            if (!TextUtils.isEmpty(json)) {
                setTuPicsDate(GsonUtil.toObject(json, RandomPicture.class));
            } else {
                getTuPicsData();
            }
        } else {
            String json = SPUtil.build().getString(Hitokoto.class.getName());
            if (!TextUtils.isEmpty(json)) {
                setDefaultData(GsonUtil.toObject(json, Hitokoto.class));
            } else {
                getDefaultData();
            }
        }
    }

    private void setTuPicsDate(RandomPicture picture) {
        setRefreshFalse();
        // 文
        mHitokotoInfo.setVisibility(View.INVISIBLE);
        mHitokotoAuthor.setVisibility(View.INVISIBLE);
        mHitokotoSource.setVisibility(View.INVISIBLE);
        String text = picture.getP_content();
        if (!TextUtils.isEmpty(text)) {
            mHitokotoInfo.setVisibility(View.VISIBLE);
            mHitokotoInfo.setText(text);
        }
        String author = picture.getP_title();
        if (!TextUtils.isEmpty(author)) {
            mHitokotoAuthor.setVisibility(View.VISIBLE);
            mHitokotoAuthor.setText(author);
        }
//        String source = picture.getUsername();
//        if (!TextUtils.isEmpty(source)) {
//            mHitokotoSource.setVisibility(View.VISIBLE);
//            mHitokotoSource.setText(source);
//        }
        SPUtil.build().putString(RandomPicture.class.getName(), GsonUtil.toJson(picture));
        // 图
        mImageSource.setVisibility(View.VISIBLE);
        String pLink = picture.getLocal_url();
        if (TextUtils.isEmpty(pLink)) {
            mRandomImage.setEnabled(false);
        } else {
            mRandomImage.setEnabled(true);
            // 2560 * 1440
            int width = picture.getWidth();
            if (width > 1440) {
                mImageUrl = pLink + "?w=1080";
            } else {
                mImageUrl = pLink;
            }
            GlideApp.with(this)
                    .load(mImageUrl)
//                    .centerCrop
                    .into(mRandomImage);
        }
    }

    private void getDefaultData() {
        // 一言
        OkUtil.get()
                .url("https://api.lwl12.com/hitokoto/v1?encode=realjson")
                .setLoadDelay()
                .execute(new ResultCallback<Hitokoto>() {
                    @Override
                    public void onSuccess(Hitokoto response) {
                        setDefaultData(response);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        setError();
                    }
                });

        // 一图
        OkUtil.get()
                .url("https://acg.toubiec.cn/random?return=json")
                .execute(new ResultCallback<RandomImage>() {
                    @Override
                    public void onSuccess(RandomImage response) {
                        loadImage(response);
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }

    private void setDefaultData(Hitokoto hitokoto) {
        setRefreshFalse();
        mHitokotoInfo.setVisibility(View.INVISIBLE);
        mHitokotoAuthor.setVisibility(View.INVISIBLE);
        mHitokotoSource.setVisibility(View.INVISIBLE);
        mImageSource.setVisibility(View.GONE);
        if (hitokoto != null) {
            String text = hitokoto.getText();
            if (!TextUtils.isEmpty(text)) {
                mHitokotoInfo.setVisibility(View.VISIBLE);
                mHitokotoInfo.setText(text);
            }
            String author = hitokoto.getAuthor();
            if (!TextUtils.isEmpty(author)) {
                mHitokotoAuthor.setVisibility(View.VISIBLE);
                mHitokotoAuthor.setText(author);
            }
            String source = hitokoto.getSource();
            if (!TextUtils.isEmpty(source)) {
                mHitokotoSource.setVisibility(View.VISIBLE);
                mHitokotoSource.setText(source);
            }
            SPUtil.build().putString(Hitokoto.class.getName(), GsonUtil.toJson(hitokoto));
        }
    }

    private void loadImage(RandomImage randomImage) {
        if (randomImage != null) {
            String acgUrl = randomImage.getAcgurl();
            if (TextUtils.isEmpty(acgUrl)) {
                mRandomImage.setEnabled(false);
            } else {
                mRandomImage.setEnabled(true);
                mImageUrl = acgUrl;
                GlideApp.with(this)
                        .load(acgUrl)
                        .centerInside()
                        .into(mRandomImage);
            }
        }
    }

    /**
     * 设置异常提示
     */
    private void setError() {
        setRefreshFalse();
        showToast("在未知的边缘试探╮(╯▽╰)╭");
    }

    /**
     * 结束刷新
     */
    private void setRefreshFalse() {
        boolean refreshing = mSwipeRefreshLayout.isRefreshing();
        if (refreshing) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * 前往搜索
     */
    private void gotoSearch() {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        startActivity(intent);
    }

    /**
     * 一言
     */
    class Hitokoto {

        // 一言主体文本
        private String text;
        //  一言在原出处中的作者
        private String author;
        // 一言的来源
        private String source;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }
    }

    /**
     * 一图
     */
    class RandomImage {

        private String code;
        private String acgurl;
        private String width;
        private String height;
        private String size;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getAcgurl() {
            return acgurl;
        }

        public void setAcgurl(String acgurl) {
            this.acgurl = acgurl;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }
    }

    class RandomPicture {
        /**
         * PID : 9a8f807a-440e-11e9-8eca-f23c914b97eb
         * p_title : 地狱之刃：塞娜的献祭
         * p_content : Hellblade：Senua\'s Sacrifice
         * width : 7680
         * height : 4320
         * username : 绝对零º
         * p_link : https://ws1.sinaimg.cn/large/006N1muNgy1g0yccpxfwfj35xc3c04qu.jpg
         * local_url : https://img.dpic.dev/7bf3e60db3a1e08f0324b1b12f30da15
         * TID : e5771003-b4ed-11e8-a8ea-0202761b0892
         * p_date : 2019-03-12
         */
        private String PID;
        private String p_title;
        private String p_content;
        private int width;
        private int height;
        private String username;
        private String p_link;
        private String local_url;
        private String TID;
        private String p_date;

        public String getPID() {
            return PID;
        }

        public void setPID(String PID) {
            this.PID = PID;
        }

        public String getP_title() {
            return p_title;
        }

        public void setP_title(String p_title) {
            this.p_title = p_title;
        }

        public String getP_content() {
            return p_content;
        }

        public void setP_content(String p_content) {
            this.p_content = p_content;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getP_link() {
            return p_link;
        }

        public void setP_link(String p_link) {
            this.p_link = p_link;
        }

        public String getLocal_url() {
            return local_url;
        }

        public void setLocal_url(String local_url) {
            this.local_url = local_url;
        }

        public String getTID() {
            return TID;
        }

        public void setTID(String TID) {
            this.TID = TID;
        }

        public String getP_date() {
            return p_date;
        }

        public void setP_date(String p_date) {
            this.p_date = p_date;
        }
    }
}
