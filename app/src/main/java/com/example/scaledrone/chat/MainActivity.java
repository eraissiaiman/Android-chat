package com.example.scaledrone.chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;

import static java.lang.Thread.*;

public class MainActivity extends AppCompatActivity implements RoomListener {
    // LOCAL VARIABLES
    private String UserName;
    private int UserAge;
    // replace this with a real channelID from Scaledrone dashboard
    private String channelID = "sRszdeJ05pYXTNAn"; // dir id dialk hh ntestiw run lol
    private String roomName = "observable-room";
    private EditText editText;
    private Scaledrone Botchan;
    private Scaledrone scaledrone;
    private MessageAdapter messageAdapter;
    private ListView messagesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);

        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        MemberData data = new MemberData(getRandomName(), getRandomColor());
        scaledrone = new Scaledrone(channelID, data);
        scaledrone.connect(new Listener() {
            @Override
            public void onOpen() {
                System.out.println("Scaledrone connection open");
                scaledrone.subscribe(roomName, MainActivity.this);
                if(Steps.step == 0)
                    scaledrone.publish(roomName,"Bot: Merci d'écrire votre nom.");
            }

            @Override
            public void onOpenFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onClosed(String reason) {
                System.err.println(reason);
            }
        });
    }

    public void sendMessage(View view) throws InterruptedException {
        String message = editText.getText().toString();
        final String answer = message;
        if (message.length() > 0) {
            // VERIFICATIONS
            switch(Steps.step){
                case 0:
                    this.UserName = message;
                    Steps.step += 1;
                    scaledrone.publish(roomName, "Vous: " +message);
                    editText.getText().clear();
                    try {
                        Thread.sleep(1400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    scaledrone.publish(roomName,"Bot: Bonjour "+this.UserName+", merci de nous indiquer votre âge.");
                    break;
                case 1:
                    try {
                    this.UserAge = Integer.parseInt(message);
                    Steps.step += 1;
                    scaledrone.publish(roomName, "Vous: " +message);
                    editText.getText().clear();

                        Thread.sleep(1400);
                    } catch (InterruptedException e) {
                        scaledrone.publish(roomName,"Bot: Fromat non validé...");
                        break;
                    }
                    scaledrone.publish(roomName,"Bot: Bonjour "+this.UserName+", merci de nous indiquer votre âge.");
                    break;
                default:
                    scaledrone.publish(roomName, "Vous: " +message);
                    editText.getText().clear();
                    try {
                        Thread.sleep(1400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    scaledrone.publish(roomName,"Bot: Bonjour "+this.UserName+", merci de nous indiquer votre âge.");
            }
            if(Steps.step == 0) {
                this.UserName = message;
                Steps.step += 1;
                scaledrone.publish(roomName, "Vous: " +message);
                editText.getText().clear();
                try {
                    Thread.sleep(1400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                scaledrone.publish(roomName,"Bot: Bonjour "+this.UserName+", merci de nous indiquer votre âge.");
            }
           /* try {
                Thread.sleep(1400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            switch(answer.toUpperCase()){
                case "BONJOUR":
                    scaledrone.publish(roomName, "Bot: Bonjour, comment puis-je vous aider?");

                    break;
                case "ANA MRID":
                    scaledrone.publish(roomName, "Bot: wa ana mali hh");

                    break;
                default:
                    scaledrone.publish(roomName, "Bot: Désolé, notre système n'a pas compris ce que vous voulez dire!");

            }
         editText.getText().clear();*/


        }
    }

    @Override
    public void onOpen(Room room) {
        System.out.println("Conneted to room");
    }

    @Override
    public void onOpenFailure(Room room, Exception ex) {
        System.err.println(ex);
    }

    @Override
    public void onMessage(Room room, com.scaledrone.lib.Message receivedMessage) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final MemberData data = mapper.treeToValue(receivedMessage.getMember().getClientData(), MemberData.class);
            boolean belongsToCurrentUser = receivedMessage.getClientID().equals(scaledrone.getClientID());
            final Message message = new Message(receivedMessage.getData().asText(), data, belongsToCurrentUser);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.add(message);
                    messagesView.setSelection(messagesView.getCount() - 1);
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private String getRandomName() {
        return "Chat Bot";
    }

    private String getRandomColor() {
        StringBuffer sb = new StringBuffer("#FFF");

        return sb.toString();
    }
}

class MemberData {
    public int age;
    public String name;
    private String color;

    public MemberData(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public MemberData() {
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "MemberData{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
