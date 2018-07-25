package de.kolatanet.networkwatcher;

import android.content.Intent;
import android.net.TrafficStats;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Option extends AppCompatActivity implements View.OnClickListener {

    private static long RX_INIT;

    private static long TX_INIT;

    TextView recieved;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        recieved = findViewById(R.id.txt_recieved);
        button = findViewById(R.id.button);
        button.setOnClickListener(this);
        startService();
    }

    private void startService() {
        Intent intent = new Intent(this, TrafficWatcher.class);
        intent.putExtra("keep", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        TrafficWatcher.bindManager(this);
        startService(intent);
    }


    @Override
    public void onClick(View v) {
        recieved.setText(String.valueOf(getDifference()));
        init();

    }

    private void init() {
        RX_INIT = TrafficStats.getMobileRxBytes();
        TX_INIT = TrafficStats.getMobileTxBytes();
    }

    private long getDifference() {
        long result = TrafficStats.getMobileRxBytes() - RX_INIT;
        result += TrafficStats.getMobileTxBytes() - TX_INIT;
        return result / 1000;
    }
}
