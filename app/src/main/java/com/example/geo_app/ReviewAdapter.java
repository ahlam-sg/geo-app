package com.example.geo_app;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.itemViewHolder> {

    Context context;
    private ArrayList<ReviewModel> reviewModelArrayList;

    class itemViewHolder extends RecyclerView.ViewHolder{
        ImageView questionIV;
        TextView questionTV, option1TV, option2TV, option3TV, option4TV;
        ArrayList<TextView> optionsTV = new ArrayList<>();
        public itemViewHolder(View itemView) {
            super(itemView);
            questionIV =  itemView.findViewById(R.id.question_iv);
            questionTV = itemView.findViewById(R.id.question_tv);
            option1TV = itemView.findViewById(R.id.option1_tv);
            option2TV = itemView.findViewById(R.id.option2_tv);
            option3TV = itemView.findViewById(R.id.option3_tv);
            option4TV = itemView.findViewById(R.id.option4_tv);
            optionsTV.clear();
            optionsTV = new ArrayList<>(Arrays.asList(option1TV, option2TV, option3TV, option4TV));
        }
    }

    //constructor
    public ReviewAdapter(Context context, ArrayList<ReviewModel> list){
        this.context=context;
        reviewModelArrayList = list;
    }

    @Override
    public itemViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_card, parent, false);

        itemViewHolder itemViewHolder = new itemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(itemViewHolder holder, int position) {
        holder.questionTV.setText(reviewModelArrayList.get(position).getQuestion());
        holder.option1TV.setText(reviewModelArrayList.get(position).getOption1());
        holder.option2TV.setText(reviewModelArrayList.get(position).getOption2());
        holder.option3TV.setText(reviewModelArrayList.get(position).getOption3());
        holder.option4TV.setText(reviewModelArrayList.get(position).getOption4());

        //for flag or map
        holder.questionIV.setImageResource(R.drawable.lu_flag);

        setTextColor(holder, position);
    }

    public void setTextColor(itemViewHolder holder, int position){
        int selectedIndex = reviewModelArrayList.get(position).getSelectedOptionIndex();
        int correctIndex = reviewModelArrayList.get(position).getCorrectOptionIndex();
        //correct => green
        if (selectedIndex == correctIndex){
            holder.optionsTV.get(selectedIndex).setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.green)));
            Log.i("ReviewAdapter", "setTextColor: correct, selIndex: "+selectedIndex + ", corIndex: "+ correctIndex);
        }
        //incorrect => red
        else {
            holder.optionsTV.get(correctIndex).setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.green)));
            holder.optionsTV.get(selectedIndex).setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.red)));
            Log.i("ReviewAdapter", "setTextColor: incorrect, selIndex: "+selectedIndex + ", corIndex: "+ correctIndex);
        }
    }

    @Override
    public int getItemCount() {
        return reviewModelArrayList.size();
    }
}