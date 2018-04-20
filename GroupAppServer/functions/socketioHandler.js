'use strict';

const message = require('../models/message');
const group = require('../models/group');
const user = require('../models/user');

exports.ioConnections = io => {

	io.on('connection', function(socket){
		console.log('user connected');
		//join all chat groups participated 

		socket.on('history', function(object){ 
			//console.log
			//send chat history to users 
		    console.log("room: " + roomName.getRoomName(object.sender, object.receiver)); 
		    socket.join(roomName.getRoomName(object.sender, object.receiver));
		    io.local.emit('join private', "joined"); //send message to user
		});

		socket.on('send message', function(object){ 
			//console.log
			//send it to the sender
			//broadcase to all other users
			//addToDB

		    console.log(' smessage: ' + object.message); 
		    //socket.broadcast.to(createRoomName(object.sender, object.receiver)) -- to all excepting sender!!!
		    socket.broadcast.to(roomName.getRoomName(object.sender, object.receiver)).emit('private message', {message: object.message}); 
		    // io.emit('chat message', msg); //send message to user
		    addToDB(object);
		});

		// socket.on('chat message', function(msg, name){ //send message to server
		//     console.log('message: ' + msg + " from: " + name); 
		//     // io.emit('chat message', msg); //send message to user
		// });

		socket.on('disconnect', function(){
		    console.log('user disconnected');
		});

	});

}



// function createRoomName(sender, receiver){
// 	if(sender.localeCompare(receiver)<0)
// 		return sender + ":" + receiver;
// 	else
// 		return receiver + ":" + sender;
// }

function addToDB(object){
	let date = new Date();
	let currentTime = date.getTime();

	const newMessage = new message({
		text: object.message,
		time: currentTime,
		chat_room: roomName.getRoomName(object.sender, object.receiver),
		sender_id: object.sender,
		receiver_id: object.receiver
	});
				
	newMessage.save();
}