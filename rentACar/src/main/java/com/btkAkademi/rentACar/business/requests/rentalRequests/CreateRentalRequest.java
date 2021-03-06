package com.btkAkademi.rentACar.business.requests.rentalRequests;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRentalRequest {

	private LocalDate rentDate;
	private Integer rentedKilometer;
	private int customerId;
	private int carId;
	private int segmentId;
	private int pickUpCityId;
	private int returnCityId;
}
