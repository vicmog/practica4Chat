package com.example.practica4chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private Button btSend,btSalir;
    private TextView tvText;
    private EditText etMessage;
    private Socket cliente;
    private DataInputStream flujoE;
    private DataOutputStream flujoS;
    private Thread listeningThread;
    public boolean run = true;
    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        btSend = findViewById(R.id.btSend);
        btSalir = findViewById(R.id.btSalir);
        etMessage = findViewById(R.id.etMessage);
        tvText = findViewById(R.id.tvText);

        btSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(){
                    @Override
                    public void run() {
                        sendText("Abandonó el chat");
                    }
                }.start();
                try {
                    flujoE.close();
                    flujoS.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.exit(0);

            }
        });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textbt = etMessage.getText().toString();
                new Thread(){
                    @Override
                    public void run() {
                        sendText(textbt);
                    }
                }.start();
                etMessage.setText("");

            }
        });
        Thread thread = new Thread(){
            @Override
            public void run() {
                startClient("10.0.2.2",5000);

            }
        };
        thread.start();
    }

    private void sendText(String text) {
        try {
            flujoS.writeUTF(text);

        } catch (IOException ex) {
            run = false;
        }


    }


    private void startClient(String host ,int port) {

        try {
            cliente = new Socket(host,port);
            flujoE = new DataInputStream(cliente.getInputStream());
            flujoS = new DataOutputStream(cliente.getOutputStream());
            listeningThread = new Thread(){

                public void run(){

                    while(run){
                        try {
                            text = flujoE.readUTF();
                            if(text.compareTo("/clear")==0){

                                tvText.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvText.setText("");
                                    }
                                });

                            }else {
                                tvText.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvText.append(text+"\n");
                                    }
                                });

                            }


                        } catch (IOException ex) {
                            run = false;
                        }

                    }
                }

            };
            listeningThread.start();

        } catch (IOException ex) {
            System.out.println("Catch START CLIENT"+ex.getLocalizedMessage());
            System.exit(0);
            run = false;
        }

    }
}