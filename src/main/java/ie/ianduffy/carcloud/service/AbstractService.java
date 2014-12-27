package ie.ianduffy.carcloud.service;

import ie.ianduffy.carcloud.domain.AbstractAuditingEntity;
import ie.ianduffy.carcloud.web.dto.AbstractAuditingEntityDTO;

import org.dozer.Mapper;
import org.hibernate.StaleStateException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

@Transactional
public abstract class AbstractService<T extends AbstractAuditingEntity, ID extends Serializable, DTO extends AbstractAuditingEntityDTO> {

    @Inject
    private Mapper mapper;

    public T create(DTO dto, T entity) {
        if (getRepository().findOne((ID) dto.getId()) != null) {
            throw new EntityExistsException();
        }
        mapper.map(dto, entity);
        return getRepository().save(entity);
    }

    public void delete(ID id) {
        findOne(id);
        getRepository().delete(id);
    }

    @Transactional(readOnly = true)
    public List<T> findAll() {
        return getRepository().findAll();
    }

    @Transactional(readOnly = true)
    public T findOne(ID id) {
        T entity = getRepository().findOne(id);
        if (entity == null) {
            throw new EntityNotFoundException();
        }
        return entity;
    }

    abstract protected JpaRepository<T, ID> getRepository();

    public T update(DTO dto, T entity) {
        if (dto.getVersion() != entity.getVersion() && dto.getVersion() != -1) {
            throw new StaleStateException(
                "Unexpected version. Got " + dto.getVersion() + " expected " + entity
                    .getVersion());
        }

        mapper.map(dto, entity);
        return getRepository().save(entity);
    }
}
