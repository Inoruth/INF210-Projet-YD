package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import java.util.List;
import java.util.Optional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.MessageToApplication;

public interface MessageToApplicationService {

    MessageToApplication save(MessageToApplication messageToApplication);

    Optional<MessageToApplication> findByApplicationAndJobOffer(Integer applicationId, Integer jobOfferId);

    List<MessageToApplication> findByCompanyId(Integer companyId);

    List<MessageToApplication> findByCandidateId(Integer candidateId);
}
