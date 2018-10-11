package com.apap.tutorial5.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.apap.tutorial5.model.FlightModel;
import com.apap.tutorial5.model.PilotModel;
import com.apap.tutorial5.service.FlightService;
import com.apap.tutorial5.service.PilotService;

@Controller
public class FlightController {
	@Autowired
	private FlightService flightService;
	
	@Autowired
	private PilotService pilotService;
	
	@RequestMapping(value = "/flight/add/{licenseNumber}", method = RequestMethod.GET)
	private String add(@PathVariable(value = "licenseNumber") String licenseNumber, Model model) {
		FlightModel flight = new FlightModel();
		PilotModel pilot = pilotService.getPilotDetailByLicenseNumber(licenseNumber);
		
		ArrayList<FlightModel> list =  new ArrayList<FlightModel>();
		
		list.add(flight);
		pilot.setPilotFlight(list);
		flight.setPilot(pilot);
		
		model.addAttribute("pilot", pilot);
		model.addAttribute("flight", flight);
		return "addFlight";
	}
	
	@RequestMapping(value="/flight/delete", method=RequestMethod.POST)
	private String deleteFLight(@ModelAttribute PilotModel pilot, Model model) {
		for (FlightModel flight : pilot.getPilotFlight()) {
			flightService.deleteFlight(flight.getId());
		}

		return "delete";
	}
	
	@RequestMapping(value = "/flight/add/{licenseNumber}", method=RequestMethod.POST, params= {"flightSubmit"})
	private String addFlightSubmit(@ModelAttribute PilotModel pilot) {
		PilotModel currPilot = pilotService.getPilotDetailByLicenseNumber(pilot.getLicenseNumber());
		for (FlightModel flight : pilot.getPilotFlight()) {
			flight.setPilot(currPilot);
			flightService.addFlight(flight);
		}
		return "add";
}
	
	@RequestMapping(value = "/flight/add", method = RequestMethod.POST)
	private String addFlightSubmit(@ModelAttribute FlightModel flight) {
		
		flightService.addFlight(flight);
		return "add";
	}
	
	@RequestMapping(value="/flight/update/{licenseNumber}/{id}", method=RequestMethod.GET)
	private String updateFlight(@PathVariable(value="licenseNumber", required=true) String licenseNumber,
								@PathVariable(value="id", required=true) long id,
								Model model) {
		PilotModel currPilot = pilotService.getPilotDetailByLicenseNumber(licenseNumber);
		FlightModel currFlight = new FlightModel();
		
		currFlight.setPilot(currPilot);
		currFlight.setId(id);
		
		model.addAttribute("currFlight", currFlight);
		model.addAttribute("currPilot", currPilot);
		return "update-flight";
	}
	
	@RequestMapping(value="/flight/update/submit/{licenseNumber}/{id}", method=RequestMethod.POST)
	private String updateFlightSubmit(@PathVariable(value="licenseNumber", required=true) String licenseNumber,
										@PathVariable(value="id", required=true) Long id, 
										@ModelAttribute FlightModel flight, Model model) {
		flightService.updateFlight(id, flight.getFlightNumber(), flight.getOrigin(), flight.getDestination(),
				flight.getTime());
		
		PilotModel current = pilotService.getPilotDetailByLicenseNumber(licenseNumber);
		List<FlightModel> pilotFlights = current.getPilotFlight();
		
		model.addAttribute("pilot", current);
		model.addAttribute("pilotFlights", pilotFlights);
		
		return "view-pilot";
	}
	
	@RequestMapping(value = "/flight/view", method = RequestMethod.GET)
	private String viewFligt (@RequestParam(value="id", required=true) long id, Model model) {
		FlightModel flight = flightService.getFlightById(id);
		PilotModel pilot = flight.getPilot();
		
		model.addAttribute("flight", flight);
		model.addAttribute("pilot", pilot);
		return "view-flight";
	}
	
	@RequestMapping(value ="/flight/add/{licenseNumber}", method=RequestMethod.POST, params= {"addRow"})
	private String addRow(@ModelAttribute PilotModel pilot, BindingResult bindingResult, Model model) {
		if(pilot.getPilotFlight() == null) {
			pilot.setPilotFlight(new ArrayList<FlightModel>());
		}
		
		pilot.getPilotFlight().add(new FlightModel());
		
		model.addAttribute("pilot", pilot);
		return "addFlight";
	}
	
	@RequestMapping(value ="/flight/add/{licenseNumber}", method=RequestMethod.POST, params= {"removeRow"})
	private String removeRow(@ModelAttribute PilotModel pilot, final BindingResult bindingResult, final HttpServletRequest req, Model model) {
		final Integer rowId = Integer.valueOf(req.getParameter("removeRow"));
		pilot.getPilotFlight().remove(rowId.intValue());
		
		model.addAttribute("pilot" , pilot);
		return "addFlight";
	}

}