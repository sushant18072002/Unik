package os.app.unik.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import os.app.unik.Adapter.PostAdapter;
import os.app.unik.Adapter.QuestionAdapter;
import os.app.unik.MainActivity;
import os.app.unik.Model.Post;
import os.app.unik.Model.User;
import os.app.unik.Model.question_post;
import os.app.unik.R;
import os.app.unik.question_uploadActivity;

public class Q_ansFragment extends Fragment {
    private RecyclerView recyclerView;
    private QuestionAdapter questionAdapter;
    private List<question_post> question_posts;

    ImageView image_profile;
    TextView question;
    LinearLayout question_layout;
    FirebaseUser firebaseUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_q_ans, container, false);
        image_profile=view.findViewById(R.id.image_profile);
        question=view.findViewById(R.id.question);
        question_layout=view.findViewById(R.id.question_layout);

        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        question_posts=new ArrayList<>();
        questionAdapter=new QuestionAdapter(getContext(),question_posts);
        recyclerView.setAdapter(questionAdapter);

        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), question_uploadActivity.class);
                startActivity(intent);
                //overridePendingTransition(R.anim.bottom_up, R.anim.stay);
            }
        });
        question_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), question_uploadActivity.class);
                startActivity(intent);
            }
        });

        userInfo();
        readPosts();
        return view;
    }
    private void userInfo(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext()==null){
                    return;
                }
                User user=snapshot.getValue(User.class);
                Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readPosts(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("questions");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                question_posts.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    question_post post=snapshot1.getValue(question_post.class);
                    question_posts.add(post);
                    }
                    questionAdapter.notifyDataSetChanged();
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}