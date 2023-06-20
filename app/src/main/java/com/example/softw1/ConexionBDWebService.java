package com.example.softw1;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ConexionBDWebService extends Worker {
    public ConexionBDWebService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
      //  calendarEventsUpdate();
        return Result.success();
    }

   /* private void calendarEventsUpdate(){
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionServidorDAS.class).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            TextView textViewResult = findViewById(R.id.textoResultado);
                            textViewResult.setText(workInfo.getOutputData().getString("datos"));
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }*/

}
