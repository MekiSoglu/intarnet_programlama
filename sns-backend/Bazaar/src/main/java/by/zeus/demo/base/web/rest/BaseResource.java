package by.zeus.demo.base.web.rest;

import by.zeus.demo.base.domain.BaseEntity;
import by.zeus.demo.base.facade.BaseFacade;
import by.zeus.demo.base.web.dto.BaseDTO;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import jakarta.transaction.NotSupportedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public abstract class BaseResource<D extends BaseDTO, E extends BaseEntity> {

    protected final Logger log = LoggerFactory.getLogger(getLoggerClass());
    private final BaseFacade<D, E> facade;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public BaseResource(final BaseFacade<D, E> facade) {
        this.facade = facade;
    }

    public abstract Class<?> getLoggerClass();

    @PostMapping("")
    @CrossOrigin(origins = {"http://localhost:4401", "http://localhost:4200"})
    public ResponseEntity<D> create(@RequestBody final D dto) throws URISyntaxException, NotSupportedException {
        log.debug("REST request to save  : {}", dto);
        if (dto.getId() != null) {
            throw new IllegalArgumentException("A new entity cannot already have an ID");
        }

        final D result = facade.save(dto);

        return ResponseEntity
                .created(new URI("/api/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, getEntityClassName(), result.getId().toString()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<D> update(
            @PathVariable(value = "id", required = false) final Long id,
            @RequestBody final D dto
    ) throws URISyntaxException {
        log.debug("REST request to update  : {}, {}", id, dto);

        if (!Objects.equals(id, dto.getId())) {
            throw new IllegalArgumentException("Invalid ID");
        }

        final D result = facade.update(dto);
        return ResponseEntity
                .ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, getEntityClassName(), dto.getId().toString()))
                .body(result);
    }

    @PatchMapping(value = "/{id}", consumes = {"application/json", "application/merge-patch+json"})
    public ResponseEntity<D> partialUpdate(
            @PathVariable(value = "id", required = false) final Long id,
            @RequestBody final D dto
    ) throws URISyntaxException {
        log.debug("REST request to partial update  partially : {}, {}", id, dto);

        if (!Objects.equals(id, dto.getId())) {
            throw new IllegalArgumentException("Invalid ID");
        }

        final Optional<D> result = facade.partialUpdate(dto);

        return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, false, getEntityClassName(), dto.getId().toString())
        );
    }

    @GetMapping("/all")
    @CrossOrigin(origins = {"http://localhost:4401", "http://localhost:4200"})

    public List<D> getAll() {
        log.debug("REST request to get all s");
        return facade.findAll();
    }

    @PostMapping("/example-like")
    @CrossOrigin(origins = {"http://localhost:4401", "http://localhost:4200"})

    public List<D> getAllByExampleLike(@RequestBody final D dto) {
        log.debug("REST request to get a page of Operations");
        return facade.findAllByExampleLike(dto);
    }

    @GetMapping("/{id}")
    @CrossOrigin(origins = {"http://localhost:4401", "http://localhost:4200"})

    public ResponseEntity<D> get(@PathVariable final Long id) {
        log.debug("REST request to get  : {}", id);
        final Optional<D> dto = facade.findOne(id);
        return ResponseUtil.wrapOrNotFound(dto);
    }

    @GetMapping("/{id}/eager")
    @CrossOrigin(origins = {"http://localhost:4401", "http://localhost:4200"})

    public ResponseEntity<D> getWithEagerRelations(@PathVariable final Long id, @RequestParam final Set<String> relations) {
        log.debug("REST request to get  : {}", id);
        if (CollectionUtils.isEmpty(relations)) {
            throw new IllegalArgumentException("Relations cannot be empty");
        }
        final Optional<D> dto = facade.findOneWithEagerRelations(id, relations);
        return ResponseUtil.wrapOrNotFound(dto);
    }

    @DeleteMapping("/{id}")
    @CrossOrigin(origins = {"http://localhost:4401", "http://localhost:4200"})

    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        log.debug("REST request to delete  : {}", id);
        facade.delete(id);
        return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, getEntityClassName(), id.toString()))
                .build();
    }

    public String getApplicationName() {
        return applicationName;
    }

    public BaseFacade<D, E> getFacade() {
        return facade;
    }

    protected String getEntityClassName() {
        return getFacade().getEntityClassName();
    }

    protected Class<E> getEntityClass() {
        return getFacade().getEntityClass();
    }

    protected Class<D> getDtoClass() {
        return getFacade().getDtoClass();
    }
}
