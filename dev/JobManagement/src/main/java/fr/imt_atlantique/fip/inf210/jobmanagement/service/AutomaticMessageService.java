package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.Application;
import fr.imt_atlantique.fip.inf210.jobmanagement.entity.JobOffer;

public interface AutomaticMessageService {

    int sendAutomaticMessagesForNewOffer(JobOffer jobOffer);

    int sendAutomaticMessagesForNewApplication(Application application);
}
