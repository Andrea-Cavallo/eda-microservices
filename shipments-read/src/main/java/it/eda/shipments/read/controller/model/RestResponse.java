package it.eda.shipments.read.controller.model;


import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RestResponse<T> implements Serializable, MarkerInterface {

	@Serial
	private static final long serialVersionUID = 6607754259039613847L;
	private transient T output;
	private Map<String, String> descrizioneErrore;
	private Map<String,Long> codiceErrore;

}
