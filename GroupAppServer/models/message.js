'use strict';

const config = require('../config/config.json');

const mongoose = require('mongoose');
const group = require('../models/user');

const Schema = mongoose.Schema;

const messageSchema = mongoose.Schema({
		
	text: {type: Schema.Types.ObjectId, ref: 'message'},
	time: Number,
	chat_room:String, //not sure here
	sender_id:{type: Schema.Types.ObjectId, ref: 'user'},
	receiver_id:{type: Schema.Types.ObjectId, ref: 'user'}

});

mongoose.Promise = global.Promise;
mongoose.connect(config.db);
module.exports = mongoose.model('message', messageSchema);
