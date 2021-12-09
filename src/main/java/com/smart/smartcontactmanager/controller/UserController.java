package com.smart.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;


import javax.servlet.http.HttpSession;

import com.smart.smartcontactmanager.dao.ContactRepository;
import com.smart.smartcontactmanager.dao.UserRepository;
import com.smart.smartcontactmanager.entities.User;
import com.smart.smartcontactmanager.helper.Message;
import com.smart.smartcontactmanager.entities.Contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    // This function will call everytime for everypage and send user data who is
    // logged in
    @ModelAttribute
    public void addCommondata(Model model, Principal principal) {
        String username = principal.getName();
        System.out.println(username);

        // get the data from database
        User user = userRepository.getUserByUserName(username);
        System.out.println("USER " + user);

        // sending data
        model.addAttribute("user", user);
    }

    // this is for user home page
    @RequestMapping("/index")
    public String dashboard(Model model) {
        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }

    // Handler for add contact form
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());

        return "normal/add_contact_form";
    }

    // processing add-contact form
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file, Principal principal, HttpSession session) {

        try {
            String userName = principal.getName();
            User user = this.userRepository.getUserByUserName(userName);

            //processing and uploading file...

            if(file.isEmpty()){
                //if the file is empty then give message to user
                contact.setImage("contact.png");
                System.out.println("File is empty");
            }
            else{
                //upload the file to folder and update the name to contact
                contact.setImage(file.getOriginalFilename());

                File savefile = new ClassPathResource("static/Img").getFile();

                Path path = Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Image file uploaded");
            }

            contact.setUser(user);
            user.getContacts().add(contact);
            this.userRepository.save(user);

            System.out.println("data " + contact);
            //Message Success..........
            session.setAttribute("message", new Message("Your contact is added Successfully!! Add More.", "success"));

        } catch (Exception e) {
            System.out.print("Error" + e.getMessage());
            e.printStackTrace();
            //Message error.......
            session.setAttribute("message", new Message("Something went wrong!! Try Again..", "danger"));
        }
        return "normal/add_contact_form";
    }

    // show contact handler
    //per page=8[n]
    //current page=0[page]
    @GetMapping("show_contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal)
    {
        model.addAttribute("title", "Show User Contacts");
        //to send contact list from database

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        //current page- page
        //contact per page-8

        Pageable pageable= PageRequest.of(page, 8);

        Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);


        model.addAttribute("contacts", contacts);
        model.addAttribute("currentpage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());

        return "normal/show_contacts";
    }

    // Showing Specific Contact Details
    @GetMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal)
    {
        Optional<Contact> contactOptional = contactRepository.findById(cId);
        Contact contact = contactOptional.get();

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        //check if user id is same as contact parent user id, then only send data
        if(user.getId()== contact.getUser().getId())
        {
            model.addAttribute("title", contact.getName());
            model.addAttribute("contact", contact);
        }

        return "normal/contact_detail";
    }

    //Delete Contact handler
    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") Integer cId, Model model, Principal principal, HttpSession session){

        Optional<Contact> contactOptional = this.contactRepository.findById(cId);
        Contact contact = contactOptional.get();

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        if(user.getId()== contact.getUser().getId())
        {
            // contact.setUser(null);
            // this.contactRepository.delete(contact);
            user.getContacts().remove(contact);
            this.userRepository.save(user);
            session.setAttribute("message", new Message("Contact deleted successfully", "success"));
        }

        //rediect to another page
        return "redirect:/user/show_contacts/0";
    }


    //Open Update form Handler
    @PostMapping("/update_contact/{cId}")
    public String updateForm(@PathVariable("cId") Integer cId, Model model)
    {
        model.addAttribute("title", "Update Contact");

        Contact contact = this.contactRepository.findById(cId).get();

        model.addAttribute("contact", contact);

        return "normal/update_form";
    }

    //Update Contact handler
    @PostMapping("/process-update")
    public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file, 
    Model model, HttpSession session, Principal principal){
        
        try {

            //fetch old contact detail
            Contact oldContactDetail = this.contactRepository.findById(contact.getcId()).get();


            if(!file.isEmpty()){
                //rewrite
                //delete old image

                File deleteFile = new ClassPathResource("static/Img").getFile();
                File file1= new File(deleteFile, oldContactDetail.getImage());
                file1.delete();


                //Update new image
                File savefile = new ClassPathResource("static/Img").getFile();
                Path path = Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(file.getOriginalFilename());


            }

            User user = this.userRepository.getUserByUserName(principal.getName());

            contact.setUser(user);

            this.contactRepository.save(contact);

            session.setAttribute("message", new Message("Your Contact is updated...", "success"));

        } catch (Exception e) {
           e.printStackTrace();
        }
        
        return "redirect:/user/"+contact.getcId()+"/contact";
    }

    // Your Profile Handler
    @GetMapping("/profile")
    public String yourProfile(Model model){

        model.addAttribute("title", "Profile Page");

        return "normal/profile";
    }

}
