
package com.mycompany.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.mycompany.domain.Contact;


public interface ContactRepository extends PagingAndSortingRepository<Contact, Long> {

}
