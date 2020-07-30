package com.example.rumi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rumi.R;
import com.example.rumi.models.Post;
import com.example.rumi.models.SurveyResponse;

import org.w3c.dom.Text;

import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {

    public static final String TAG = "MatchAdapter";
    private Context context;
    private List<SurveyResponse> responses;
    private Fragment fragment;

    public MatchAdapter(Context context, List<SurveyResponse> responses, Fragment fragment) {
        this.context = context;
        this.responses = responses;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recommendation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SurveyResponse response = responses.get(position);
        holder.bind(response);
    }

    @Override
    public int getItemCount() {
        return responses.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        responses.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAllReponses(List<SurveyResponse> responseList) {
        responses.addAll(responseList);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        TextView tvUserName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUserName = itemView.findViewById(R.id.tvUserName);
        }

        public void bind(SurveyResponse response) {
            if (!response.getImageUrl().equals("")) {
                Glide.with(context).load(response.getImageUrl()).into(ivProfileImage);
            }
        }
    }

}
