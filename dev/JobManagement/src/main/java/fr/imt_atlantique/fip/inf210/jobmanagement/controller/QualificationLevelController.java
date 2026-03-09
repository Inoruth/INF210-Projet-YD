package fr.imt_atlantique.fip.inf210.jobmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.*;

@Controller
public class QualificationLevelController {
    
    @Autowired
    private QualificationLevelService qualificationLevelService;

    @GetMapping("/allqualifications")
    public ModelAndView getAllQualificationLevels(){        
        ModelAndView mav = new ModelAndView("allqualificationlevelstab.html");
        mav.addObject("qualificationLevels", qualificationLevelService.getAllQualificationLevels());

        return mav;
    }
    
}
