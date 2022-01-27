package com.woo.proxypool.controller;

import com.bugsnag.Bugsnag;
import com.woo.proxypool.service.api.ProxyPoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class ProxyPoolController {
    @Autowired
    ProxyPoolService proxyPoolService;

    @Autowired
    Bugsnag bugsnag;

    @Operation(summary = "Get proxy from proxy pool", description = "", tags = {"getProxy"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proxy returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "User already exists")})
    @RequestMapping( value = "/proxy", method = {RequestMethod.GET}, produces = "text/plain")
    public ResponseEntity<String> getAProxy() {
        return new ResponseEntity<String>(proxyPoolService.getAProxy(), HttpStatus.OK);
    }

    @Operation(summary = "Get proxy from user proxies", description = "", tags = {"getProxyByUserId"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proxy returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "User already exists")})
    @RequestMapping( value = "/user-proxy", method = {RequestMethod.GET}, produces = "text/plain")
    public ResponseEntity<String> getAProxyForUser(@RequestParam String userid) {
        ResponseEntity<String> responseEntity;
        try {
            responseEntity = new ResponseEntity<String>(proxyPoolService.getProxyForUser(userid), HttpStatus.OK);
        } catch (Exception e) {
            bugsnag.notify(e);
            responseEntity = new ResponseEntity<>("Error: Something went wrong, try again", HttpStatus.SERVICE_UNAVAILABLE);
        }
        return responseEntity;
    }

}

/**
 * TODO
 * 1. create 3 queues, of millisecond granularity. each element will be time in millisecond
 *     seconds_queue: 50
 * 	   minutes_queue : 1200
 * 	   day_queue: 160000
 *
 * 2. check seconds:
 * 	   if
 * 	        seconds_queue length is less than 50: pass
 * 	   else:
 * 		    take time difference between queue oldest element and newest element. is greater than second. remove last element and add new element with current time. : pass
 * 		    if time difference is less than second we have already sent 50 orders within this second: not pass. check for next proxy.
 *
 * 3. check for minute in same way. with queue size of 1200 instead of 50 and time difference is minute.
 *
 * 4. check for day in same way. with queue size of 160000 instead of 50 and time difference is day.
 */
