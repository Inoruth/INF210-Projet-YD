package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.MessageToOffer;
import fr.imt_atlantique.fip.inf210.jobmanagement.repository.MessageToOfferJpaRepository;

@Service
public class MessageToOfferServiceImpl implements MessageToOfferService {

    private final MessageToOfferJpaRepository messageToOfferRepository;

    public MessageToOfferServiceImpl(MessageToOfferJpaRepository messageToOfferRepository) {
        this.messageToOfferRepository = messageToOfferRepository;
    }

    @Override
    public MessageToOffer save(MessageToOffer messageToOffer) {
        return messageToOfferRepository.save(messageToOffer);
    }

    @Override
    public Optional<MessageToOffer> findByJobOfferAndApplication(Integer jobOfferId, Integer applicationId) {
        return messageToOfferRepository.findByJobOfferIdAndApplicationId(jobOfferId, applicationId);
    }

    @Override
    public List<MessageToOffer> findByCompanyId(Integer companyId) {
        return messageToOfferRepository.findByJobOfferCompanyIdOrderByPublicationdateDesc(companyId);
    }

    @Override
    public List<MessageToOffer> findByCandidateId(Integer candidateId) {
        return messageToOfferRepository.findByApplicationCandidateIdOrderByPublicationdateDesc(candidateId);
    }
}
