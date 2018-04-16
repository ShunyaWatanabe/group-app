'use strict';

const mongoose = require('mongoose');
const user = require('../models/user');

const Schema = mongoose.Schema;

const conversationSchema = mongoose.Schema({

	members: [{type: Schema.Types.ObjectId, ref: 'user'}],

});

module.exports = mongoose.model('conversation', conversationSchema);
