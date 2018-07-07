package com.example.alexbig.smartape.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.alexbig.smartape.R;
import com.example.alexbig.smartape.database.viewmodels.QuizViewModel;
import com.example.alexbig.smartape.models.Quiz;

public class CreateQuizActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_create_quiz);

        final EditText titleEditText = findViewById(R.id.editText_createQuiz_quizName);
        final EditText descriptionEditText = findViewById(R.id.editText_createQuiz_quizDescription);
        final Spinner categorySpinner = findViewById(R.id.spinner_createQuiz_quizCategory);
        Button createButton = findViewById(R.id.button_createQuiz);

        final Quiz quiz = new Quiz();
        final QuizViewModel quizViewModel = ViewModelProviders.of(this).get(QuizViewModel.class);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quiz.setTitle(titleEditText.getText().toString());
                quiz.setDescription(descriptionEditText.getText().toString());
                quiz.setCategory(categorySpinner.getSelectedItem().toString());
                quiz.setCreator("USER");
                quizViewModel.insertQuiz(quiz);
                finish();
            }
        });
    }
}