package com.example.facebooklogin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private TextView prfl_nm,prfl_email;
    private CircleImageView circleImageView;

    private CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton=findViewById(R.id.btn_login);
        circleImageView=findViewById(R.id.prfl_pic);
        prfl_email=findViewById(R.id.prfl_email);
        prfl_nm=findViewById(R.id.prfl_nm);

        callbackManager=CallbackManager.Factory.create();

        loginButton.setReadPermissions(Arrays.asList("email","public_profile"));

        checkLoginStatus();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    AccessTokenTracker tokenTracker=new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if(currentAccessToken==null){
                prfl_email.setText("");
                prfl_nm.setText("");
                circleImageView.setImageResource(0);
                Toast.makeText(getApplicationContext(), "User Logged out", Toast.LENGTH_SHORT).show();
            }
            else{
                loadUserProfile(currentAccessToken);
            }
        }
    };

    private void loadUserProfile(AccessToken accessToken){
        GraphRequest request=GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String first_name=object.getString("first_name");
                    String last_name=object.getString("last_name");
                    String email=object.getString("email");
                    String id=object.getString("id");
                    String img_url="https://graph.facebook.com/"+id+"/picture?type=normal";

                    prfl_email.setText(email);
                    prfl_nm.setText(first_name+" "+last_name);
                    RequestOptions requestOptions=new RequestOptions();
                    requestOptions.dontAnimate();


                    Glide.with(MainActivity.this).load(img_url).into(circleImageView);




                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters=new Bundle();
        parameters.putString("fields","first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void checkLoginStatus(){
        if(AccessToken.getCurrentAccessToken()!=null)
            loadUserProfile(AccessToken.getCurrentAccessToken());
    }
}
