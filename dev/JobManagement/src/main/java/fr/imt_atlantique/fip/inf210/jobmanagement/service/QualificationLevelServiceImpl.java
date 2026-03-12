package fr.imt_atlantique.fip.inf210.jobmanagement.service;
import org.springframework.stereotype.Service;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.QualificationLevel;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.QualificationLevelRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;  

@Service
public class QualificationLevelServiceImpl implements QualificationLevelService {
    
    @Autowired
    private QualificationLevelRepository qualificationLevelRepository;

    @Override
    public List<QualificationLevel> getAllQualificationLevels() {
        return qualificationLevelRepository.findAll();
    }
}
