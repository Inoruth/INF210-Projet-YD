package fr.imt_atlantique.fip.inf210.jobmanagement.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import fr.imt_atlantique.fip.inf210.jobmanagement.service.*;

@Controller
public class SectorController {
    
    @Autowired
    private SectorService sectorService;

    @GetMapping("/allsectors")
    public ModelAndView getAllSectors(){
        ModelAndView mav = new ModelAndView("allsectorstab.html");
        mav.addObject("sectors", sectorService.getAllSectors());
        return mav;
    }
    
}
