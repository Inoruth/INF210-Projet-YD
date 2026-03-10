package fr.imt_atlantique.fip.inf210.jobmanagement.service;

import java.util.List;
import java.util.Optional;

import fr.imt_atlantique.fip.inf210.jobmanagement.entity.MessageToOffer;

public interface MessageToOfferService {

    MessageToOffer save(MessageToOffer messageToOffer);

    Optional<MessageToOffer> findByJobOfferAndApplication(Integer jobOfferId, Integer applicationId);

    List<MessageToOffer> findByCompanyId(Integer companyId);

    List<MessageToOffer> findByCandidateId(Integer candidateId);
}
