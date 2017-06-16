package com.blueprint.du.okh;

import android.os.Environment;
import android.widget.Toast;

import com.blueprint.LibApp;
import com.blueprint.rx.RxUtill;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @another 江祖赟
 * @date 2017/6/14.
 */
public class MultipartHelper {

    private static final long DEFAULT_TIMEOUT = 15;
    private String mDownloadUrl;
    private String mSaveName;
    private ProgressListener mProgressListener;
    private long mStartsPoint;
    private final File mDestFile;
    private Disposable mSubscribe;

    public MultipartHelper(DownLoadEntity downLoadEntity, ProgressListener progressListener){
        mDownloadUrl = downLoadEntity.url;
        mSaveName = downLoadEntity.saveName;
        mDestFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), mSaveName);
        mProgressListener = progressListener;
    }

    private OkHttpClient getProgressClient(Interceptor interceptor){
        // 拦截器，用上ProgressResponseBody
        return new OkHttpClient.Builder().addNetworkInterceptor(interceptor).retryOnConnectionFailure(true)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS).build();
    }

    public void download(){
        //        Request request = new Request.Builder().url(mDownloadUrl).build();
        //        Call call = getProgressClient(new DownloadProgressInterceptor(mProgressListener)).newCall(request);
        //
        //        Single.just(call).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe();
        mSubscribe = getRetrofit(new DownloadProgressInterceptor(mProgressListener)).create(MultipartService.class)
                .download(mDownloadUrl).compose(RxUtill.<ResponseBody>all_io_single()).doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception{
                        mProgressListener.onCancel();
                        System.out.println("--------------断开了");
                    }
                }).subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(@NonNull ResponseBody responseBody) throws Exception{
                        save(responseBody, mStartsPoint);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception{
                        mProgressListener.onFailure();
                    }
                });
    }

    public void downloadFrom(long startPoing){
        mStartsPoint = startPoing;
        mSubscribe = getRetrofit(new DownloadProgressInterceptor(mProgressListener)).create(MultipartService.class)
                .downloadRange(mDownloadUrl, String.valueOf(startPoing)).compose(RxUtill.<ResponseBody>all_io_single())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(@NonNull ResponseBody responseBody) throws Exception{
                        save(responseBody, mStartsPoint);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception{
                        mProgressListener.onFailure();
                    }
                });
    }

    public void pause(){
        cancel();
    }

    public void cancel(){
        if(mSubscribe != null) {
            mSubscribe.dispose();
        }
    }

    private void save(ResponseBody body, long startsPoint){
        InputStream in = body.byteStream();
        FileChannel channelOut = null;
        // 随机访问文件，可以指定断点续传的起始位置
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(mDestFile, "rwd");
            //Chanel NIO中的用法，由于RandomAccessFile没有使用缓存策略，直接使用会使得下载速度变慢，亲测缓存下载3.3秒的文件，用普通的RandomAccessFile需要20多秒。
            channelOut = randomAccessFile.getChannel();
            // 内存映射，直接使用RandomAccessFile，是用其seek方法指定下载的起始位置，使用缓存下载，在这里指定下载位置。
            MappedByteBuffer mappedBuffer = channelOut
                    .map(FileChannel.MapMode.READ_WRITE, startsPoint, body.contentLength());
            byte[] buffer = new byte[1024];
            int len;
            while(( len = in.read(buffer) ) != -1) {
                mappedBuffer.put(buffer, 0, len);
            }
        }catch(IOException e) {
            e.printStackTrace();
        }finally {
            try {
                in.close();
                if(channelOut != null) {
                    channelOut.close();
                }
                if(randomAccessFile != null) {
                    randomAccessFile.close();
                }
            }catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Single getUplodFileSingle(String upUrl, File file){
        if(file != null && file.exists()) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("application/otcet-stream"), file);

            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            return getRetrofit(new UploadProgressInterceptor(mProgressListener)).create(MultipartService.class)
                    .upload(upUrl, body).compose(RxUtill.all_io_single());

        }

        return Single.error(new RuntimeException("文件不存在"));
    }

    public void uploadFiles(String upUrl, File file){
        getUplodFileSingle(upUrl, file).subscribe(new Consumer() {
            @Override
            public void accept(@NonNull Object o) throws Exception{

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception{
                mProgressListener.onFailure();
            }
        });
    }

    public void uploadFiles(String ulr, List<String> imagesList){
        if(imagesList.size() == 0) {
            Toast.makeText(LibApp.getContext(), "不能不选择图片", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String,RequestBody> files = new HashMap<>();

        MultipartService multipartService = getRetrofit(new UploadProgressInterceptor(mProgressListener))
                .create(MultipartService.class);

        for(int i = 0; i<imagesList.size(); i++) {
            File file = new File(imagesList.get(i));
            files.put("file"+i+"\"; filename=\""+file.getName(),
                    RequestBody.create(MediaType.parse("application/otcet-stream"), file));
        }
        multipartService.upload(ulr, files).compose(RxUtill.defaultSchedulers_single()).subscribe(new Consumer() {
            @Override
            public void accept(@NonNull Object o) throws Exception{

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception{
                mProgressListener.onFailure();
            }
        });
    }

    public Retrofit getRetrofit(Interceptor interceptor){
        return new Retrofit.Builder().client(getProgressClient(interceptor)).baseUrl("http://www.gank.io/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
    }

}