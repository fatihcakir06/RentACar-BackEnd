package com.btkAkademi.rentACar.business.abstracts;

import java.util.List;

import javax.validation.Valid;

import com.btkAkademi.rentACar.business.dtos.CityListDto;
import com.btkAkademi.rentACar.business.dtos.SegmentListDto;
import com.btkAkademi.rentACar.business.requests.segmentRequest.CreateSegmentRequest;
import com.btkAkademi.rentACar.business.requests.segmentRequest.UpdateSegmentRequest;
import com.btkAkademi.rentACar.core.utilities.results.DataResult;
import com.btkAkademi.rentACar.core.utilities.results.Result;

public interface SegmentService {
	DataResult<SegmentListDto> findById(int id);
	DataResult<List<SegmentListDto>> findAll();
	Result add(CreateSegmentRequest createSegmentRsequest);
	Result update( UpdateSegmentRequest createSegmentRequest);
	Result delete(int id);


}
