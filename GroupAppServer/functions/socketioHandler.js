'use strict';

const message = require('../models/message');
const roomName = require('../functions/roomName');

exports.ioConnections = io => {

	io.on('connection', function(socket){
		console.log('user connected');

		socket.on('join private', function(object){ 
		    console.log("room: " + roomName.getRoomName(object.sender, object.receiver)); 
		    socket.join(roomName.getRoomName(object.sender, object.receiver));
		    io.local.emit('join private', "joined"); //send message to user
		});

		socket.on('private message', function(object){ 
		    console.log('message: ' + object.message); 
		    //socket.broadcast.to(createRoomName(object.sender, object.receiver)) -- to all excepting sender!!!
		    socket.broadcast.to(roomName.getRoomName(object.sender, object.receiver)).emit('private message', {message: object.message}); 
		    // io.emit('chat message', msg); //send message to user
		    addToDB(object);
		});

		socket.on('chat message', function(msg, name){ //send message to server
		    console.log('message: ' + msg + " from: " + name); 
		    // io.emit('chat message', msg); //send message to user
		});

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