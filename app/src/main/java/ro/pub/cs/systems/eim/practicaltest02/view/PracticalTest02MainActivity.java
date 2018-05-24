package ro.pub.cs.systems.eim.practicaltest02.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ro.pub.cs.systems.eim.practicaltest02.R;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.model.AlarmInformation;
import ro.pub.cs.systems.eim.practicaltest02.network.ClientThread;
import ro.pub.cs.systems.eim.practicaltest02.network.ServerThread;

public class PracticalTest02MainActivity extends AppCompatActivity {

    // Server widgets
    private EditText serverPortEditText = null;
    private Button connectButton = null;

    // Client widgets
    private EditText clientAddressEditText = null;
    private EditText clientPortEditText = null;
    private EditText hourEditText = null;
    private EditText minuteEditText = null;
    private Button setButton = null;
    private Button resetButton = null;
    private Button pollButton = null;
    private TextView textView = null;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    private ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }

    }

    private class SetAlarmButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            String clientAddress = clientAddressEditText.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            String hour = hourEditText.getText().toString();
            String minute = minuteEditText.getText().toString();
            AlarmInformation alarmInformation = new AlarmInformation(hour, minute);
            serverThread.setData(clientAddress, alarmInformation);
//            if (hour == null || hour.isEmpty()
//                    || minute == null || minute.isEmpty()) {
//                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            textView.setText(Constants.EMPTY_STRING);
//
//            clientThread = new ClientThread(
//                    clientAddress, Integer.parseInt(clientPort), hour, minute, textView
//            );
//            clientThread.start();
        }
    }

    private class ResetAlarmButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String clientAddress = clientAddressEditText.getText().toString();
            serverThread.removeAlarm(clientAddress);
        }
    }

    private class PollAlarmButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            String clientAddress = clientAddressEditText.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            String hour = hourEditText.getText().toString();
            String minute = minuteEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();

            AlarmInformation alarmInformation = new AlarmInformation(hour, minute);
            if (hour == null || hour.isEmpty()
                    || minute == null || minute.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            textView.setText(Constants.EMPTY_STRING);

            clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort), hour, minute, textView
            );
            clientThread.start();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method has been invoked");
        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = (EditText)findViewById(R.id.server_port_edit_text);
        connectButton = (Button)findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonClickListener);

        clientAddressEditText = (EditText)findViewById(R.id.client_address_edit_text);
        clientPortEditText = (EditText)findViewById(R.id.client_port_edit_text);
        hourEditText = (EditText)findViewById(R.id.hour_edit_text);
        minuteEditText = (EditText)findViewById(R.id.minute_edit_text);
        setButton = (Button)findViewById(R.id.set_button);
        resetButton = (Button)findViewById(R.id.reset_button);
        pollButton = (Button)findViewById(R.id.poll_button);

        setButton.setOnClickListener(new SetAlarmButtonClickListener());
        resetButton.setOnClickListener(new ResetAlarmButtonClickListener());
        pollButton.setOnClickListener(new PollAlarmButtonClickListener());

        textView = (TextView)findViewById(R.id.text_view);
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }

}
