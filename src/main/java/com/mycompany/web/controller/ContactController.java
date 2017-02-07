
package com.mycompany.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.validation.Valid;

import com.mycompany.web.form.Message;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mycompany.domain.Contact;
import com.mycompany.service.ContactService;
import com.mycompany.web.form.ContactGrid;
import com.google.common.collect.Lists;


@RequestMapping("/contacts")
@Controller
public class ContactController {

	final Logger logger = LoggerFactory.getLogger(ContactController.class);	
	
	@Autowired
	MessageSource messageSource;
	
	@Autowired
	private ContactService contactService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String list(Model uiModel) {
		logger.info("Listing contacts");	
		
		List<Contact> contacts = contactService.findAll();
		uiModel.addAttribute("contacts", contacts);
		
		logger.info("Number of contacts: " + contacts.size());
		
		return "contacts/list";
	}

	/**
	 * Displays contact details.
     */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, Model uiModel) {
        Contact contact = contactService.findById(id);
		uiModel.addAttribute("contact", contact);
        return "contacts/show";
    }

	/**
	 * Updates contact.
     * @param file the photo of the contact.
     */
	@RequestMapping(value = "/{id}", params = "form", method = RequestMethod.POST)
    public String update(@Valid Contact contact, BindingResult bindingResult, Model uiModel, 
    		HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes, Locale locale,
    		@RequestParam(value="file", required=false) Part file) {
		logger.info("Updating contact");
        if (bindingResult.hasErrors()) {
        	uiModel.addAttribute("message", new Message("error", messageSource.getMessage("contact_save_fail", new Object[]{}, locale)));
            uiModel.addAttribute("contact", contact);
            return "contacts/update";
        }
        uiModel.asMap().clear();
        redirectAttributes.addFlashAttribute("message", new Message("success", messageSource.getMessage("contact_save_success", new Object[]{}, locale)));        

        // Process upload file
        if (file != null) {
			logger.info("File name: " + file.getName());
			logger.info("File size: " + file.getSize());
			logger.info("File content type: " + file.getContentType());
			byte[] fileContent = null;
			try {
				InputStream inputStream = file.getInputStream();
				if (inputStream == null) logger.info("File inputstream is null");
				fileContent = IOUtils.toByteArray(inputStream);
				contact.setPhoto(fileContent);
			} catch (IOException ex) {
				logger.error("Error saving uploaded file");
			}
			contact.setPhoto(fileContent);
		}          
        
        contactService.save(contact);
        return "redirect:/contacts/" + contact.getId();
    }

	/**
	 * Deletes contact by id.
     */
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
	public String delete(RedirectAttributes redirectAttributes, Locale locale,
						 @PathVariable Long id) {
		logger.info("Deleting contact");

		redirectAttributes.addFlashAttribute("message", new Message("success", messageSource.getMessage("contact_delete_success", new Object[]{}, locale)));

		contactService.delete(id);
		return "redirect:/contacts";
	}

	/**
	 * Displays client form for updating contact.
	 * @param id
	 * @param uiModel
     * @return
     */
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
		uiModel.addAttribute("contact", contactService.findById(id));
		return "contacts/update";
	}


	/**
	 * Creates new contact.
     * @param file the photo of the contact.
     * @return
     */
	@RequestMapping(params = "form", method = RequestMethod.POST)
    public String create(@Valid Contact contact, BindingResult bindingResult, Model uiModel, 
    		HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes, Locale locale,
    		@RequestParam(value="file", required=false) Part file) {
		logger.info("Creating contact");
        if (bindingResult.hasErrors()) {
			uiModel.addAttribute("message", new Message("error", messageSource.getMessage("contact_save_fail", new Object[]{}, locale)));
            uiModel.addAttribute("contact", contact);
            return "contacts/create";
        }
        uiModel.asMap().clear();
        redirectAttributes.addFlashAttribute("message", new Message("success", messageSource.getMessage("contact_save_success", new Object[]{}, locale)));

        logger.info("Contact id: " + contact.getId());
        
        // Process upload file
        if (file != null) {
			logger.info("File name: " + file.getName());
			logger.info("File size: " + file.getSize());
			logger.info("File content type: " + file.getContentType());
			byte[] fileContent = null;
			try {
				InputStream inputStream = file.getInputStream();
				if (inputStream == null) logger.info("File inputstream is null");
				fileContent = IOUtils.toByteArray(inputStream);
				contact.setPhoto(fileContent);
			} catch (IOException ex) {
				logger.error("Error saving uploaded file");
			}
			contact.setPhoto(fileContent);
		}          
        
        contactService.save(contact);
        return "redirect:/contacts/" + contact.getId();
    }

	/**
	 * Displays client form for creating new contact.
	 * @param uiModel
	 * @return
     */
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model uiModel) {
		Contact contact = new Contact();
        uiModel.addAttribute("contact", contact);
        return "contacts/create";
    }

	/**
	 * Support pagination for front-end grid.
	 * 'rows', 'sidx', 'sord' - default parameters names sent by jqGrid library.
	 * @return JSON object described by ContactGrid class.
     */
	@RequestMapping(value = "/listgrid", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public ContactGrid listGrid(@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "rows", required = false) Integer rows,
			@RequestParam(value = "sidx", required = false) String sortBy,
			@RequestParam(value = "sord", required = false) String order) {
		
		logger.info("Listing contacts for grid with page: {}, rows: {}", page, rows);
		logger.info("Listing contacts for grid with sort: {}, order: {}", sortBy, order);

		Sort sort = null;
		String orderBy = sortBy;
		if (orderBy != null && orderBy.equals("birthDateString")) orderBy = "birthDate";
		
		if (orderBy != null && order != null) {
			if (order.equals("desc")) {
				sort = new Sort(Sort.Direction.DESC, orderBy);
			} else
				sort = new Sort(Sort.Direction.ASC, orderBy);
		}
		
		// page number for Spring Data JPA starts with 0, while jqGrid starts with 1
		PageRequest pageRequest = null;
		
		if (sort != null) {
			pageRequest = new PageRequest(page - 1, rows, sort);
		} else {
			pageRequest = new PageRequest(page - 1, rows);
		}
				
		Page<Contact> contactPage = contactService.findAllByPage(pageRequest);
		
		// Construct the grid data that will return as JSON data
		ContactGrid contactGrid = new ContactGrid();
		
		contactGrid.setCurrentPage(contactPage.getNumber() + 1);
		contactGrid.setTotalPages(contactPage.getTotalPages());
		contactGrid.setTotalRecords(contactPage.getTotalElements());
		contactGrid.setContactData(Lists.newArrayList(contactPage.iterator()));
		
		return contactGrid;
	}

	/**
	 * Returns contact photo to display.
	 * @param id
	 * @return
     */
	@RequestMapping(value = "/photo/{id}", method = RequestMethod.GET)
	@ResponseBody
	public byte[] downloadPhoto(@PathVariable("id") Long id) {
		
		Contact contact = contactService.findById(id);
        
        if (contact.getPhoto() != null) {
    		logger.info("Downloading photo for id: {} with size: {}", contact.getId(), contact.getPhoto().length);
        }
        
		return contact.getPhoto();
	}
	
}
