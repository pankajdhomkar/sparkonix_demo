package com.sparkonix.entity.dto;

import com.sparkonix.entity.CompanyDetail;

/**
 * this class used in manage Customers view page
 * 
 * @author Satyawan
 */
public class CustomerDetailsDTO {

	private CompanyDetail companyDetail;
	private long factoryLocationsCount;
	private long machinesCount;
	private long operatorsCount;

	public CompanyDetail getCompanyDetail() {
		return companyDetail;
	}

	public void setCompanyDetail(CompanyDetail companyDetail) {
		this.companyDetail = companyDetail;
	}

	public long getFactoryLocationsCount() {
		return factoryLocationsCount;
	}

	public void setFactoryLocationsCount(long factoryLocationsCount) {
		this.factoryLocationsCount = factoryLocationsCount;
	}

	public long getMachinesCount() {
		return machinesCount;
	}

	public void setMachinesCount(long machinesCount) {
		this.machinesCount = machinesCount;
	}

	public long getOperatorsCount() {
		return operatorsCount;
	}

	public void setOperatorsCount(long operatorsCount) {
		this.operatorsCount = operatorsCount;
	}

}
