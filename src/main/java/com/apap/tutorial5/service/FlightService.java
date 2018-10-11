
package com.apap.tutorial5.service;

import java.sql.Date;

import com.apap.tutorial5.model.FlightModel;

public interface FlightService {
	void addFlight(FlightModel flight);
	void deleteFlight(long id);
	void updateFlight(long id, String flightNumber, String origin, String destination, Date time);
	FlightModel getFlightById(long id);
}