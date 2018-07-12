package bigdreams.example.alexbig.smartape.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import bigdreams.example.alexbig.smartape.R;
import bigdreams.example.alexbig.smartape.adapters.AnswerAdapter;
import bigdreams.example.alexbig.smartape.api.APIRequest;
import bigdreams.example.alexbig.smartape.database.viewmodels.AnswerViewModel;
import bigdreams.example.alexbig.smartape.database.viewmodels.QuestionViewModel;
import bigdreams.example.alexbig.smartape.database.viewmodels.QuizViewModel;
import bigdreams.example.alexbig.smartape.models.Answer;
import bigdreams.example.alexbig.smartape.models.Question;
import bigdreams.example.alexbig.smartape.models.Quiz;

import java.util.ArrayList;
import java.util.List;

import bigdreams.example.alexbig.smartape.adapters.AnswerAdapter;
import bigdreams.example.alexbig.smartape.api.APIRequest;
import bigdreams.example.alexbig.smartape.models.Answer;
import bigdreams.example.alexbig.smartape.models.Question;
import bigdreams.example.alexbig.smartape.models.Quiz;

public class QuizActivity extends AppCompatActivity{

    private List<Question> questions = new ArrayList<>();
    private List<Question> currentQuestions = new ArrayList<>();
    private TextView questionTextView;
    private AnswerViewModel answerViewModel;
    private AnswerAdapter answerAdapter;
    private int counter = 0;


    @Override
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        setContentView(bigdreams.example.alexbig.smartape.R.layout.layout_answer_question);

        QuizViewModel quizViewModel = ViewModelProviders.of(this).get(QuizViewModel.class);
        QuestionViewModel questionViewModel = ViewModelProviders.of(this).get(QuestionViewModel.class);
        answerViewModel = ViewModelProviders.of(this).get(AnswerViewModel.class);
        APIRequest apiRequest = new APIRequest(this, quizViewModel);

        Intent intent = getIntent();
        Quiz quiz = (Quiz)intent.getSerializableExtra("QUIZ");
        questions = quiz.getQuestions();
        currentQuestions = new ArrayList<>();
        for (Question q:questions){
            Question question = new Question();
            question.setPremise(q.getPremise());
            List<Answer> answers = new ArrayList<>();
            for (Answer a: q.getAnswers()){
                System.out.println("ANSWER "+a.isCorrect());
                Answer answer = new Answer();
                answer.setText(a.getText());
                answers.add(answer);
            }
            question.setAnswers(answers);
            question.setType(q.getType());
            currentQuestions.add(question);
        }
        questionViewModel.setQuestions(currentQuestions);

        questionTextView = findViewById(bigdreams.example.alexbig.smartape.R.id.textView_answerQuestion_question);
        RecyclerView recyclerView = findViewById(bigdreams.example.alexbig.smartape.R.id.recyclerView_answerQuestion_answers);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        answerAdapter = new AnswerAdapter(this) {
            @Override
            public void onItemClick(View v, int position) {

            }
        };
        answerAdapter.setAnswerList(new ArrayList<>());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(answerAdapter);
        recyclerView.setHasFixedSize(true);

        Button previous = findViewById(bigdreams.example.alexbig.smartape.R.id.button_answerQuestion_previousQuestion);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter--;
                if (counter >= 0){
                    setQuestion(counter);
                }else{
                    counter = 0;
                }
            }
        });

        Button next = findViewById(bigdreams.example.alexbig.smartape.R.id.button_answerQuestion_nextQuestion);
        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                counter++;
                if (counter < questions.size()){
                    setQuestion(counter);
                }else{
                    counter = questions.size()-1;
                }
            }
        });

        Button finish = findViewById(bigdreams.example.alexbig.smartape.R.id.button_answerQuestion_finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateResult();
            }
        });

        questionViewModel.getQuestions().observe(this, new Observer<List<Question>>() {
            @Override
            public void onChanged(@Nullable List<Question> questionList) {
                currentQuestions = questionList;
            }
        });

        answerViewModel.getAnswers().observe(this, new Observer<List<Answer>>() {
            @Override
            public void onChanged(@Nullable List<Answer> answers) {
                answerAdapter.setAnswerList(answers);
            }
        });
        setQuestion(0);
    }

    private void setQuestion(int counter){
        questionTextView.setText(currentQuestions.get(counter).getPremise());
        answerViewModel.setAnswers(currentQuestions.get(counter).getAnswers());
    }

    private void calculateResult(){
        boolean correct;
        int numCorrect = 0;
        int counter = questions.size();

        for (int i=0; i<questions.size(); i++){
            Question question = questions.get(i);
            Question myQuestion = currentQuestions.get(i);
            correct = true;

            for (int j=0; j<question.getAnswers().size(); j++){
                Answer answer = question.getAnswers().get(j);
                Answer myAnswer = myQuestion.getAnswers().get(j);
                if (answer.isCorrect() != myAnswer.isCorrect()){
                    correct = false;
                }
            }
            if (correct){
                numCorrect++;
            }
        }

        float grade = (numCorrect*10)/counter;
        System.out.println("CORRECT "+numCorrect+" OUT OF "+counter);
        System.out.println("GRADE "+grade);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(bigdreams.example.alexbig.smartape.R.string.text_result);
        builder.setMessage(getString(bigdreams.example.alexbig.smartape.R.string.text_correct)+": "+numCorrect+"/"+counter+"\n"+getString(bigdreams.example.alexbig.smartape.R.string.text_grade)+": "+grade);
        builder.setPositiveButton(bigdreams.example.alexbig.smartape.R.string.text_ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent data = new Intent();
                data.putExtra("GRADE", ""+grade);
                setResult(1,data);
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}