'use strict';

const user = require('../models/user');
const bcrypt = require('bcryptjs');

exports.loginUser = (private_key, fcm_token) =>

	new Promise((resolve,reject) => {

		user.find({ private_key: private_key }, {isVerified:1, fcm_token:1, refresh_token:1})

		.then(users => {

			if (users.length == 0) {
				reject({ status: 404, message: 'User Not Found !' });

			} else {

				return users[0];

			}
		})

		.then(user => {

			if(user.isVerified == true){

				if(fcm_token != null)
					if (!fcm_token || !fcm_token.trim())
						if(user.fcm_token.indexOf(fcm_token) < 0)
							user.fcm_token.push(fcm_token);

				user.save();

				resolve({ status: 200, message: name, refresh_token: user.refresh_token });
			}
			else{
				reject({ status: 404, message: 'Account is not verified!' });
			}
		})

		.catch(err => reject({ status: 500, message: 'Internal Server Error !' }));

	});

exports.reloginUser = private_key =>

	new Promise((resolve,reject) => {

		user.find({ private_key: private_key }, { refresh_token: 1})

		.then(users => resolve(users[0]))

		.catch(err => reject({ status: 500, message: 'Internal Server Error !' }))

	});

exports.logout = (private_key, fcm_token) =>

	new Promise((resolve,reject) => {

		user.find({ private_key: private_key }, { fcm_token: 1})

		.then(users => {

			if (users.length == 0) {

				reject({ status: 404, message: 'User Not Found !' });

			} else {

				return users[0];

			}
		})

		.then(user => {

			if(fcm_token != null)
				if (!fcm_token || !fcm_token.trim())
					if(user.fcm_token.indexOf(fcm_token) >= 0)
						user.fcm_token.splice(user.fcm_token.indexOf(fcm_token), 1);

			return user.save();

		})

		.then(user => resolve({ status: 200, message: 'Logout !'}))

		.catch(err => reject({ status: 500, message: 'Internal Server Error !' }))


	});
