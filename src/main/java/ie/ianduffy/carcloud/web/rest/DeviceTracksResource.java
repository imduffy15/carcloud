package ie.ianduffy.carcloud.web.rest;

import com.codahale.metrics.annotation.Timed;

import ie.ianduffy.carcloud.assembler.TrackDTOAssembler;
import ie.ianduffy.carcloud.domain.Track;
import ie.ianduffy.carcloud.web.dto.TrackDTO;
import ie.ianduffy.carcloud.service.DeviceService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

/**
 * REST controller for managing Device.
 */
@RestController
@RequestMapping("/app/rest/devices/{device_id}/tracks")
public class DeviceTracksResource {

    @Inject
    private DeviceService deviceService;

    @Inject
    private TrackDTOAssembler trackDTOAssembler;

    @RequestMapping(value = "/{index}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> get(@PathVariable("device_id") Long deviceId,
                                 @PathVariable("index") int index) {
        return new ResponseEntity<>(deviceService.getTrack(deviceId, index), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getAll(@PathVariable("device_id") Long deviceId) {
        List<TrackDTO> tracks = new ArrayList<>();
        for (Track track : deviceService.getTracks(deviceId)) {
            tracks.add(trackDTOAssembler.toResource(track));
        }
        return new ResponseEntity<>(tracks, HttpStatus.OK);
    }
}