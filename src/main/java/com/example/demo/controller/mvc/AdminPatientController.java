package com.example.demo.controller.mvc;

import com.example.demo.dto.PatientFormDto;
import com.example.demo.model.Patient;
import com.example.demo.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/patients")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPatientController {

    private final PatientService patientService;

    public AdminPatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("patients", patientService.findAll());
        return "admin/patients";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("patientDto", new PatientFormDto());
        return "admin/patient-create";
    }

    @PostMapping("/create")
    public String createSubmit(@Valid @ModelAttribute("patientDto") PatientFormDto dto,
                               BindingResult result,
                               RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) return "admin/patient-create";
        patientService.create(dto);
        redirectAttrs.addFlashAttribute("success",
                "Пациент «" + dto.getFirstName() + " " + dto.getLastName() + "» добавлен");
        return "redirect:/admin/patients";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Patient p = patientService.findById(id);
        PatientFormDto dto = new PatientFormDto();
        dto.setFirstName(p.getFirstName());
        dto.setLastName(p.getLastName());
        dto.setDob(p.getDob());
        dto.setGender(p.getGender());
        dto.setEmail(p.getEmail());
        dto.setPhone(p.getPhone());
        dto.setAddress(p.getAddress());
        model.addAttribute("patientDto", dto);
        model.addAttribute("patientId", id);
        return "admin/patient-edit";
    }

    @PostMapping("/{id}/edit")
    public String editSubmit(@PathVariable Long id,
                             @Valid @ModelAttribute("patientDto") PatientFormDto dto,
                             BindingResult result, Model model,
                             RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            model.addAttribute("patientId", id);
            return "admin/patient-edit";
        }
        patientService.update(id, dto);
        redirectAttrs.addFlashAttribute("success", "Данные пациента обновлены");
        return "redirect:/admin/patients";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        patientService.delete(id);
        redirectAttrs.addFlashAttribute("success", "Пациент удалён");
        return "redirect:/admin/patients";
    }
}
