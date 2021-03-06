package os.app.unik.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import os.app.unik.Fragment.PostDetailFragment;
import os.app.unik.Fragment.ProfileFragment;
import os.app.unik.Model.Notification;
import os.app.unik.Model.Post;
import os.app.unik.Model.User;
import os.app.unik.Model.question_post;
import os.app.unik.PostActivity;
import os.app.unik.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Viewholder> {
    private Context mContext;
    private List<Notification> mNotification;
    public NotificationAdapter(Context mContext, List<Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent,false);
        return new NotificationAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Notification notification=mNotification.get(position);
        holder.text.setText(notification.getText());
        getUserInfo(holder.image_profile,holder.username,notification.getUserid());
        if(notification.isIspost()){
            holder.post_image.setVisibility(View.VISIBLE);
            getPostImage(holder.post_image,notification.getPostid());
        }
        else if(notification.isIsquestion()){
            holder.post_image.setVisibility(View.VISIBLE);
            getquestionImage(holder.post_image,notification.getPostid());
        }
        else {
            holder.post_image.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notification.isIspost()) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("postid", notification.getPostid());
                    editor.apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new PostDetailFragment()).commit();
                }
                if(notification.isIsquestion()){
                }
                else {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", notification.getUserid());
                    editor.apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ProfileFragment()).commit();
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        public ImageView image_profile,post_image;
        public TextView username,text;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.comment);
        }
    }

    private void getUserInfo(final ImageView imageView,final TextView username,String publisherid){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).apply(new RequestOptions().placeholder(R.drawable.profilelogo)).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostImage(ImageView imageView,String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Posts").child(postid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                if (!post.getPostimage().isEmpty())
                try {
                    Glide.with(mContext).load(post.getPostimage()).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(imageView);
                }
                catch (Exception e){

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getquestionImage(ImageView imageView, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("questions").child(postid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                question_post questionPost= dataSnapshot.getValue(question_post.class);
                if(!questionPost.getPostimage().equals("none")){
                    Glide.with(mContext).load(questionPost.getPostimage()).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(imageView);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
