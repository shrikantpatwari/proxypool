package com.woo.proxypool.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/proxy")
public class ProxyPoolController {
    @Operation(summary = "Get proxy from proxy pool", description = "", tags = {"getProxy"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proxy returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "User already exists")})
    @GetMapping()
    public ResponseEntity<String> getProxy() {
        return new ResponseEntity<String>("true", HttpStatus.OK);
    }
}

/**
 * TODO
 * 1. create 3 queues, of millisecond granularity. each element will be time in millisecond
 *     seconds_queue: 50
 * 	minutes_queue : 1200
 * 	day_queue: 160000
 *
 * 2. check seconds:
 * 	if seconds_queue length is less than 50: pass
 * 	else:
 * 		take time difference between queue oldest element and newst element. is greater than second. remove last element and add new element with current time. : pass
 * 		if time difference is less than second we have already sent 50 orders within this second: not pass. check for next proxy.
 *
 * 3. check for minut in same way. with queue size of 1200 instead of 50 and time difference is minut.
 *
 * 4. check for day in same way. with queue size of 160000 instead of 50 and time difference is day.
 */
