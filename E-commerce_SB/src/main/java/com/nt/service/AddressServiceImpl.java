package com.nt.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nt.entity.Address;
import com.nt.entity.User;
import com.nt.exception.APIException;
import com.nt.exception.ResourceNotFoundException;
import com.nt.payload.AddressDTO;
import com.nt.repository.IAddressRepository;
import com.nt.repository.UserRepository;

@Service
public class AddressServiceImpl implements IAddressService {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private IAddressRepository addressRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public AddressDTO createAddress(AddressDTO addressDTO, User user) {
		//converting address to address entity
		Address address = modelMapper.map(addressDTO, Address.class);

		//setting the address to users and address entity as it is bidirectional
		List<Address> addList = user.getAddresses(); //getting the existing address
		addList.add(address); //adding new add to list
		user.setAddresses(addList); //setting new addlist to user
		address.setUser(user); //setting user to address above line and this is because of bidirectional

		Address savedAddress = addressRepository.save(address);
		AddressDTO addDTO = modelMapper.map(savedAddress, AddressDTO.class);
		return addDTO;
	}

	@Override
	public List<AddressDTO> getAddresses() {
		List<Address> addressList = addressRepository.findAll();
		if (addressList.isEmpty()) {
			throw new APIException("Addresses are not added for this user!");
		}
		List<AddressDTO> addressDTOs = addressList.stream().map(add -> {
			AddressDTO addressDTO = modelMapper.map(add, AddressDTO.class);
			return addressDTO;
		}).collect(Collectors.toList());
		return addressDTOs;
	}

	@Override
	public AddressDTO getAddressById(Long addressId) {
		Address address = addressRepository.findById(addressId)
				.orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
		AddressDTO addressDTO = modelMapper.map(address, AddressDTO.class);
		return addressDTO;
	}

	@Override
	public List<AddressDTO> getUserAddresses(User user) {
		List<Address> addresses = user.getAddresses();
		if (addresses.isEmpty()) {
			throw new APIException("Addresses are not added for this user!");
		}
		List<AddressDTO> addressDTOs = addresses.stream().map(add -> {
			AddressDTO address = modelMapper.map(add, AddressDTO.class);
			return address;
		}).collect(Collectors.toList());
		return addressDTOs;
	}

	@Override
	public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
		Address addressFromdb = addressRepository.findById(addressId)
				.orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
		addressFromdb.setCity(addressDTO.getCity());
		addressFromdb.setPincode(addressDTO.getPincode());
		addressFromdb.setState(addressDTO.getState());
		addressFromdb.setCountry(addressDTO.getCountry());
		addressFromdb.setStreet(addressDTO.getStreet());
		addressFromdb.setBuildingName(addressDTO.getBuildingName());
		Address updatedAddress = addressRepository.save(addressFromdb);
		
		//adding it to user;
		User user = updatedAddress.getUser();
		user.getAddresses().removeIf(add->add.getAddressId().equals(addressId));
		user.getAddresses().add(updatedAddress);
		userRepository.save(user);
		return modelMapper.map(updatedAddress, AddressDTO.class);
	}

	@Override
	public String deleteAddress(Long addressId) {
		Address addressFromDB=addressRepository.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address","addressId",addressId));
		User user=addressFromDB.getUser();
		user.getAddresses().removeIf(add->add.getAddressId().equals(addressId));
		userRepository.save(user);
		addressRepository.delete(addressFromDB);
		return "Address deleted successfully with addressId "+addressId;
	}

}
