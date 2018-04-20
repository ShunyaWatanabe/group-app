'use strict';

const auth = require('basic-auth');
const jwt = require('jsonwebtoken');

const backPath = './';
const config = require(backPath + 'config/config.json');
const user = require(backPath + 'models/user');

const register = require(backPath + 'functions/register');
const login = require(backPath + 'functions/login');
// const profile = require(backPath + 'functions/profile');

//Push notifications
let FCM = require('fcm-node');
let fcm = new FCM(config.serverKey);

module.exports = router => {

	//for non path
	router.get('/', (req, res) => res.end('Works!'));


	//push not
	router.get('/push', (req,res) => {

	    var message = { //this may vary according to the message type (single recipient, multicast, topic, et cetera)
	        registration_ids: ['eSobx6Kftco:APA91bG6X3w18PdYKaEs2GNeoJLEwyCUvkLErRZcmBUMZQpW37Kq7k8LImcnGR_Sj6QLLWU5mEQQBTrE9VvhBGrVFNAbjPiFYwaVRs0VBvgCftkNwRztessYxzg3VftWdlZK5DCqoLxK',
	        'dCjUpAl8ItU:APA91bGjlOmHVqFKDTMoDupMUmnkdrshLx_OfEcbyzRbuB_KsMlNU57oUOlqzCNzKnKNOOSreSgMxfEYfKI4TYjSgj1jr29R0AywjARHKeaKQbMX_2WHFmVI2MjL65vlAhivH2_norOL'], //refistration_token
	        //at most 1000 registration tokens
	        //collapse_key

	        // notification: {
	        //     title: 'Title of your push notification',
	        //     body: 'Body of your push notification'
	        // },

	        data: {  //you can send only notification or only data(or include both)
	            title: 'Title of your push notification',
	            text: 'Body of your push notification',
	            my_key: 'my value',
	            my_another_key: 'my another value'
	        }
	    };

	    fcm.send(message, function(err, response){
	        if (err) {
	            console.log("Something has gone wrong!");
	            res.status(400).json({ message: "Error" });
	        } else {
	            console.log("Successfully sent with response: ", response);
	            res.status(200).json({ message: "Sent"});
	        }
	    });
	});


	//user login operation
	router.post('/users/login', (req, res) => {

		const credentials = auth(req);

		if (!credentials) {

			res.status(400).json({ message: 'Invalid Request !' });

		} else {

			login.loginUser(credentials.private_key, req.body.fcm_token)

			.then(result => {

				const token = jwt.sign(result, config.secret, { expiresIn: 20 });
				res.status(result.status).json({ message: result.message, token: token, refresh_token: result.refresh_token });

			})

			.catch(err => res.status(err.status).json({ message: err.message }));
		}
	});

	//relogin
	router.post('/users/relogin', (req, res) => {

		//var ob = JSON.stringify(req.body);

		checkingTokens.checkTokens(req)

			.then(result => {

				let response = { message: result.message};

				response = createResponse(result, response);

				res.status(result.status).json(response);
			})

			.catch(err => res.status(err.status).json({ message: err.message }));

	});

	//add a new user
	router.post('/users/signup', (req, res) => {

		console.log("Name: " + req.body.name); //+" private key "+req.body.private_key);

		const name = req.body.name;
		// const private_key = req.body.private_key;

		if (!name  || !name.trim() ) {//|| !private_key || !private_key.trim()

		console.log("error");
			res.status(400).json({message: 'Invalid Request !'});

		} else {

		console.log("ok");
			register.registerUser(req)

			.then(result => {
				const token = jwt.sign(result, config.secret, { expiresIn: 20 });

				//res.setHeader('Location', '/users/'+name);
				res.status(result.status).json({ message: result.message, token: token, refresh_token: result.refresh_token})
			})

			.catch(err => res.status(err.status).json({ message: err.message }));
		}
	});


	router.get('/users/search/:query', (req,res) => {

			checkingTokens.checkTokens(req)

			.then(result => {

				search.users(req.params.query)

				.then(result1 =>{

					result1 = createResponse(result, result1);

					res.status(result.status).json(result1);
				})

				.catch(err1 => res.status(err1.status).json({ message: err1.message }));

			})

			.catch(err => res.status(err.status).json({ message: err.message }));

	});

	//Creates new access token if needed
	function createResponse(result, response){

		if(result.status == 203){
			response.token = jwt.sign(result, config.secret, { expiresIn: 20 });
			return response;
		}
		else
			return response;

	}

}
