package ie.ianduffy.carcloud.service;

import ie.ianduffy.carcloud.domain.Alert;
import ie.ianduffy.carcloud.domain.AlertFieldWrapper;
import ie.ianduffy.carcloud.repository.AlertRepository;
import ie.ianduffy.carcloud.repository.FieldRepository;
import ie.ianduffy.carcloud.repository.RestrictedRepository;
import ie.ianduffy.carcloud.web.dto.AlertDTO;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 * Service class for managing tracks.
 */
@Service
@Transactional
public class AlertService extends AbstractRestrictedService<Alert, Long, AlertDTO> {

    @Inject
    private AlertRepository alertRepository;

    @Inject
    private FieldRepository fieldRepository;

    public Alert addField(Long alertId, AlertFieldWrapper field) {
        Alert alert = findOneForCurrentUser(alertId);
        alert.getFields().add(field);
        alertRepository.save(alert);
        return alert;
    }

    @Transactional(readOnly = true)
    public List<Alert> findAllForCurrentUserWithDevice() {
        List<Alert> alerts = findAllForCurrentUser();
        for (Alert alert : alerts) Hibernate.initialize(alert.getDevice());
        return alerts;
    }

    @Transactional(readOnly = true)
    public Alert findOneForCurrentUserWithDevice(Long id) {
        Alert alert = findOneForCurrentUser(id);
        Hibernate.initialize(alert.getDevice());
        return alert;
    }

    @Transactional(readOnly = true)
    public List<AlertFieldWrapper> getFields(Long id) {
        Alert alert = findOneForCurrentUser(id);
        List<AlertFieldWrapper> fields = alert.getFields();
        Hibernate.initialize(fields);
        return fields;
    }

    @Override
    protected RestrictedRepository<Alert, Long> getRepository() {
        return alertRepository;
    }

    public void removeAlertField(Long fieldId) {
        fieldRepository.delete(fieldId);
    }

    public Alert update(AlertDTO alertDTO) {
        Alert alert = findOneForCurrentUser(alertDTO.getId());
        alert.getFields().clear();
        alert.getFields().addAll(alertDTO.getFields());
        Hibernate.initialize(alert.getDevice());
        return super.update(alertDTO, alert);
    }
}
