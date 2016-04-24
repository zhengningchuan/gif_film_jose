package com.jose.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jose.gifmovie.R;
import com.jose.model.CommentsModel;

import java.util.List;


/**
 * Created by zhengningchuan on 16/4/22.
 */
public class CommentsListViewAdapter extends BaseAdapter {
    private List<CommentsModel> dataList;
    private Context context;

    public CommentsListViewAdapter(Context context, List<CommentsModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    class ViewHolder {
        ImageView comments_icon_iv;
        TextView comments_name_tv;
        TextView comments_content_tv;
    }

    @Override
    public int getCount() {
        if (dataList != null) {
            return dataList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.comments_listview_item_layout,null);
            viewHolder = new ViewHolder();
            viewHolder.comments_content_tv= (TextView) convertView.findViewById(R.id.comments_content_tv);
            viewHolder.comments_icon_iv = (ImageView) convertView.findViewById(R.id.comments_icon_iv);
            viewHolder.comments_name_tv = (TextView) convertView.findViewById(R.id.comments_name_tv);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.comments_icon_iv.setImageResource(R.drawable.adai);
        viewHolder.comments_name_tv.setText("R");
        viewHolder.comments_content_tv.setText("很好的电影~~~~");
        return convertView;
    }
}
