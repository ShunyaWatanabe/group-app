'use strict';

const config = require('../config/config.json');

const mongoose = require('mongoose');

const user 	 = require('../models/user');
const group	 = require('../models/group');

const Schema = mongoose.Schema;

const messageSchema = mongoose.Schema({

	group: {type: Schema.Types.ObjectId, required: true},
	text: {type: String, required: true},
	sender:{type: Schema.Types.ObjectId, ref: 'user'},
	
	},
	{
		timestamps: true;
	});

mongoose.Promise = global.Promise;
mongoose.connect(config.db);
module.exports = mongoose.model('message', messageSchema);
