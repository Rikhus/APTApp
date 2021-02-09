package com.example.aptapp.Parsing;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptapp.R;

import java.io.Serializable;
import java.util.ArrayList;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.TextViewHolder>{
    private ArrayList<Subject> subjects = new ArrayList<>();

    class TextViewHolder extends RecyclerView.ViewHolder{
        TextView subjectNameView;
        TextView firstSubgroupSubjectNameView;
        TextView secondSubgroupSubjectNameView;

        TextView subjectAuditoriumView;
        TextView firstSubgroupSubjectAuditoriumView;
        TextView secondSubgroupSubjectAuditoriumView;

        TextView subjectTimeViewStart;
        TextView subjectTimeViewEnd;
        TextView subjectDash;
        View view;

        public TextViewHolder(View itemView){
            super(itemView);
            subjectNameView = itemView.findViewById(R.id.subjectNameView);
            firstSubgroupSubjectNameView = itemView.findViewById(R.id.firstSubgroupSubjectNameView);
            secondSubgroupSubjectNameView = itemView.findViewById(R.id.secondSubgroupSubjectNameView);

            subjectAuditoriumView = itemView.findViewById(R.id.subjectAuditoriumView);
            firstSubgroupSubjectAuditoriumView = itemView.findViewById(R.id.firstSubgroupSubjectAuditoriumView);
            secondSubgroupSubjectAuditoriumView = itemView.findViewById(R.id.secondSubgroupSubjectAuditoriumView);

            subjectTimeViewStart = itemView.findViewById(R.id.subjectTimeStart);
            subjectTimeViewEnd = itemView.findViewById(R.id.subjectTimeEnd);
            subjectDash = itemView.findViewById(R.id.subjectDash);
            view = itemView;
        }

        public void bind(Subject subject){
            if (subject.subjectType == Subject.SubjectType.FOR_ALL_SUBGROUPS_SEPARATELY){
                firstSubgroupSubjectNameView.setText(subject.firstSubgroupSubjectName);
                secondSubgroupSubjectNameView.setText(subject.secondSubgroupSubjectName);

                firstSubgroupSubjectAuditoriumView.setText(subject.firstSubgroupSubjectAuditorium);
                secondSubgroupSubjectAuditoriumView.setText(subject.secondSubgroupSubjectAuditorium);

                // если пара не онлайн
                if (!subject.firstSubgroupSubjectAuditorium.equals("on-line")){
                    firstSubgroupSubjectAuditoriumView.setText(view.getContext().getString(R.string.auditorium) + " "
                            + subject.firstSubgroupSubjectAuditorium);
                }
                else{
                    firstSubgroupSubjectAuditoriumView.setText(subject.firstSubgroupSubjectAuditorium);
                }
                // если пара не онлайн
                if (!subject.secondSubgroupSubjectAuditorium.equals("on-line")){
                    secondSubgroupSubjectAuditoriumView.setText(view.getContext().getString(R.string.auditorium) + " "
                            + subject.secondSubgroupSubjectAuditorium);
                }
                else{
                    secondSubgroupSubjectAuditoriumView.setText(subject.secondSubgroupSubjectAuditorium);
                }
            }
            else if (subject.subjectType == Subject.SubjectType.EMPTY){
                    subjectNameView.setText("На этот день расписание отсутсвует");
                    return;
            }
            else{
                subjectNameView.setText(subject.subjectName);
                // если пара не онлайн
                if (!subject.subjectAuditorium.equals("on-line")){
                    subjectAuditoriumView.setText(view.getContext().getString(R.string.auditorium) + " "
                            + subject.subjectAuditorium);
                }
                else{
                    subjectAuditoriumView.setText(subject.subjectAuditorium);
                }
            }

            subjectTimeViewStart.setText(subject.subjectTimeStart);
            subjectTimeViewEnd.setText(subject.subjectTimeEnd);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        switch (subjects.get(position).subjectType){
            case FOR_ALL_SUBGROUPS:
                return 0;
            case FOR_FIRST_SUBGROUP_ONLY:
                return 1;
            case FOR_SECOND_SUBGROUP_ONLY:
                return 2;
            case FOR_ALL_SUBGROUPS_SEPARATELY:
                return 3;
            case EMPTY:
                return 4;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int item_view = 0;
        // выбираем item_view в зависимости от типа пары (для обоих подгрупп, для одной из них и т.д)
        switch (viewType){
            case 0:
                item_view = R.layout.subject_view;
                break;
            case 1:
                item_view = R.layout.subject_view_for_first_subgroup;
                break;
            case 2:
                item_view = R.layout.subject_view_for_second_subgroup;
                break;
            case 3:
                item_view = R.layout.subject_view_for_two_subgroups;
                break;
            case 4:
                item_view = R.layout.subject_empty_view;
                break;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(item_view, parent, false);
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
