package ie.ianduffy.carcloud.web.munic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.javadocmd.simplelatlng.LatLng;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import org.joda.time.DateTime;

import java.util.List;

import lombok.Data;

@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackDTO {

    @ApiModelProperty
    private Long deviceId;

    @ApiModelProperty
    private List<FieldDTO> fields;

    @ApiModelProperty
    private LatLng location;

    @ApiModelProperty
    private DateTime receivedAt;

    @ApiModelProperty
    private DateTime recordedAt;

}
