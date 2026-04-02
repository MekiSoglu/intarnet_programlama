package by.zeus.demo.tablemap.web.rest;

import by.zeus.demo.base.facade.BaseFacade;
import by.zeus.demo.base.web.rest.BaseResource;
import by.zeus.demo.tablemap.domain.TableMapEntity;
import by.zeus.demo.tablemap.web.dto.TableMapDTO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/Map")
@CrossOrigin( {"http://localhost:4401", "http://localhost:4200"} )
public class TableMapController  {

}
