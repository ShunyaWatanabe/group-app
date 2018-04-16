'use strict';

const mongoose = require('mongoose');
const group = require('../models/user');

const Schema = mongoose.Schema;

const messageSchema = mongoose.Schema({

	conversation_id: {type: Schema.Types.ObjectId, required: true},
	text: {type: String, required: true},
	author:{type: Schema.Types.ObjectId, ref: 'user'},

	
	},
	{
		timestamps: true;
	});

module.exports = mongoose.model('message', messageSchema);
