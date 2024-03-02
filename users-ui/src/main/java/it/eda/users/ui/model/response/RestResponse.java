package it.eda.users.ui.model.response;


import it.eda.users.ui.model.Dto;
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
public class RestResponse<T> implements Serializable, Dto {

	@Serial
	private static final long serialVersionUID = 6607754259039613847L;
	private transient T output;
	private Map<String, String> descrizioneErrore;
	private Map<String,Long> codiceErrore;

}
