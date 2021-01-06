package com.example.aptapp.Parsing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptapp.R;

import java.io.Serializable;
import java.util.ArrayList;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.TextViewHolder> implements Serializable {
    private ArrayList<Subject> subjects = new ArrayList<>();

    class TextViewHolder extends RecyclerView.ViewHolder{
        TextView subjectNameView;
        TextView subjectTeacherView;
        TextView subjectTimeView;
        TextView subjectNumberView;
        TextView subjectAuditoriumView;

        public TextViewHolder(View itemView){
            super(itemView);
            subjectNameView = itemView.findViewById(R.id.subjectNameView);
            subjectTeacherView = itemView.findViewById(R.id.subjectTeacherView);
            subjectTimeView = itemView.findViewById(R.id.subjectTimeView);
            subjectNumberView = itemView.findViewById(R.id.subjectNumberView);
            subjectAuditoriumView = itemView.findViewById(R.id.subjectAuditoriumView);

        }

        public void bind(Subject subject){
            subjectNameView.setText(subject.subjectName);
            subjectTeacherView.setText(subject.subjectTeacher);
            subjectNumberView.setText(subject.subjectNumber);
            subjectAuditoriumView.setText(subject.subjectAuditorium);
            subjectTimeView.setText(subject.subjectTime);
        }
    }
    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_view, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextViewHolder holder, int position) {
        holder.bind(subjects.get(position));
    }

    @Override
    public int getItemCount() {
        return this.subjects.size();
    }

    public void setItems(ArrayList<Subject> subjects){
        this.subjects.addAll(subjects);
        notifyDataSetChanged();
    }

    public void clearItems(){
        this.subjects.clear();
        notifyDataSetChanged();
    }


}
