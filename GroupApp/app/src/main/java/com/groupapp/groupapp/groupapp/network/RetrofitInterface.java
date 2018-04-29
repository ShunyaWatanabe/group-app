package com.groupapp.groupapp.groupapp.network;

import android.app.FragmentManager;

import com.groupapp.groupapp.groupapp.model.ConnectingUser;
import com.groupapp.groupapp.groupapp.model.Response;
import com.groupapp.groupapp.groupapp.model.User;
import com.groupapp.groupapp.groupapp.model.Group;

import java.util.ArrayList;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface RetrofitInterface {

    //USERS
    @POST("users/signup")
    Observable<Response> register(@Body User user);

    @POST("users/relogin")
    Observable<Response> reLogin();

    @GET("users/{private_key}")
    Observable<User> getProfile(@Path("private_key") String private_key);

    @POST("users/changeUserName")
    Observable<Response> changeUserName(@Body String[] name_private_key);


    //GROUPS
    @GET("groups/{getgroups}")
    Observable<Response> getGroupsFromServer(@Path("getgroups") String private_key );

    @GET("groups/get/{getsinglegroup}")
    Observable<Group> getSingleGroupFromServer(@Path("getsinglegroup") String private_key );

    @POST("groups/newgroup")
    Observable<Response> newGroup(@Body User user);

    @POST("groups/newgroupfromanterchamber")
    Observable<Response> newGroupFromAntechamber(@Body ArrayList<ConnectingUser> users);

    @POST("groups/joininvite")
    Observable<Response> joinInvite(@Body String[] private_key_invite_code);

    @GET("groups/invite/{getinvitationcode}")
    Observable<Response> getInvitationCode(@Path("getinvitationcode") String group_id);

    @POST("groups/leavegroup")
    Observable<Response> leaveGroup(@Body String[] private_key_groupID);

    @GET("groups/getmembers/{groupid}")
    Observable<Response> getMembers(@Path("groupid") String group_id);

    @POST("groups/changeGroupName")
    Observable<Group> changeGroupName();


//
//    //MESSAGES
//    @POST("messages/getMessages")
//    Observable<MessageList> getMessages(@Body Message message);
//
//    @GET("messages/getLastConversation/{email}")
//    Observable<MessageList> getLastConversation(@Path("email") String email);


}
