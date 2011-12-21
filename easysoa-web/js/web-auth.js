// EasySOA Web
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/* 
 * Provides functions to handle authentication.
 *
 * Author: Marwane Kalam-Alami
 */

var url = require('url');
var easysoaNuxeo = require('./web-nuxeo.js');

// INTERNAL FUNCTIONS

redirectTo = function(response, url) {
	response.redirect(url);
};

isAnonymouslyAvailable = function(url) {
    return (url.pathname.indexOf('easysoa/core') == -1
            ||  url.pathname.indexOf('bookmarklet') != -1)
        && url.pathname.indexOf('easysoa/light') == -1
        && url.pathname.indexOf('easysoa/dashboard') == -1
        && url.pathname.indexOf('scaffoldingProxy') == -1;
};

isLoginValid = function(username, password, callback) {
    if (easysoaNuxeo.isNuxeoReady()) {
        easysoaNuxeo.checkNuxeo(username, password, function(data) {
            callback(data[0] == "{"); // If login succeeded, we get the automation doc
        });
    }
    else {
        throw "Nuxeo is not ready";
    }
};

// EXPORTS

exports.handleLogin = function(request, response) {
	
	var credentials = request.body || request.query;
	
    if (credentials && credentials.username && credentials.password) {
    	try {
	        isLoginValid(credentials.username, credentials.password, function(isValid) {
	            if (isValid) {
	                request.session.username = request.body.username;
	                request.session.password = request.body.password; // XXX: Could store the Base64 hash instead
	                console.log("[INFO] Session created for: "+request.body.username);
	            	if (credentials.callback) {
	            		response.writeHead(200, {'Content-Type': 'application/json'});
	            		response.end(credentials.callback + '({result: "ok"})');
	            	}
	            	else {
	            		redirectTo(response, request.query.prev || '/easysoa');
	            	}
	            }
	            else {
	            	if (credentials.callback) {
	            		response.writeHead(200, {'Content-Type': 'application/json'});
	            		response.end(credentials.callback + '({result: "error", error: "Invalid credentials"})');
	            	}
	            	else {
	            		redirectToLogin(response, request.url, true);
	            	}
	            }
	        });
    	}
    	catch (error) {
        	if (credentials.callback) {
        		response.writeHead(200, {'Content-Type': 'application/json'});
        		console.log(credentials.callback + '({result: "error", error: "Nuxeo not started"})');
        		response.end(credentials.callback + '({result: "error", error: "Nuxeo not started"})');
        	}
        	else {
        		redirectToLogin(response, request.url, true);
        	}
    	}
    }
    else {
    	if (credentials.callback) {
    		response.writeHead(200, {'Content-Type': 'application/json'});
    		response.end(credentials.callback + '({result: "error", error: "Invalid parameters"})');
    	}
    	else {
    		response.redirect('/easysoa/login.html?nuxeoNotReady=true&prev='+request.body.prev);
    	}
    }
};

exports.authFilter = function (request, response, next) {
    reqUrl = request.urlp = url.parse(request.url, true);

    // Logout
    if (reqUrl.pathname == "/logout") {
      request.session.destroy();
      response.redirect('/easysoa');
      return;
    }
    
    // User data
    if (reqUrl.pathname == "/userdata") {
        if (request.session && request.session.username) {
            var responseData = new Object();
            responseData.username = request.session.username;
            response.write(JSON.stringify(responseData));
        }
        else {
        	response.writeHead(403);
        }
        response.end();
        return;
    }

    // Handle already logged user
    if (request.session && request.session.username != undefined) {
        if (request.url == '/login') {
            if (request.body && request.body.prev) {
            	response.redirect((request.body.prev != '') ? request.body.prev : '/easysoa');
               return;
            }
            else {
            	response.redirect('/easysoa');
               return;
            }
        }
        else {
            next();
            return;
        }
    }

    // Authentication
    if (reqUrl.pathname.indexOf('login.html') != -1) {
        next();
        return;
    }
    else if (reqUrl.pathname == "/login" && request.body) {
        handleLogin(request, response);
    }
    else if (isAnonymouslyAvailable(reqUrl)) {
        next();
    }
    else {
    	response.redirect('/easysoa/login.html?prev='+reqUrl.href);
    }
};

exports.redirectToLogin = redirectToLogin = function(response, prevUrl, error) {
    redirectTo(response, '/easysoa/login.html' + ((prevUrl) ? '?prev='+prevUrl : '') + ((error) ? '?error=true' : ''));
};

exports.isLoggedIn = function(request) {
    return request.session != undefined && request.session.user != undefined;
};
