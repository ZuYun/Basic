package com.zuyun.blueprint.vp.workshop.recommend.itemholderbinder;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zuyun.blueprint.vp.workshop.recommend.itemholderbinder.bean.ItemLoopImage;
import com.blueprint.widget.LoopImagePager;
import com.blueprint.adapter.RecyclerHolder;
import com.zuyun.blueprint.R;

import me.drakeet.multitype.ItemViewBinder;

/**
 * @author 江祖赟.
 * @date 2017/6/8
 * @des [一句话描述]
 */
public class ItemLoopImageBinder extends ItemViewBinder<ItemLoopImage,RecyclerHolder> {
    @NonNull
    @Override
    protected RecyclerHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent){
        View rootView = inflater.inflate(R.layout.recv_item_recom_loopimage, null);
        //        View rootView = inflater.inflate(R.layout.recv_item_recom_msgcard, null);
        return new RecyclerHolder(rootView);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerHolder holder, @NonNull ItemLoopImage item){
        LoopImagePager view = holder.getView(R.id.lip_recv_item_lipager);
        view.setPagerData(item.getImageUrls());
    }
}
