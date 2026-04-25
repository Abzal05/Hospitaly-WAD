package com.example.demo.controller.mvc;

import com.example.demo.dto.DoctorFormDto;
import com.example.demo.model.Doctor;
import com.example.demo.repository.HospitalRepository;
import com.example.demo.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/doctors")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDoctorController {

    private final DoctorService doctorService;
    private final HospitalRepository hospitalRepository;

    public AdminDoctorController(DoctorService doctorService,
                                  HospitalRepository hospitalRepository) {
        this.doctorService = doctorService;
        this.hospitalRepository = hospitalRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("doctors", doctorService.findAll());
        return "admin/doctors";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("doctorDto", new DoctorFormDto());
        model.addAttribute("hospitals", hospitalRepository.findAll());
        return "admin/doctor-create";
    }

    @PostMapping("/create")
    public String createSubmit(@Valid @ModelAttribute("doctorDto") DoctorFormDto dto,
                               BindingResult result, Model model,
                               RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            model.addAttribute("hospitals", hospitalRepository.findAll());
            return "admin/doctor-create";
        }
        doctorService.create(dto);
        redirectAttrs.addFlashAttribute("success", "Врач «" + dto.getFirstName() + " " + dto.getLastName() + "» добавлен");
        return "redirect:/admin/doctors";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Doctor d = doctorService.findById(id);
        DoctorFormDto dto = new DoctorFormDto();
        dto.setFirstName(d.getFirstName());
        dto.setLastName(d.getLastName());
        dto.setSpecialty(d.getSpecialty());
        dto.setEmail(d.getEmail());
        dto.setPhone(d.getPhone());
        dto.setHospitalId(d.getHospital() != null ? d.getHospital().getId() : null);
        model.addAttribute("doctorDto", dto);
        model.addAttribute("doctorId", id);
        model.addAttribute("hospitals", hospitalRepository.findAll());
        return "admin/doctor-edit";
    }

    @PostMapping("/{id}/edit")
    public String editSubmit(@PathVariable Long id,
                             @Valid @ModelAttribute("doctorDto") DoctorFormDto dto,
                             BindingResult result, Model model,
                             RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            model.addAttribute("doctorId", id);
            model.addAttribute("hospitals", hospitalRepository.findAll());
            return "admin/doctor-edit";
        }
        doctorService.update(id, dto);
        redirectAttrs.addFlashAttribute("success", "Данные врача обновлены");
        return "redirect:/admin/doctors";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        doctorService.delete(id);
        redirectAttrs.addFlashAttribute("success", "Врач удалён");
        return "redirect:/admin/doctors";
    }
}
