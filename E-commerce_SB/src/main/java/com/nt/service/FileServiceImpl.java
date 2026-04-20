package com.nt.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService{
	
	@Override
	public String uploadImage(String path, MultipartFile file) throws IOException {
		//File names of current / original file
		String originalFileName=file.getOriginalFilename();
		if (originalFileName == null) {
	        throw new RuntimeException("File name is null");
	    }
		
		//Generate a unique file name
		String randomId=UUID.randomUUID().toString();
		//mat.jpg->123->123.jpg
		String fileName=randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
		String filePath=path+File.separator+fileName; //Here separator means it adds // like images//filename
		
		//check if path exists or create 
		File folder=new File(path);
		if(!folder.exists()) {
			folder.mkdir();
		}
		
	
		//upload to server
		Files.copy(file.getInputStream(), Paths.get(filePath)); //copy uploaded file data and save it to a specific path on server
		
		//return file name
		return fileName;
	}

}
