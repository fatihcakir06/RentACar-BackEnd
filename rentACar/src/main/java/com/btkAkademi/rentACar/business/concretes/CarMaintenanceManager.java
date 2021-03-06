package com.btkAkademi.rentACar.business.concretes;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.btkAkademi.rentACar.business.abstracts.CarMaintenanceService;
import com.btkAkademi.rentACar.business.abstracts.CarService;
import com.btkAkademi.rentACar.business.abstracts.RentalService;
import com.btkAkademi.rentACar.business.constants.Messages;
import com.btkAkademi.rentACar.business.dtos.CarMaintenanceListDto;
import com.btkAkademi.rentACar.business.requests.carMaintananceRequests.CreateCarMaintenanceRequest;
import com.btkAkademi.rentACar.business.requests.carMaintananceRequests.UpdateCarMaintananceRequest;
import com.btkAkademi.rentACar.core.utilities.business.BusinessRules;
import com.btkAkademi.rentACar.core.utilities.mapping.ModelMapperService;
import com.btkAkademi.rentACar.core.utilities.results.DataResult;
import com.btkAkademi.rentACar.core.utilities.results.ErrorDataResult;
import com.btkAkademi.rentACar.core.utilities.results.ErrorResult;
import com.btkAkademi.rentACar.core.utilities.results.Result;
import com.btkAkademi.rentACar.core.utilities.results.SuccessDataResult;
import com.btkAkademi.rentACar.core.utilities.results.SuccessResult;
import com.btkAkademi.rentACar.dataAccess.abstracts.CarMaintenanceDao;
import com.btkAkademi.rentACar.entities.concretes.CarMaintenance;

@Service
public class CarMaintenanceManager implements CarMaintenanceService {
	
	private CarMaintenanceDao carMaintananceDao;
	private ModelMapperService modelMapperService;
	private RentalService rentalService;
	private CarService carService;


	@Autowired
	public CarMaintenanceManager(CarMaintenanceDao carMaintananceDao, ModelMapperService modelMapperService,
			@Lazy RentalService rentalService, CarService carService) {
		super();
		this.carMaintananceDao = carMaintananceDao;
		this.modelMapperService = modelMapperService;
		this.rentalService = rentalService;
		this.carService = carService;
	}

	
	@Override
	public DataResult<List<CarMaintenanceListDto>> findAll() {
		List<CarMaintenance> carMaintananceList = this.carMaintananceDao.findAll();
		List<CarMaintenanceListDto> response = carMaintananceList.stream()
				.map(carMaintanance -> modelMapperService.forDto().map(carMaintanance, CarMaintenanceListDto.class))
				.collect(Collectors.toList());

		return new SuccessDataResult<List<CarMaintenanceListDto>>(response);
	}

	
	@Override
	public DataResult<List<CarMaintenanceListDto>> findAllByCarId(int id) {
		List<CarMaintenance> carMaintananceList = this.carMaintananceDao.findAllByCarId(id);
		List<CarMaintenanceListDto> response = carMaintananceList.stream()
				.map(carMaintanance -> modelMapperService.forDto().map(carMaintanance, CarMaintenanceListDto.class))
				.collect(Collectors.toList());

		return new SuccessDataResult<List<CarMaintenanceListDto>>(response);
	}


	@Override
	public DataResult<CarMaintenanceListDto> findById(int id) {
		if (carMaintananceDao.existsById(id)) {
			CarMaintenance carMaintenance = carMaintananceDao.findById(id).get();
			CarMaintenanceListDto response = modelMapperService.forDto().map(carMaintenance,
					CarMaintenanceListDto.class);
			return new SuccessDataResult<CarMaintenanceListDto>(response);
		}
		return new ErrorDataResult<CarMaintenanceListDto>(Messages.notFound);
	}

	
	@Override
	public Result add(CreateCarMaintenanceRequest createCarMaintananceRequest) {
		Result result = BusinessRules.run(checkIfCarIsExists(createCarMaintananceRequest.getCarId()),
				checkIfCarIsRented(createCarMaintananceRequest.getCarId()),
				checkIfCarIsAlreadyInMaintanance(createCarMaintananceRequest.getCarId()));
		if (result != null) {
			return result;
		}

		CarMaintenance carMaintanance = this.modelMapperService.forRequest().map(createCarMaintananceRequest,
				CarMaintenance.class);
	
		carMaintanance.setId(0);

		this.carMaintananceDao.save(carMaintanance);
		return new SuccessResult(Messages.carMaintananceAdded);
	}

	
	@Override
	public Result update(UpdateCarMaintananceRequest updateCarMaintananceRequest) {
		Result result = BusinessRules.run(checkIfCarIsExists(updateCarMaintananceRequest.getCarId()),
				checkIfCarIsRented(updateCarMaintananceRequest.getCarId()));
		if (result != null) {
			return result;
		}
		CarMaintenance carMaintanance = this.modelMapperService.forRequest().map(updateCarMaintananceRequest,
				CarMaintenance.class);

		this.carMaintananceDao.save(carMaintanance);
		return new SuccessResult(Messages.carMaintananceUpdated);
	}


	@Override
	public Result delete(int id) {
		if (carMaintananceDao.existsById(id)) {
			carMaintananceDao.deleteById(id);
			return new SuccessResult(Messages.carMaintananceDeleted);
		} else
			return new ErrorResult(Messages.notFound);
	}

	
	@Override
	public boolean isCarInMaintenance(int carId) {
		if (carMaintananceDao.findByCarIdAndMaintenanceEndIsNull(carId) != null) {
			return true;
		} else
			return false;
	}


	private Result checkIfCarIsExists(int carId) {
		if (!carService.findCarById(carId).isSuccess()) {
			return new ErrorResult(Messages.carIdNotExists);
		} else
			return new SuccessResult();
	}


	private Result checkIfCarIsRented(int carId) {
		if (rentalService.isCarRented(carId)) {
			return new ErrorResult(Messages.carRented);
		} else
			return new SuccessResult();
	}

	
	private Result checkIfCarIsAlreadyInMaintanance(int carId) {
		if (isCarInMaintenance(carId)) {
			return new ErrorResult(Messages.carInMaintanance);
		} else
			return new SuccessResult();
	}

}
