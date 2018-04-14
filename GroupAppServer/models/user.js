'use strict';

const config = require('../config/config.json');

const mongoose = require('mongoose');
const group = require('../models/group');

const Schema = mongoose.Schema;

const userSchema = mongoose.Schema({

	name: String,
	created_at: String,
	private_key: String,
	refresh_token: String,
	token: String,

	fcm_token: [String],

	groups_created: [{type: Schema.Types.ObjectId, ref: 'group'}],
	groups_participated: [{type: Schema.Types.ObjectId, ref: 'group'}],
});

mongoose.Promise = global.Promise;
mongoose.connect(config.db);
module.exports = mongoose.model('user', userSchema);