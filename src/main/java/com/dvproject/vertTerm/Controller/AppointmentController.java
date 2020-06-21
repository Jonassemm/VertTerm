

    package com.dvproject.vertTerm.Controller;

    import com.dvproject.vertTerm.Model.Appointment;
    import com.dvproject.vertTerm.Model.AppointmentStatus;
    import com.dvproject.vertTerm.Model.Employee;
    import com.dvproject.vertTerm.Model.User;
    import com.dvproject.vertTerm.Model.Warning;
    import com.dvproject.vertTerm.Service.AppointmentService;
    import com.dvproject.vertTerm.Service.UserService;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.web.bind.annotation.*;

    import java.security.Principal;
    import java.util.Collection;
    import java.util.Date;
    import java.util.List;

    @RestController
    @RequestMapping("/Appointments")
    public class AppointmentController {
    	@Autowired
    	AppointmentService service;

    	@Autowired
    	private UserService userService;

    	@GetMapping()
    	public @ResponseBody List<Appointment> get() {
    		return service.getAll();
    	}

    	@GetMapping("/{id}")
    	public @ResponseBody Appointment get(@PathVariable String id) {
    		return service.getById(id);
    	}
    	
    	@GetMapping("/Own")
    	public @ResponseBody List<Appointment> getOwnAppointments(Principal principal) {
    		Collection<? extends GrantedAuthority> auth = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    		// TODO: test authority
    		if (principal == null) {
    			throw new IllegalArgumentException("No principal available");
    		}
    		
    		User user = userService.getOwnUser(principal);
    		String id = user.getId();
    		List<Appointment> appointments = service.getAppointmentsByUserid(id);
    		if (user instanceof Employee)
    			appointments.addAll(service.getAppointmentsByEmployeeid(id));
    		
    		return appointments;
    	}
    	
    	@GetMapping("/Resources/{resourceId}")
    	public @ResponseBody List<Appointment> getByResourceId(
    			@PathVariable String resourceId,
    			@RequestParam(required = false) Date starttime,
    			@RequestParam(required = false) Date endtime,
    			@RequestParam(required = false, defaultValue = "PLANNED") AppointmentStatus status) {
    		List<Appointment> appointments = null;
    		
    		if (starttime == null || endtime == null) {
    			appointments = service.getAppointmentsByResourceid(resourceId);
    		} else {
    			appointments = service.getAppointmentsOfBookedResourceInTimeinterval(resourceId, starttime, endtime, status);
    		}
    		
    		return appointments;
    	}

    	@GetMapping("/user/{userid}")
    	public List<Appointment> getAppointmentsWithUserInTimeInterval(
    			@PathVariable String userid,
    			@RequestParam(required = false) Date starttime, 
    			@RequestParam(required = false) Date endtime) {
    		List<Appointment> appointments = null;
    		boolean isEmployee = userService.getById(userid) instanceof Employee;

    		if (starttime == null || endtime == null) {
    			appointments = service.getAppointmentsByUserid(userid);

    			if (isEmployee)
    				appointments.addAll(service.getAppointmentsByEmployeeid(userid));
    		} else if (starttime != null && endtime != null) {
    			appointments = service.getAppointmentsWithUseridAndTimeInterval(userid, starttime, endtime);
    			
    			if (isEmployee)
    				appointments.addAll(service.getAppointmentsOfBookedEmployeeInTimeinterval(userid, starttime, endtime,
    						AppointmentStatus.PLANNED));
    		}

    		return appointments;
    	}

    	@GetMapping(path = { "/Warnings/{userid}", "/Warnings", "/Warnings/" })
    	public List<Appointment> getAppointmentsWithWarnings(
    			@PathVariable(required = false) String userid,
    			@RequestBody(required = true) List<Warning> warnings) {
    		List<Appointment> appointments = null;

    		if (userid == null || userid.equals("")) {
    			appointments = service.getAppointmentsByWarnings(warnings);
    		} else {
    			appointments = service.getAppointmentsByWarningsAndId(userid, warnings);
    		}

    		return appointments;
    	}

    	@GetMapping("/status/{status}")
    	public List<Appointment> getAppointmentsInTimeInterval(
    			@PathVariable(required = false) AppointmentStatus status,
    			@RequestParam Date starttime, 
    			@RequestParam Date endtime) {
    		List<Appointment> retVal = null;

    		if (status == null) {
    			retVal = service.getAppointmentsInTimeInterval(starttime, endtime);
    		} else {
    			retVal = service.getAppointmentsInTimeIntervalWithStatus(starttime, endtime, status);
    		}

    		return retVal;
    	}
		
        //@GetMapping("/ResEmp")
        //public @ResponseBody Res_Emp getAvailableResourcesAndEmployees(@RequestBody Appointmentgroup group,
        //		@RequestParam Date startdate,@RequestParam Date enddate)
        //{
        //	return service.getAvailableResourcesAndEmployees(group, startdate, enddate);
        //}
		
    	@PostMapping()
    	public @ResponseBody Appointment create(@RequestBody Appointment newAppointment) {
    		return service.create(newAppointment);
    	}

    	@PutMapping("/{id}")
    	public Appointment UpdateAppointment(@RequestBody Appointment newAppointment) {
    		return service.update(newAppointment);
    	}

    	@PutMapping("/{id}/{customerIsWaiting}")
    	public boolean setCustomerIsWaiting(
    			@PathVariable String id, 
    			@PathVariable boolean customerIsWaiting) {
    		return service.setCustomerIsWaiting(id, customerIsWaiting);
    	}

    	@DeleteMapping("/{id}")
    	public boolean DeleteAppointment(@PathVariable String id) {
    		return service.delete(id);
    	}
    }