package com.barneswebb.android.tyshlerfootdisco.trainingrec;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.barneswebb.android.tyshlerfootdisco.R;

public class MyTrainingRecordActivity extends AppCompatActivity implements TrainingFragment.OnListFragmentInteractionListener  {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytrainingrecord);

        MyTrainingRecordActivity.this.setTitle(getString(R.string.trainingrecord_title));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MyTrainingRecordActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.trainingrecord_deleteall_title)
                        .setMessage(getString(R.string.trainingrecord_deleteall_mesg))
                        .setPositiveButton(R.string.trainingrecord_deleteall_delbtn, new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which)  {
                                ExerciseDataOpenHelper db = new ExerciseDataOpenHelper(MyTrainingRecordActivity.this);
                                db.delAllData();
                                MyTrainingRecordActivity.this.finish();
                            } })
                        .setNegativeButton(R.string.trainingrecord_deleteall_cancelbtn, null)
                        .show();
            }
                    //db.delAllSMSes();

        });
    }

    @Override
    public void onListFragmentInteraction(ExerciseSession item) {
        Log.d("ttsTraininRecAct", "wot goes 'ere?");
        //wot goes 'ere?
    }
}
