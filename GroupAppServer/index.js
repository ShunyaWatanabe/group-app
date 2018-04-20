'use strict';

const express    = require('express');
const app        = express();
const bodyParser = require('body-parser');
const logger 	 = require('morgan');
const router 	 = express.Router();
const port 	   	 = process.env.PORT || 8080;

//Security
var helmet = require('helmet');
app.use(helmet());

var cors = require('cors');
// cors({credentials: true, origin: true});
app.use(cors({credentials: true, origin: true}));

app.use(helmet.referrerPolicy({ policy: 'same-origin' }))


app.use(helmet.contentSecurityPolicy({
  directives: {
    defaultSrc: ["'self'"] //,
    // styleSrc: ["'self'", 'maxcdn.bootstrapcdn.com']
  }
}))


var ninetyDaysInSeconds = 7776000;
app.use(helmet.hpkp({
  maxAge: ninetyDaysInSeconds,
  sha256s: ['nipolski', 'tbachosz', 'jimenez']
}))


//Limits the number of requests
var RateLimit = require('express-rate-limit');

app.enable('trust proxy'); // only if you're behind a reverse proxy (Heroku, Bluemix, AWS if you use an ELB, custom Nginx setup, etc)

var limiter = new RateLimit({
  windowMs: 60*1000, // 15 minutes
  max: 30, // limit each IP to 30 requests per windowMs
  delayMs: 0 // disable delaying - full speed until the max limit is reached
});

//  apply to all requests
app.use(limiter);

// CHAT
var server = require('http').Server(app);
var io = require('socket.io')(server);
const socketioHandler = require('./functions/socketioHandler');
socketioHandler.ioConnections(io);


app.use(bodyParser.json());
app.use(logger('dev'));

require('./routes')(router);
app.use('/api/v1', router);

// app.listen(port);
server.listen(port);

console.log(`App Runs on ${port}`);
