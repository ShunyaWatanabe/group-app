package com.groupapp.groupapp.groupapp.network;

import android.app.FragmentManager;

import com.groupapp.groupapp.groupapp.model.Response;
import com.groupapp.groupapp.groupapp.model.User;
import com.groupapp.groupapp.groupapp.model.Group;

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

    @POST("users/login")
    Observable<Response> login(@Body User user);

    @POST("users/relogin")
    Observable<Response> reLogin();

    @GET("users/checkIfregistered")
    Observable<Response> checkIfRegistered(@Path("private_key")String private_key);

    @GET("users/{private_key}")
    Observable<User> getProfile(@Path("private_key") String private_key);

    @POST("users/changeUserName")
    Observable<User> changeUserName();


    //GROUPS
    @POST("groups/joingroup")
    Observable<Response> joinGroup(@Body User user);

    @GET("groups/getinvitationcode")
    Observable<Integer> getInvitationCode();

    @POST("groups/leavegroup")
    Observable<Response> leaveGroup();

    @GET("groups/getmembers")
    Observable<Response> getMembers();

    @POST("groups/changeGroupName")
    Observable<Group> changeGroupName();




    //Group
//    @POST("events/create/{email}")
//    Observable<Response> createEvent(@Path("email") String email, @Body Event event);
//
//    @GET("events/delete/{id}")
//    Observable<Response> deleteEvent(@Path("id") String id, @Body Event event);
//
//    @GET("events/getEvents/{email}")
//    Observable<EventList> getEvents(@Path("email") String email);
//
//    @GET("events/joinedEvents/{email}")
//    Observable<EventList> joinedEvents(@Path("email") String email);
//
//    @GET("events/myEvents/{email}")
//    Observable<EventList> myEvents(@Path("email") String email);
//
//    @GET("events/interestedEvents/{email}")
//    Observable<EventList> interestedEvents(@Path("email") String email);
//
//    @POST("events/join/{id}/{email}")
//    Observable<Response> joinEvent(@Path("id") String id, @Path("email") String email);
//
//    @POST("events/unjoin/{id}/{email}")
//    Observable<Response> unjoinEvent(@Path("id") String id, @Path("email") String email);
//
//    @POST("events/interested/{id}/{email}")
//    Observable<Response> interestedinEvent(@Path("id") String id, @Path("email") String email);
//
//    @POST("events/uninterested/{id}/{email}")
//    Observable<Response> uninterestedinEvent(@Path("id") String id, @Path("email") String email);
//
//    @GET("events/getComments/{id}") //event id
//    Observable<CommentList> getEventComments(@Path("id") String id);
//
//    @GET("events/search/{query}")
//    Observable<EventList> getEventsSearch(@Path("query") String query);
//
//    @GET("events/getUsersAttending/{id}")
//    Observable<UserList> getUsersAttending(@Path("id") String id);
//
//    @GET("events/getUsersInterested/{id}")
//    Observable<UserList> getUsersInterested(@Path("id") String id);
//
//    @POST("events/filteredEvents")
//    Observable<EventList> getFilteredEvents(@Body Event event);
//
//    @GET("events/myEvents/{email}")
//    Observable<EventList> getMyEvents(@Path("email") String email);
//
//    @GET("events/getEvent/{id}")
//    Observable<Event> getEvent(@Path("id") String id);

//
//    //MESSAGES
//    @POST("messages/getMessages")
//    Observable<MessageList> getMessages(@Body Message message);
//
//    @GET("messages/getLastConversation/{email}")
//    Observable<MessageList> getLastConversation(@Path("email") String email);


//    @POST("emailList/createList/{email}")
//    Observable<MessageList>createList()
        // @POST("organization/create/{email}")
    // Observable<Response> createOrganization(@Path("email") String email, @Body Organization organization);

}
