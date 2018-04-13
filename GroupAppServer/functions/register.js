'use strict';

const user = require('../models/user');
const bcrypt = require('bcryptjs');
const randomstring = require("randomstring");
const config = require('../config/config.json');
const nodemailer = require('nodemailer');

exports.registerUser = req =>

	new Promise((resolve,reject) => {

	  const salt = bcrypt.genSaltSync(10);
		const hash = bcrypt.hashSync(req.body.password, salt);
		const refreshToken = randomstring.generate();

		//Mail token
		// const random = randomstring.generate(4);
		// const salt2 = bcrypt.genSaltSync(10);
		// const hash2 = bcrypt.hashSync(random, salt2);

		const newUser = new user({
			name: req.body.name,

			private_key =  randomstring.generate(12);
			created_at: new Date(),
			refresh_token: refreshToken,

			isVerified: true,
		});
		newUser.save()

		.then(() => {

			// const transporter = nodemailer.createTransport(`smtps://${config.email}:${config.password}@smtp.gmail.com`);

			// const transporter = nodemailer.createTransport({
		  //       host: 'tunny.kylos.pl',
		  //       port: 465,
		  //       secure: true,
		  //       auth: {
		  //           user: "ballotbox@mitkowskiwojcik.pl",
		  //          	pass: "n7uKEAZWayTo",
		  //       }
		  //   });

			// const mailOptions = {
			//
    	// 		from: `"${config.name}" <${config.email}>`,
    	// 		to: req.body.email,
    	// 		subject: 'BallotBox Verification Code',
    	// 		html: `Hello ${req.body.name},<br><br>
			//
    	// 		     Your verification code is <b>${random}</b>.<br><br>
			//
    	// 		Thanks,<br>
    	// 		BallotBox Team.`
			//
			// };

			// If you are viewing this mail from a Android Device click this <a href="http://learn2crack/${random}">link</a>.


			 ///transporter.sendMail(mailOptions);
			 //Un comment ABOVE!!!!!!!!! - message: 'Check mail for instructions'
			resolve({ status: 201, message: 'User created!', refresh_token: refreshToken,  })

		})

		// .then(info => {
		// console.log("5");

		// 	console.log(info);

			// resolve({ status: 201, message: 'Check mail for instructions' })

		// })

		// .then(() => resolve({ status: 201, message: 'User Registered Sucessfully !'}))


		.catch(err => {
			if (err.code == 11000) {

				reject({ status: 409, message: 'User Already Registered !' });

			} else {

				reject({ status: 500, message: 'Internal Server Error !' });
			}
		});
	});


exports.checkEmail = email =>

	new Promise((resolve,reject) => {

		user.find({ email: email }, {email:1})

		.then(users => {

			if (users.length == 0) {

				resolve({ status: 200, message: 'User not registered yet', isAvailable: true });

			} else {

				resolve({ status: 201, message: 'User already registered !', isAvailable: false });

			}
		})

		.catch(err => reject({ status: 500, message: 'Internal Server Error !' }))

	});


exports.resendCode = email =>

	new Promise((resolve,reject) => {
		let name, random;

		user.find({ email: email }, { email: 1, verification_pass: 1, isVerified: 1, name: 1})

		.then(users => {

			if (users.length == 0) {

				resolve({ status: 200, message: 'User does not exist', isAvailable: true });

			} else {

				let user = users[0];

				if(!user.isVerified){
					name = user.name;
					//Mail token
					random = randomstring.generate(4);
					const salt2 = bcrypt.genSaltSync(10);
					const hash2 = bcrypt.hashSync(random, salt2);

					user.verification_pass = hash2;

					return user.save();

				}else{

					reject({ status: 401, message: 'Already Verified !' });
				}
			}
		})

		.then(() => {

			// const transporter = nodemailer.createTransport(`smtps://${config.email}:${config.password}@smtp.gmail.com`);

			const transporter = nodemailer.createTransport({
		        host: 'tunny.kylos.pl',
		        port: 465,
		        secure: true,
		        auth: {
		            user: "ballotbox@mitkowskiwojcik.pl",
		           	pass: "n7uKEAZWayTo",
		        }
		    });

			const mailOptions = {

    			from: `"${config.name}" <${config.email}>`,
    			to: email,
    			subject: 'BallotBox Verification Code',
    			html: `Hello ${name},<br><br>

    			     Your verification code is <b>${random}</b>.<br><br>

    			Thanks,<br>
    			BallotBox Team.`

			};

			// If you are viewing this mail from a Android Device click this <a href="http://learn2crack/${random}">link</a>.


		// return
			transporter.sendMail(mailOptions);
			resolve({ status: 201, message: 'Check mail for instructions' })

		})

		.catch(err => reject({ status: 500, message: 'Internal Server Error !' }))

	});


exports.verifyEmail = (email, code) =>

	new Promise((resolve,reject) => {

		user.find({ email: email }, { email: 1, verification_pass: 1, isVerified: 1})

		.then(users => {

			if (users.length == 0) {

				resolve({ status: 200, message: 'User does not exist', isAvailable: true });

			} else {

				let user = users[0];

				if (bcrypt.compareSync(code, user.verification_pass)) {

					user.verification_pass = undefined;
					user.isVerified = true;

					return user.save();

				} else {

					reject({ status: 401, message: 'Invalid Code !' });
				}
			}
		})

		.then(user => resolve({ status: 200, message: 'Verified Sucessfully' }))

		.catch(err => reject({ status: 500, message: 'Internal Server Error !' }))

	});
