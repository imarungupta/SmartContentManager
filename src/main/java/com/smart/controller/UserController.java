package com.smart.controller;


import com.smart.controller.helper.Message;
import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    @ModelAttribute
    public void getUserDetailForAll(Model model, Principal principal) {
        /*String username = principal.getName();
        System.out.println("username: " + username);
        User userDetail = userRepository.getUserByUserName(username);
        System.out.println("UserDetail: " + userDetail);

        model.addAttribute("user", userDetail);*/

        // OR in short we can write below line
        model.addAttribute("user", userRepository.getUserByUserName(principal.getName()));
    }

    @RequestMapping(value = "/index")
    public String dashboard(Model model) {

       /* String username = principal.getName();
        System.out.println("username: " + username);
        User userDetail = userRepository.getUserByUserName(username);
        System.out.println("UserDetail: " + userDetail);
         model.addAttribute("user", userDetail);
*/
        model.addAttribute("title", "Dashboard");
        return "normal/user_dashboard";
    }

    @GetMapping(value = "/addContact")
    public String openAddContactForm(Model model) {

        // Since here we need the user detail in each handler so let's create a common method to get this user detail and can be used in each method
        // for we will use @ModelAttribute so let's comment below code
        /* get the user detail from database and send it to page
        userRepository.getUserByUserName(principal.getName());
        model.addAttribute("user", userRepository.getUserByUserName(principal.getName()));*/
        System.out.println("openAddContactForm: handler method ");
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/addCustomerContact";
    }

    @PostMapping("/processContact")
    public String saveContact(@ModelAttribute @Valid Contact myContact,
                              @RequestParam("imageProfile") MultipartFile multipartFile,
                              Principal principal,
                              HttpSession session) {

        System.out.println("Contact: " + myContact);
        // The below line is not required because this method will be called on form submit
        //model.addAttribute("title", "Add Contact");

        /* Here by using the @ModelAttribute we can get the value from the view, which we have already set in the
         openAddContactForm method, so entire contact object will be copy in to myContact form Contact */
        try {
            User userByUserName = this.userRepository.getUserByUserName(principal.getName());
            // Processing and uploading file...
            if (multipartFile.isEmpty()) {
                System.out.println("File is empty");
                myContact.setImage("contact.png");
            } else {
                // Save the file to folder and update the name to contact table
                System.out.println("Les's get the source path, name and the option like save new or replace the file");
                //byte[] data = multipartFile.getBytes();
                //String imageString = Base64.getEncoder().encodeToString(data);
                myContact.setImage(multipartFile.getOriginalFilename());
                File fileName = new ClassPathResource("static/image").getFile();
                Path destinationPath = Paths.get(fileName.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename());

                Files.copy(multipartFile.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("image is uploaded ..");
            }
            myContact.setUser(userByUserName);
            userByUserName.getContactList().add(myContact);
            this.userRepository.save(userByUserName);
            System.out.println("Contact :: " + myContact);
            System.out.println("Data is added in DB");
            session.setAttribute("message",new Message("Contact Added Successfully !!","success"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            session.setAttribute("message",new Message("Something went wrong !!","danger"));
        }
        // Since once the contact save then, show success msg on the same Add Contact page, so return the same page again
        return "normal/addCustomerContact";
    }

        // Handler for showing all contacts
/*
    @GetMapping("/showContactsList")
    public String showAllContacts(Model model, Principal principal) {
        model.addAttribute("title", "All Contacts");
        // Get the user from the principal
        // call the method from repository and pass the user to get the list of contacts
        // set in the model attribute
        // set this model attribute object in the html and get the value of each fields
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        //List<Contact> contactList = userDetail.getContactList();
        // But the above method is the shortcut and not recommended, let's create repository class and declare method
        // to get contact list of the user
        List<Contact> contactsListByUser = this.contactRepository.findContactsByUser(user.getId());
        model.addAttribute("userContacts",contactsListByUser);

        return "normal/user_allContacts";
    }

 */
  // The above handler method is not being used because instead of List we have returned the Pages of Contact for Pagination


    @GetMapping("/showContacts/{currentPage}")
    public String showAllContacts(@PathVariable int currentPage, Model model, Principal principal) {

        model.addAttribute("title", "All Contacts");
        // Get the user from the principal
        // call the method from repository and pass the user to get the list of contacts
        // set in the model attribute
        // set this model attribute object in the html and get the value of each fields
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        //List<Contact> contactList = userDetail.getContactList();
        // But the above method is the shortcut and not recommended, let's create repository class and declare method
        // to get contact list of the user
        //List<Contact> contactsByUser = this.contactRepository.findContactsByUser(user.getId());

        Pageable pageRequest = PageRequest.of(currentPage, 3);
        Page<Contact> contactsByUser = this.contactRepository.findContactsPerPageByUser(user.getId(),pageRequest);

        System.out.println("Contact / Records per page: "+ pageRequest.getPageSize());
        System.out.println("current page number: "+ pageRequest.getPageNumber());
        System.out.println("Total Number of pages: "+ contactsByUser.getTotalPages());

        model.addAttribute("userContacts",contactsByUser);
        model.addAttribute("currentPage",currentPage);
        model.addAttribute("totalPages",contactsByUser.getTotalPages());

        model.addAttribute("ContactPerPage",pageRequest.getPageSize());
        model.addAttribute("CurrentPageNumber",pageRequest.getPageNumber());

        return "normal/user_allContacts";
    }
    // Showing particular contact detail on email link is clicked

    @RequestMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable Integer cId , Model model, Principal principal) {
        Optional<Contact> userById = contactRepository.findById(cId);
        Contact contact = null;
        if (userById.isPresent()) {
            contact = userById.get();
        }
        String username = principal.getName();
        System.out.println("username: "+ username);
        int id = this.userRepository.getUserByUserName(username).getId();
        System.out.println("user id: "+ id);

/* This condition we have written, because other user also
can see the contact detail on just changing id in the URL, and this happens if we usg GetMapping http method,
so to solve this issue we should use PostMapping */

        if(id==contact.getUser().getId()) { System.out.println(" in the userid if condition..");
            model.addAttribute("contact", contact);
            model.addAttribute("title",contact.getName()); // the name will show in the title.
        }
        return "normal/contact_detail";
    }

    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable Integer cid, Model model, HttpSession session) {

        Optional<Contact> userById = this.contactRepository.findById(cid);
        Contact contact = null;
        if (userById.isPresent()) {
            contact = userById.get();
        }
        assert contact != null; // Here we are just un-linking the user with contact so that it can be deleted successfully

        contact.setUser(null);  // This line is required only when the message is getting deleted because of linking
        this.contactRepository.delete(contact);
        // Since the above statement do not return anything, so there is no scope to set in Model Attribute,
        // and to so the message on the successfully delete, will use session
        session.setAttribute("message",new Message("Contact Deleted Successfully !","success"));
        // Once contact get deleted then to redirect the page to showContact page , we will use redirect as given below
        return "redirect:/user/showContacts/0";
    }
    // open update form.

    @RequestMapping(value = "/updateContact/{cid}", method = RequestMethod.POST)
    public String openUpdateContactForm(@PathVariable Integer cid,Model model,Principal principal) {

        String name = principal.getName();
        Optional<Contact> byId = this.contactRepository.findById(cid);
        Contact contact = null;
        if (byId.isPresent()) {
            contact = byId.get();
        }
        model.addAttribute("contactUpdate", contact);
        model.addAttribute("title", "UpdateContact");
        return "normal/update_contact";
    }
    // Since the contact is already stored in the model in the last method openUpdateContactForm, here in this method we will j
    // just receive contact including value using @ModelAttribute
    // Here we are trying to update image so, will do tha same thing what we had done in case of saving image first time, i.e.
    // taken image in the form of MultipartFile object, when we choose any image then it will assign to MultipartFile via RequestParam
    @PostMapping("/saveUpdateContact")
    public String saveUpdatedContact(@ModelAttribute Contact contact,
                                     @RequestParam("imageProfile") MultipartFile multipartFile,
                                     Model model,
                                     HttpSession session,Principal principal){
        System.out.println("Contact- Name: "+contact.getName());
        System.out.println("Contact- Id : "+contact.getCId());

        // Let's get the old detail first from DB
        Contact oldContactDetail = contactRepository.findById(contact.getCId()).get();
        System.out.println("oldContactDetail: "+oldContactDetail.getCId());
        if(!multipartFile.isEmpty()){
            // when file is not empty then overwrite the new one with old pic and delete the old one
            System.out.println("File is not empty then delete the old and update with new one");
            try {
                File deleteFile = new ClassPathResource("static/image").getFile();
                File file= new File(deleteFile,oldContactDetail.getImage());
                boolean delete = file.delete();
                System.out.println("Result:: "+delete);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Get the file path using ClassPathResource and update the image
            try {
                File saveFile = new ClassPathResource("static/image").getFile();
                System.out.println("saveFile: "+saveFile);
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename());
                System.out.println("path : "+path);
                // Copy multipart file getting from request to destination path
                Files.copy(multipartFile.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(multipartFile.getOriginalFilename());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else {
           // overwrite the old file  and do not delete the old pic
            contact.setImage(oldContactDetail.getImage());
        }

        // Note entire updated contact already came into contact, so simple we can save the contact
        User userByUserName = this.userRepository.getUserByUserName(principal.getName());
        contact.setUser(userByUserName);
        this.contactRepository.save(contact); //But still the image could not be updated,lets update the image below using setter

        // So here first we need to check whether user wants to update his profile pic or not, so if user do not select the pic
        // then in this case we just need to update existing old image, otherwise we will overwrite the new one with old one.
         session.setAttribute("message",new Message("Contact is updated Successfully!!","success"));
        return "redirect:/user/showContacts/0";
    }
   // Your Profile Handler
    @GetMapping("/profile")
    public String yourProfile(){
        return "normal/user_profile";
    }

    // Your Profile Handler
    @GetMapping("/settings")
    public String settings(){
        return "normal/settings";
    }
}
