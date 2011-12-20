// EasySOA Web
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

/* 
 * Provides functions to communicate with the Nuxeo server.
 *
 * Author: Marwane Kalam-Alami
 */
 
var http = require('http');
var fs = require('fs');
var url = require('url');
var base64 = require('./lib/base64').base64;

eval(fs.readFileSync('./settings.js', 'ASCII'));

var nuxeoReady = false;
var nuxeoDiscovery = url.parse(settings.nuxeoDiscovery);
var nuxeoAutomation = url.parse(settings.nuxeoAutomation);

// INTERNAL FUNCTIONS

computeAuthorization = function(username, password) {
    if (username != null && password != null) {
        return "Basic " + base64.encode(username + ':' + password);
    }
    else {
        return null;
    }
};

sendNotification = function(nuxeoUploadOptions, body, callback) {

      restRequest = http.request(nuxeoUploadOptions, function(restResponse) {
	
          // Nuxeo response handling
          var data = "";
          restResponse.on('data', function(chunk) {
            data += chunk.toString("ascii");
          });
          restResponse.on('end', function() {
            try {
                callback(JSON.parse(data));
            }
            catch (error) {
                callback({result: error});
            }
          });
          
      });
      
      restRequest.addListener('error', function(error) {
        console.log("[WARN] Failure while sending REST request to Nuxeo "+error);
        callback({result: error});
      });
      restRequest.write(body);
      restRequest.end();
    
};

// EXPORTS

/**
 * 
 */
exports.isNuxeoReady = function() {
    return nuxeoReady;
};

/**
 * 
 * @param session
 * @param query
 * @param headers
 * @param callback
 */
exports.queryDocuments = function(session, query, callback, headers) {
    if (headers == undefined || headers == null) {
        headers = {};
    }
    if (headers["X-NXDocumentProperties"] == undefined)  {
        headers["X-NXDocumentProperties"] = "*";
    }
    easysoaNuxeo.automationQuery(session, "Document.Query", {query: query}, headers, callback);
};

// TODO Refactoring with checkNuxeo
exports.automationQuery = function(session, operation, input, headers, callback) {

  // Normalize optional params
  if (input == null)
      input = "";
  if (headers == null)
      headers = {};
  if (callback == null)
      callback = function() {};
    
  headers['Content-Type'] = 'application/json+nxrequest';
  headers['Accept'] = 'application/json+nxentity, */*';
  headers['Authorization'] = computeAuthorization(session.username, session.password);

  var requestOptions = {
	  'port' : nuxeoAutomation.port,
	  'method' : 'POST',
	  'host' : nuxeoAutomation.hostname,
	  'path' : nuxeoAutomation.href+'/'+operation,
	  'headers' : headers
  };
  
  var body = new Object();
  body.params = input;
  body.context = {};
  
  var nxRequest = http.request(requestOptions, function(response) {
        var responseData = '';
        response.on('data', function(data) {
              responseData += data;
          });
		response.on('end', function() {
            callback(responseData);
		});
  });
  
  nxRequest.on('error', function(data) {
    callback(false);
  });
  nxRequest.write(JSON.stringify(body));
  nxRequest.end();

};

/**
 *
 */
exports.checkNuxeo = function(username, password, callback) {

  var requestOptions = {
	  port : nuxeoAutomation.port,
	  method : 'GET',
	  host : nuxeoAutomation.hostname,
	  path : nuxeoAutomation.href,
	  headers : {
	    'Content-Type': 'application/x-www-form-urlencoded',
        'Authorization': computeAuthorization(username, password)
	  }
  };
  
  // Test request

  var nxRequest = http.request(requestOptions, function(response) {
        var responseData = "";
        response.on('data', function(data) {
              responseData += data;
          });
		response.on('end', function() {
            if (!nuxeoReady) {
                nuxeoReady = true;
            }
            callback(responseData);
		});
  });
  
  nxRequest.on('error', function(data) {
    callback(false);
    if (!nuxeoReady) {
      console.log("[INFO] Nuxeo is not ready yet...");
      setTimeout(function() { 
              exports.checkNuxeo(username, password, callback);
          }, 3000);
    }
  });
	
  nxRequest.end();
	
};


/**
 *
 */
exports.registerWsdl = function(data, callback) {

    try {
    
      // Service notification
      
      console.log("[INFO] Registering: "+data.url);
        
      var body = 'url='+data.url+
          '&environment='+data.environment+
          '&title='+data.servicename+
          '&discoveryTypeBrowsing=Discovered by '+data.session.username;
      var nuxeoUploadOptions = {
	          port : nuxeoDiscovery.port,
	          method : 'POST',
	          host : nuxeoDiscovery.hostname,
	          path : nuxeoDiscovery.href+"service",
	          headers : {
	            'Content-Type': 'application/x-www-form-urlencoded',
	            'Content-Length': body.length,
	            'Authorization': computeAuthorization(data.session.username, data.session.password)
	          }
          };
          
      sendNotification(nuxeoUploadOptions, body, callback);
        
    }
    catch (error) {
      console.error("[ERROR] Client message badly formatted. "+error);
    }
        
};


/**
 * NOTE: Appends the url path to the EasySOA Rest URL
 */
exports.forwardToNuxeo = function(request, response, settings) {
    var easysoaServicestateUrl = url.parse(settings.nuxeoEasySOARest + request.url);
    var requestOptions = {
        'port' : easysoaServicestateUrl.port,
        'method' : request.method,
        'host' : easysoaServicestateUrl.hostname,
        'path' : easysoaServicestateUrl.href,
        'headers' : {
            Authorization: computeAuthorization(request.session.username, request.session.password)
        }
    };
    console.log(requestOptions);
    http.request(requestOptions, function(nxResponse) {
          var responseData = '';
          nxResponse.on('data', function(data) {
              responseData += data;
          });
          nxResponse.on('end', function() {
              response.write(responseData);
              response.end();
          });
    }).end();
};
