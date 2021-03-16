package com.rikhus.aptapp.Parsing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rikhus.aptapp.R;

import java.util.ArrayList;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.TextViewHolder>{
    private ArrayList<Subject> subjects = new ArrayList<>();

    class TextViewHolder extends RecyclerView.ViewHolder{
        TextView subjectNumberView;
        TextView subjectNameView;
        TextView firstSubgroupSubjectNameView;
        TextView secondSubgroupSubjectNameView;

        TextView subjectAuditoriumView;
        TextView firstSubgroupSubjectAuditoriumView;
        TextView secondSubgroupSubjectAuditoriumView;

        TextView subjectTeacherView;
        TextView firstSubgroupTeacherView;
        TextView secondSubgroupTeacherView;

        TextView subjectTimeView;

        View view;

        public TextViewHolder(View itemView){
            super(itemView);

            subjectNumberView = itemView.findViewById(R.id.subjectNumberView);
            subjectNameView = itemView.findViewById(R.id.subjectNameView);
            firstSubgroupSubjectNameView = itemView.findViewById(R.id.firstSubgroupSubjectNameView);
            secondSubgroupSubjectNameView = itemView.findViewById(R.id.secondSubgroupSubjectNameView);

            subjectAuditoriumView = itemView.findViewById(R.id.subjectAuditoriumView);
            firstSubgroupSubjectAuditoriumView = itemView.findViewById(R.id.firstSubgroupSubjectAuditoriumView);
            secondSubgroupSubjectAuditoriumView = itemView.findViewById(R.id.secondSubgroupSubjectAuditoriumView);

            subjectTeacherView = itemView.findViewById(R.id.teacherNameView);
            firstSubgroupTeacherView = itemView.findViewById(R.id.firstSubgroupTeacherNameView);
            secondSubgroupTeacherView = itemView.findViewById(R.id.secondSubgroupTeacherNameView);

            subjectTimeView = itemView.findViewById(R.id.subjectTimeView);
            view = itemView;
        }

        public void bind(Subject subject){
            // для обоих групп отдельно
            if (subject.getSubjectType() == Subject.SubjectType.FOR_ALL_SUBGROUPS_SEPARATELY){
                firstSubgroupSubjectNameView.setText(subject.getFirstSubgroupSubjectName());
                secondSubgroupSubjectNameView.setText(subject.getSecondSubgroupSubjectName());

                firstSubgroupSubjectAuditoriumView.setText(subject.getFirstSubgroupSubjectAuditorium());
                secondSubgroupSubjectAuditoriumView.setText(subject.getSecondSubgroupSubjectAuditorium());

                firstSubgroupTeacherView.setText(subject.getFirstSubgroupSubjectTeacher());
                secondSubgroupTeacherView.setText(subject.getSecondSubgroupSubjectTeacher());

                // если пара не онлайн
                if (!subject.getFirstSubgroupSubjectAuditorium().equals("on-line")){
                    firstSubgroupSubjectAuditoriumView.setText(view.getContext().getString(R.string.auditorium) + " "
                            + subject.getFirstSubgroupSubjectAuditorium());
                }
                else{
                    firstSubgroupSubjectAuditoriumView.setText(subject.getFirstSubgroupSubjectAuditorium());
                }
                // если пара не онлайн
                if (!subject.getSecondSubgroupSubjectAuditorium().equals("on-line")){
                    secondSubgroupSubjectAuditoriumView.setText(view.getContext().getString(R.string.auditorium) + " "
                            + subject.getSecondSubgroupSubjectAuditorium());
                }
                else{
                    secondSubgroupSubjectAuditoriumView.setText(subject.getSecondSubgroupSubjectAuditorium());
                }
            }
            // пустая
            else if (subject.getSubjectType() == Subject.SubjectType.EMPTY){
                    subjectNameView.setText("На этот день расписание отсутсвует");
                    return;
            }
            // для обоих подгрупп одна пара или же пара только у одной из подгрупп
            else{
                subjectNameView.setText(subject.getSubjectName());
                subjectTeacherView.setText(subject.getSubjectTeacher());
                // если пара не онлайн
                if (!subject.getSubjectAuditorium().equals("on-line")){
                    subjectAuditoriumView.setText(view.getContext().getString(R.string.auditorium) + " "
                            + subject.getSubjectAuditorium());
                }
                else{
                    subjectAuditoriumView.setText(subject.getSubjectAuditorium());
                }
            }
            subjectNumberView.setText(subject.getSubjectNumber() + " пара");
            subjectTimeView.setText(subject.getSubjectTimeStart() + " - " + subject.getSubjectTimeEnd());
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        switch (subjects.get(position).getSubjectType()){
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
