package com.nt.service;

import java.util.List;

import com.nt.entity.User;
import com.nt.payload.AddressDTO;

public interface IAddressService {

	public AddressDTO createAddress(AddressDTO addressDTO, User user);

	public List<AddressDTO> getAddresses();

	public AddressDTO getAddressById(Long addressId);

	public List<AddressDTO> getUserAddresses(User user);

	public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO);

	public String deleteAddress(Long addressId);

}
