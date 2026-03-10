package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.MessageToApplication;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.MessageToApplicationJpaRepository;

@Service
public class MessageToApplicationServiceImpl implements MessageToApplicationService {

    private final MessageToApplicationJpaRepository messageToApplicationRepository;

    public MessageToApplicationServiceImpl(MessageToApplicationJpaRepository messageToApplicationRepository) {
        this.messageToApplicationRepository = messageToApplicationRepository;
    }

    @Override
    public MessageToApplication save(MessageToApplication messageToApplication) {
        return messageToApplicationRepository.save(messageToApplication);
    }

    @Override
    public Optional<MessageToApplication> findByApplicationAndJobOffer(Integer applicationId, Integer jobOfferId) {
        return messageToApplicationRepository.findByApplicationIdAndJobOfferId(applicationId, jobOfferId);
    }

    @Override
    public List<MessageToApplication> findByCompanyId(Integer companyId) {
        return messageToApplicationRepository.findByJobOfferCompanyIdOrderByPublicationdateDesc(companyId);
    }

    @Override
    public List<MessageToApplication> findByCandidateId(Integer candidateId) {
        return messageToApplicationRepository.findByApplicationCandidateIdOrderByPublicationdateDesc(candidateId);
    }
}
