
package com.mycompany.service;

import java.util.List;

import com.mycompany.domain.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ContactService {

	public List<Contact> findAll();
	
	public Contact findById(Long id);
	
	public Contact save(Contact contact);

	public void delete(Long id);

	public Page<Contact> findAllByPage(Pageable pageable);
	
}
