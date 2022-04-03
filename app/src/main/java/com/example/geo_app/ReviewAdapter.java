package com.example.geo_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.Viewholder> {

    private Context context;
    private ArrayList<ReviewModel> reviewModelArrayList;

    // Constructor
    public ReviewAdapter(Context context, ArrayList<ReviewModel> reviewModelArrayList) {
        this.context = context;
        this.reviewModelArrayList = reviewModelArrayList;
    }

    @NonNull
    @Override
    public ReviewAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_card, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        ReviewModel model = reviewModelArrayList.get(position);
        holder.questionTV.setText(model.getQuestion());
        holder.option1TV.setText(model.getOption1());
        holder.option2TV.setText(model.getOption2());
        holder.option3TV.setText(model.getOption3());
        holder.option4TV.setText(model.getOption4());

        //for flag or map
        //holder.questionIV.setImageResource(model.getCourse_image());
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return reviewModelArrayList.size();
    }

    // View holder class for initializing of
    // your views such as TextView and Imageview.
    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView questionIV;
        private TextView questionTV, option1TV, option2TV, option3TV, option4TV;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            questionIV = itemView.findViewById(R.id.question_iv);
            questionTV = itemView.findViewById(R.id.question_tv);
            option1TV = itemView.findViewById(R.id.option1_tv);
            option2TV = itemView.findViewById(R.id.option2_tv);
            option3TV = itemView.findViewById(R.id.option3_tv);
            option3TV = itemView.findViewById(R.id.option4_tv);
        }
    }
}
