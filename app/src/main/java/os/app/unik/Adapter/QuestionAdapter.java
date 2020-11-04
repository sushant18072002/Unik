package os.app.unik.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import os.app.unik.CommentsActivity;
import os.app.unik.Fragment.ProfileFragment;
import os.app.unik.MainActivity;
import os.app.unik.Model.Notification;
import os.app.unik.Model.User;
import os.app.unik.Model.question_post;
import os.app.unik.PostActivity;
import os.app.unik.R;
import os.app.unik.RegisterActivity;
import os.app.unik.answer_uploadActivity;
import os.app.unik.question_uploadActivity;

import static android.content.ContentValues.TAG;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder>{
    public Context mContext;
    public List<question_post> question_posts;
    String notification_id="none";
    boolean condition=true;

    private FirebaseUser firebaseUser;
    public QuestionAdapter(Context mContext, List<question_post> question_posts) {
        this.mContext = mContext;
        this.question_posts = question_posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.question_item,parent,false);
        return new QuestionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final question_post qpost=question_posts.get(position);
        String id;
        if(!qpost.getPostimage().equals("none")) {
            holder.post_image.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(qpost.getPostimage()).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(holder.post_image);
        }
        else
        holder.post_image.setVisibility(View.GONE);
        if(qpost.getQuestion()!=null) {
            holder.question_post.setText(qpost.getQuestion());
            holder.answers.setVisibility(View.VISIBLE);
            id=qpost.getPostid();
            getanswer(id,holder.answers);
            isLiked(id,holder.upward);
            nrupwards(holder.upwards,id);
        }
        else {
            holder.question_post.setText(qpost.getAnswer());
            id=qpost.getAnswerid();
            holder.answer.setImageResource(R.drawable.ic_comment);
            getComments(id,holder.answers);
            isLiked_answer(id,holder.upward);
            nrupwards(holder.upwards,id);
        }
        publisherInfo(holder.image_profile,holder.username,qpost.getPublisher());



        if(qpost.getQuestion()!=null) {
            holder.image_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("profileid",qpost.getPublisher());
                    editor.apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                }
            });

            holder.username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("profileid",qpost.getPublisher());
                    editor.apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                }
            });

            holder.upward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        /*if (holder.upward.getTag().equals("Upward")) {
                            FirebaseDatabase.getInstance().getReference().child("Upward").child(qpost.getPostid())
                                    .child(firebaseUser.getUid()).setValue(true);
                            addNotification(qpost.getPublisher(),qpost.getPostid(),true);
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("Upward").child(qpost.getPostid())
                                    .child(firebaseUser.getUid()).removeValue();
                            addNotification(qpost.getPublisher(),qpost.getPostid(),false);
                        }*/
                    if (holder.upward.getTag().equals("Upward")) {
                        FirebaseDatabase.getInstance().getReference().child("Upward").child(id)
                                .child(firebaseUser.getUid()).setValue(true);
                        addNotification(qpost.getPublisher(),qpost.getPostid(),true);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Upward").child(id)
                                .child(firebaseUser.getUid()).removeValue();
                        addNotification(qpost.getPublisher(),qpost.getPostid(),false);
                    }
                }
            });
            holder.answer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, answer_uploadActivity.class);
                    intent.putExtra("postid",qpost.getPostid());
                    intent.putExtra("publisherid", qpost.getPublisher());
                    mContext.startActivity(intent);
                }
            });
            holder.answers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, answer_uploadActivity.class);
                    intent.putExtra("postid", qpost.getPostid());
                    intent.putExtra("publisherid", qpost.getPublisher());
                    mContext.startActivity(intent);
                }
            });
        }
        else{
            holder.image_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("publisherid", qpost.getPublisher());
                    mContext.startActivity(intent);
                    //((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                }
            });

            holder.username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("publisherid", qpost.getPublisher());
                    mContext.startActivity(intent);
                    //((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                }
            });

            holder.upward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.upward.getTag().equals("Upward")) {
                        FirebaseDatabase.getInstance().getReference().child("Upward").child(id)
                                .child(firebaseUser.getUid()).setValue(true);
                        //addNotification(qpost.getPublisher(),qpost.getPostid(),true);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Upward").child(id)
                                .child(firebaseUser.getUid()).removeValue();
                        //addNotification(qpost.getPublisher(),qpost.getPostid(),false);
                    }
                }
            });
            holder.answer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent= new Intent(mContext, CommentsActivity.class);
                    intent.putExtra("postid", qpost.getAnswerid());
                    intent.putExtra("publisherid",qpost.getPublisher());
                    mContext.startActivity(intent);
                }
            });
            holder.answers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(mContext, CommentsActivity.class);
                    intent.putExtra("postid", qpost.getAnswerid());
                    intent.putExtra("publisherid", qpost.getPublisher());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return question_posts.size();
    }

        public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView image_profile,post_image,upward,answer,save;
        public TextView username,question_post,upwards,answers;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile=itemView.findViewById(R.id.image_profile);
            post_image=itemView.findViewById(R.id.post_image);
            upward=itemView.findViewById(R.id.upward);
            answer=itemView.findViewById(R.id.answer);
            save=itemView.findViewById(R.id.save);
            username=itemView.findViewById(R.id.username);
            question_post=itemView.findViewById(R.id.question_post);
            upwards=itemView.findViewById(R.id.upwards);
            answers=itemView.findViewById(R.id.answers);
        }
    }

    private  void publisherInfo(ImageView image_profile,TextView username,String userid){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isLiked(String posid,ImageView imageView){
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Upward").child(posid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_upwarded);
                    imageView.setTag("Upwarded");
                }
                else {
                    imageView.setImageResource(R.drawable.ic_upward);
                    imageView.setTag("Upward");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void isLiked_answer(String posid,ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Upward").child(posid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_upwarded);
                    imageView.setTag("Upwarded");
                } else {
                    imageView.setImageResource(R.drawable.ic_upward);
                    imageView.setTag("Upward");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void nrupwards(final TextView upwards, String postId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Upward").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    upwards.setText(String.format("%d", dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void getComments(String postid,final TextView comments){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.setText("Views ALL "+snapshot.getChildrenCount()+" Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getanswer(String postid,final TextView answer){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("answers").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                answer.setText("Views ALL "+snapshot.getChildrenCount()+" Answers");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void addNotification(String userid, String postid,boolean noti) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        if (noti) {
            notification_id = reference.push().getKey();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("userid", firebaseUser.getUid());
            hashMap.put("text", " upward your question");
            //hashMap.put("text", " upward your answer");
            hashMap.put("postid",postid);
            hashMap.put("ispost", false);
            hashMap.put("isquestion", true);
            hashMap.put("notification_id", notification_id);
            reference.child(notification_id).setValue(hashMap);

        } else {
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.child("postid").getValue().equals(postid) && noti == false) {
                            Log.e(TAG, "=======" + ds.child("userid").getValue());
                            FirebaseDatabase.getInstance().getReference().child("Notifications").child(userid)
                                    .child(String.valueOf(ds.child("notification_id").getValue())).removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

}
