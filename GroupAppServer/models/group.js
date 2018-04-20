'use strict';

const config = require('../config/config.json');

const mongoose = require('mongoose');

const user = require('../models/user');
const message = require('../models/message');

const Schema = mongoose.Schema;

const groupSchema = mongoose.Schema({

	name: String,
	creator:{type: Schema.Types.ObjectId, ref: 'user'},
	members:[{type: Schema.Types.ObjectId, ref: 'user'}],
	conversation: [{type: Schema.Types.ObjectId, ref: 'message'}]
	// timestamps: true
	});

mongoose.Promise = global.Promise;
mongoose.createConnection(config.db);
module.exports = mongoose.model('group', groupSchema);
