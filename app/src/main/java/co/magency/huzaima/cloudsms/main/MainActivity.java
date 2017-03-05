package co.magency.huzaima.cloudsms.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import co.magency.huzaima.cloudsms.ConversationListService;
import co.magency.huzaima.cloudsms.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(getApplicationContext(), ConversationListService.class));
    }
}
