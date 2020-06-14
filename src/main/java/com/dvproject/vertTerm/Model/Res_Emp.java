package com.dvproject.vertTerm.Model;

import java.util.List;

public class Res_Emp {
	
	private List<Resource> ResourcesList;
	private  List<Employee> EmployeesList;
	
	
	public List<Resource> getResources() {
		return ResourcesList;
	}

	public void setResources(List<Resource> Resources) {
		this.ResourcesList = Resources;
	}

	public List<Employee> getEmployees() {
		return EmployeesList;
	}

	public void setEmployees(List<Employee> Employees) {
		this.EmployeesList = Employees;
	}

}