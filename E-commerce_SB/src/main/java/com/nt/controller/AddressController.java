package com.nt.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nt.entity.User;
import com.nt.payload.AddressDTO;
import com.nt.service.IAddressService;
import com.nt.util.AuthUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AddressController {
	
	@Autowired
	private IAddressService addressService;
	
	@Autowired
	private AuthUtil authUtil;

	@PostMapping("/addresses")
	public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO){
		User user=authUtil.loggedInUser();
		AddressDTO savedAddressDTO=addressService.createAddress(addressDTO,user);
		return new ResponseEntity<AddressDTO>(savedAddressDTO,HttpStatus.CREATED);
	}
	
	@GetMapping("/addresses")
	public ResponseEntity<List<AddressDTO>> getAddresses(){
		List<AddressDTO> addressList=addressService.getAddresses();
		return new ResponseEntity<List<AddressDTO>>(addressList,HttpStatus.OK);
	}
	
	@GetMapping("/addresses/{addressId}")
	public ResponseEntity<AddressDTO> getAddressById(@PathVariable(name = "addressId") Long addressId){
		AddressDTO address=addressService.getAddressById(addressId);
		return new ResponseEntity<AddressDTO>(address,HttpStatus.OK);
	}
	
	@GetMapping("/users/addresses")
	public ResponseEntity<List<AddressDTO>> getUserAddresses(){
		User user=authUtil.loggedInUser();
		List<AddressDTO> addressList=addressService.getUserAddresses(user);
		return new ResponseEntity<List<AddressDTO>>(addressList,HttpStatus.OK);
	}
	
	@PutMapping("/addresses/{addressId}")
	public ResponseEntity<AddressDTO> updateAddress(@PathVariable(name = "addressId") Long addressId,
			@RequestBody AddressDTO addressDTO){
		AddressDTO updatedAddress=addressService.updateAddress(addressId,addressDTO);
		return new ResponseEntity<AddressDTO>(updatedAddress,HttpStatus.OK);
	}
}
