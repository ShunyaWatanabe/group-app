'use strict';

const config = require('../config/config.json');

const mongoose = require('mongoose');
const group = require('../models/user');

const Schema = mongoose.Schema;

const groupSchema = mongoose.Schema({
	
	name: String,
	created_at: String,
	creator:{type: Schema.Types.ObjectId, ref: 'user'},
	members:[{type: Schema.Types.ObjectId, ref: 'user'}],

});

mongoose.Promise = global.Promise;
mongoose.connect(config.db);
module.exports = mongoose.model('group', groupSchema);
