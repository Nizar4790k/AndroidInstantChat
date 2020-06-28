package com.example.androidinstantchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidinstantchat.model.ChatMessage;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment {

    private static final int SIGN_IN_REQUEST_CODE=1;
    private EditText mInput;
    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private DatabaseReference mDatabaseReference;
    private List<ChatMessage> mMChatMessages;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(getActivity());
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(getActivity(),"Welcome " + FirebaseAuth.getInstance()
                    .getCurrentUser()
                    .getDisplayName(),Toast.LENGTH_SHORT).show();

            // Load chat room contents
            displayChatMessages();
        }





    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment,container,false);

        mMChatMessages = new ArrayList<>();

        FloatingActionButton fab =
                view.findViewById(R.id.fab);

        mInput = view.findViewById(R.id.input);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mRecyclerView = view.findViewById(R.id.list_of_messages);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));








        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                mDatabaseReference
                        .push()
                        .setValue(new ChatMessage(mInput.getText().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName())
                        );

                // Clear the input
                mInput.setText("");

                displayChatMessages();
            }
        });


        


        return view;
    }

    private class ChatAdapter extends RecyclerView.Adapter<ChatHolder>{

        private List<ChatMessage> mChatMessageList;

        public ChatAdapter(List<ChatMessage> list){
            mChatMessageList = list;
        }

        @NonNull
        @Override
        public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getContext());

            return new ChatHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
            holder.bind(mChatMessageList.get(position));
        }

        @Override
        public int getItemCount() {
            return mChatMessageList.size();
        }

        public List<ChatMessage> getChatMessageList() {
            return mChatMessageList;
        }

        public void setChatMessageList(List<ChatMessage> chatMessageList) {
            mChatMessageList = chatMessageList;
        }
    }

    private class ChatHolder extends  RecyclerView.ViewHolder{

        private TextView mMessageUser;
        private TextView mMessageTime;
        private TextView mMessageText;

       public ChatHolder(LayoutInflater inflater,ViewGroup parent){
           super(inflater.inflate(R.layout.message,parent,false));

           mMessageText = itemView.findViewById(R.id.message_text);
           mMessageUser = itemView.findViewById(R.id.message_user);
           mMessageTime = itemView.findViewById(R.id.message_time);

       }

       public void bind(ChatMessage message){
           mMessageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                   message.getMessageTime()));

           mMessageText.setText(message.getMessageText());
           mMessageUser.setText(message.getMessageUser());

       }



    }


    private void displayChatMessages(){

        mMChatMessages = new ArrayList<>();






        if(mMChatMessages.size()==0){
            return;
        }


        if(mAdapter==null){


            mAdapter = new ChatAdapter(mMChatMessages);
            mRecyclerView.setAdapter(mAdapter);


            return;

        } else {
            mAdapter.setChatMessageList(mMChatMessages);
            mAdapter.notifyDataSetChanged();

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Activity activity = getActivity();

        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(activity,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                displayChatMessages();
            } else {
                Toast.makeText(activity,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
               activity.finish();
            }
        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_fragment,menu);

    }

    public boolean onOptionsItemSelected(MenuItem item) {

       final Activity activity = getActivity();


       switch (item.getItemId()){
           case R.id.menu_sign_out:

               AuthUI.getInstance().signOut(activity)
                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override                                      //Code to manage sign out
                           public void onComplete(@NonNull Task<Void> task) {
                               Toast.makeText(activity,
                                       "You have been signed out.",
                                       Toast.LENGTH_LONG)
                                       .show();

                               // Close activity
                               activity.finish();
                           }
                       });

               return true;

           default:
               return super.onOptionsItemSelected(item);
       }



    }





}
