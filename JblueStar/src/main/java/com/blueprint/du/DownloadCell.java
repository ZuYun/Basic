package com.blueprint.du;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Pair;

import com.blueprint.LibApp;
import com.blueprint.du.okh.ProgressListener;
import com.blueprint.helper.LogHelper;

import java.io.File;

import static com.blueprint.du.DownloadCell.DownloadStates.D_STATE_DEFAULT;

/**
 * @another 江祖赟
 * @date 2017/6/17.
 */
public class DownloadCell {

    public static interface DownloadStates {
        int D_STATE_DEFAULT = -10;
        int D_STATE_WAIGHT = 0;
        int D_STATE_SUCCEED = 1;
        int D_STATE_FAIL = 2;
        int D_STATE_CANCEL = 3;
        int D_STATE_PAUSE = 4;
        int D_STATE_DOWNLOADING = 4;
    }

    private String downUrl;
    private String savePath;
    private String saveName;
    private String downloadID;
    private long fileSize;
    /**
     * 设置通知栏样式
     */
    private String extra1;
    private String extra2;
    private String extra3;
    private String version;
    private int mDownloadState = D_STATE_DEFAULT;
    private Pair mPair;
    private ProgressListener mProgressListener;


    public DownloadCell(String downUrl, String savePath, String saveName, String downloadID, long fileSize, String extra1, String extra2, String extra3, String version, Pair pair, ProgressListener progressListener){
        this.downUrl = downUrl;
        this.savePath = savePath;
        this.saveName = saveName;
        this.downloadID = downloadID;
        this.fileSize = fileSize;
        this.extra1 = extra1;
        this.extra2 = extra2;
        this.extra3 = extra3;
        this.version = version;
        mProgressListener = progressListener;
        mPair = pair;
    }

    public ProgressListener getProgressListener(){
        return mProgressListener;
    }

    public void setProgressListener(ProgressListener progressListener){
        mProgressListener = progressListener;
    }

    public int getDownloadState(){
        return mDownloadState;
    }

    public void setDownloadState(int downloadState){
        mDownloadState = downloadState;
    }

    public String getDownUrl(){
        return downUrl;
    }

    public void setDownUrl(String downUrl){
        this.downUrl = downUrl;
    }

    public String getSavePath(){
        return savePath;
    }

    public File getDestFile(){
        return new File(savePath, saveName);
    }

    public void setSavePath(String savePath){
        this.savePath = savePath;
    }

    public String getSaveName(){
        return saveName;
    }

    public void setSaveName(String saveName){
        this.saveName = saveName;
    }

    public String getDownloadID(){
        return downloadID;
    }

    public void setDownloadID(String downloadID){
        this.downloadID = downloadID;
    }

    public long getFileSize(){
        return fileSize;
    }

    public void setFileSize(long fileSize){
        this.fileSize = fileSize;
    }

    public String getExtra1(){
        return extra1;
    }

    public void setExtra1(String extra1){
        this.extra1 = extra1;
    }

    public String getExtra2(){
        return extra2;
    }

    public void setExtra2(String extra2){
        this.extra2 = extra2;
    }

    public String getExtra3(){
        return extra3;
    }

    public void setExtra3(String extra3){
        this.extra3 = extra3;
    }

    public String getVersion(){
        return version;
    }

    public void setVersion(String version){
        this.version = version;
    }

    public Pair getPair(){
        return mPair;
    }

    public void setPair(Pair pair){
        mPair = pair;
    }

    public static class DownloadCellBuilder {
        private String mDownUrl;
        private String mSavePath;
        private String mSaveName;
        private String mDownloadID;
        private long mFileSize;
        private String mExtra1;
        private String mExtra2;
        private String mExtra3;
        private Pair mPair;
        private String mVersion;
        private ProgressListener mProgressListener;

        public DownloadCellBuilder downUrl(String downUrl){
            mDownUrl = downUrl;
            return this;
        }

        public DownloadCellBuilder savePath(String savePath){
            mSavePath = savePath;
            return this;
        }

        public DownloadCellBuilder saveName(String saveName){
            mSaveName = saveName;
            return this;
        }

        public DownloadCellBuilder downloadID(String downloadID){
            mDownloadID = downloadID;
            return this;
        }

        public DownloadCellBuilder fileSize(long fileSize){
            mFileSize = fileSize;
            return this;
        }

        public DownloadCellBuilder extra1(String extra1){
            mExtra1 = extra1;
            return this;
        }

        public DownloadCellBuilder extra2(String extra2){
            mExtra2 = extra2;
            return this;
        }

        public DownloadCellBuilder extra3(String extra3){
            mExtra3 = extra3;
            return this;
        }

        public DownloadCellBuilder setPair(Pair pair){
            mPair = pair;
            return this;
        }

        public DownloadCellBuilder setVersion(String version){
            mVersion = version;
            return this;
        }

        public void progressListener(ProgressListener progressListener){
            mProgressListener = progressListener;
        }

        public DownloadCell build(){
            if(TextUtils.isEmpty(mSavePath)) {
                mSavePath = LibApp.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                LogHelper.slog_e("DownloadCell", "没有设置下载地址 使用默认地址");
            }
            if(TextUtils.isEmpty(mSaveName)) {
                mSaveName = "default.d";
                LogHelper.slog_e("DownloadCell", "没有设置文件名字 使用默认");
            }
            return new DownloadCell(mDownUrl, mSavePath, mSaveName, mDownloadID, mFileSize, mExtra1, mExtra2, mExtra3,
                    mVersion, mPair, mProgressListener);
        }
    }
}
