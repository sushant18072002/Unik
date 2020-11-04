package os.app.unik.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import os.app.unik.Adapter.PostAdapter;
import os.app.unik.Model.Post;
import os.app.unik.R;


public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postLists;
    private List<String> followingList;
    ImageView search;
    FirebaseUser firebaseUser;

    ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home,container,false);
        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),postLists);
        recyclerView.setAdapter(postAdapter);

        progressBar=view.findViewById(R.id.progress_circular);

        search=view.findViewById(R.id.search_bar);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SearchFragment()).commit();
            }
        });
        checkFollowing();
        return view;
    }
    private void checkFollowing(){
        followingList=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    followingList.add(snapshot1.getKey());
                    //Toast.makeText(getContext(),"check "+snapshot1.getKey().toString(),Toast.LENGTH_LONG).show();
                }
                //followingList.add("ygojlhghmaWhKNwRFMv2Vrv9Iub2");
                readPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
    private void readPosts(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postLists.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Post post=snapshot1.getValue(Post.class);
                    for(String id : followingList){
                        if(post.getPublisher().equals(id)){
                            postLists.add(post);
                        }
                    }
                    if(post.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        postLists.add(post);
                    }
                }
               //mypost();
                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mypost(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Post post=snapshot1.getValue(Post.class);

                }
                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                //Collections.reverse(postLists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void msg(String msgs){
        Toast.makeText(getContext(),msgs,Toast.LENGTH_LONG).show();
    }
}