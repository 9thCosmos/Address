package com.lzy.addressselector;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.addressselector.bean.ISelectAble;

import java.util.List;


/**
 * Title: AddressAdapter <br>
 * @author LiZhengyu.
 */
public class AddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ISelectAble> datas;
    private int layoutType;
    private String selectedId = Selector.INDEX_INVALID;
    private OnAddressItemClickListener mOnAddressItemClickListener;

    public interface OnAddressItemClickListener{
        void onItemClick(int position);
    }

    public AddressAdapter(Context context,int layoutType,List<ISelectAble> datas){
        mContext = context;
        this.datas = datas;
        this.layoutType = layoutType;
    }

    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
    }

    public void setOnAddressItemClickListener(OnAddressItemClickListener onAddressItemClickListener) {
        this.mOnAddressItemClickListener = onAddressItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(layoutType == Constants.LAYOUT_GRID){
            View view = LayoutInflater.from(mContext).inflate(R.layout.address_selector_item_area_grid, parent, false);
            return new GridViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.address_selector_item_area, parent, false);
            return new LinearViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof LinearViewHolder){
            buildLinearView((LinearViewHolder)holder,position);
        }else if(holder instanceof GridViewHolder){
            buildGridView((GridViewHolder)holder,position);
        }

    }


    private void buildLinearView(LinearViewHolder viewHolder,final int position){
        ISelectAble item =datas.get(position);
        viewHolder.textView.setText(item.getName());

        boolean checked = selectedId != Selector.INDEX_INVALID && datas.get(position).getId() == selectedId;
        viewHolder.textView.setEnabled(!checked);
        viewHolder.imageViewCheckMark.setVisibility(checked ? View.VISIBLE : View.GONE);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnAddressItemClickListener != null){
                    mOnAddressItemClickListener.onItemClick(position);
                }
            }
        });
    }

    private void buildGridView(GridViewHolder viewHolder,final int position){
        ISelectAble item =datas.get(position);
        viewHolder.textView.setText(item.getName());

        boolean checked = selectedId != Selector.INDEX_INVALID && datas.get(position).getId() == selectedId;
        viewHolder.textView.setEnabled(!checked);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnAddressItemClickListener != null){
                    mOnAddressItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(datas != null){
            return datas.size();
        }
        return 0;
    }

    private class LinearViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public ImageView imageViewCheckMark;

        public LinearViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            imageViewCheckMark = (ImageView) itemView.findViewById(R.id.imageViewCheckMark);
        }
    }

    private class GridViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;

        public GridViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }

}
